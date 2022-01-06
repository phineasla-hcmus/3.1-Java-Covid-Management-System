package com.seasidechachacha.payment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.seasidechachacha.common.payment.ErrorResponse;
import com.seasidechachacha.common.payment.ErrorResponseType;
import com.seasidechachacha.common.payment.GetUserRequest;
import com.seasidechachacha.common.payment.NewAdminRequest;
import com.seasidechachacha.common.payment.NewUserRequest;
import com.seasidechachacha.common.payment.PaymentRequest;
import com.seasidechachacha.common.payment.PaymentResponse;
import com.seasidechachacha.common.payment.UserResponse;
import com.seasidechachacha.payment.database.BankDao;
import com.seasidechachacha.payment.models.BankAccount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receive a request and answer with a response, then close the socket
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);
    private final Socket socket;
    private final ObjectOutputStream ostream;
    private final ObjectInputStream istream;

    public ClientHandler(Socket clientSocket) throws IOException {
        socket = clientSocket;
        ostream = new ObjectOutputStream(socket.getOutputStream());
        istream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        // try-with-resource will auto-close the socket, so do not reuse it
        // ObjectOutputStream must be before the ObjectInputStream
        // https://stackoverflow.com/a/27736470/12405558
        try (ostream; istream) {
            Serializable raw = (Serializable) istream.readObject();
            logger.trace(raw);
            if (raw instanceof PaymentRequest) {
                handlePaymentRequest((PaymentRequest) raw);
            } else if (raw instanceof GetUserRequest) {
                handleGetUserRequest((GetUserRequest) raw);
            } else if (raw instanceof NewUserRequest) {
                handleNewUserRequest((NewUserRequest) raw);
            } else if (raw instanceof NewAdminRequest) {
                handleNewAdminRequest((NewAdminRequest) raw);
            } else {
                responseError(ErrorResponseType.INVALID_REQUEST);
            }
        } catch (IOException | ClassNotFoundException e) {
            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            logger.warn(address, e);
        }
    }

    private void handleNewUserRequest(NewUserRequest req) throws IOException {
        boolean result = BankDao.register(req.getUserId(), req.getDeposit());
        if (result) {
            UserResponse res = new UserResponse(req.getUserId(), req.getDeposit());
            ostream.writeObject(res);
        } else {
            // If SQL fail, respond error
            responseError(ErrorResponseType.ID_EXISTED);
        }
    }

    private void handleNewAdminRequest(NewAdminRequest req) throws IOException {
        boolean result = BankDao.registerAdmin(req.getUserId());
        if (result) {
            Admin.set(new BankAccount(req.getUserId(), 0));
            UserResponse res = new UserResponse(req.getUserId(), 0);
            ostream.writeObject(res);
        } else {
            // If SQL fail, respond error
            responseError(ErrorResponseType.ID_EXISTED);
        }
    }

    private void handleGetUserRequest(GetUserRequest req) throws IOException {
        BankAccount acc = BankDao.get(req.getUserId());
        // If SQL fail, respond error
        if (acc != null) {
            double balance = acc.getBalance();
            UserResponse res = new UserResponse(req.getUserId(), balance);
            ostream.writeObject(res);
        } else {
            responseError(ErrorResponseType.ID_NOT_FOUND);
        }
    }

    private void handlePaymentRequest(PaymentRequest req) throws IOException {
        BankAccount user = BankDao.get(req.getUserId());
        double amount = req.getTotal();
        if (amount > user.getBalance()) {
            responseError(ErrorResponseType.INSUFFICIENT_FUNDS);
            return;
        }
        long transactionId = BankDao.transferToAdmin(req.getUserId(), amount);
        if (transactionId == 0) {
            // If SQL fail, respond error
            responseError(ErrorResponseType.ID_NOT_FOUND);
        }
        PaymentResponse res = new PaymentResponse(transactionId, req.getTotal());
        ostream.writeObject(res);
    }

    private void responseError(ErrorResponseType type) throws IOException {
        ostream.writeObject(new ErrorResponse(type));
    }

    @SuppressWarnings("unused")
    private void responseError(ErrorResponseType type, String message) throws IOException {
        ostream.writeObject(new ErrorResponse(type, message));
    }
}
