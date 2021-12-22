package com.seasidechachacha.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;
import com.seasidechachacha.common.payment.GetUserRequest;
import com.seasidechachacha.common.payment.NewUserRequest;
import com.seasidechachacha.common.payment.TransactionRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receive a request and answer with a respone, then close the socket
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);
    // private Gson gson;
    private Socket socket;

    public ClientHandler(Socket clientSocket) {
        // gson = new Gson();
        socket = clientSocket;
    }

    @Override
    public void run() {
        // try (BufferedReader br = createBufferedReader();
        // PrintWriter pw = createPrintWriter()) {
        // String raw = br.readLine();
        // System.out.println(raw);
        // // Client must have close the socket
        // if (raw == null) {
        // return;
        // }
        // logger.trace(raw);

        // } catch (Exception e) {
        // InetSocketAddress address = (InetSocketAddress)
        // socket.getRemoteSocketAddress();
        // logger.warn(address, e);
        // }

        // try-with-resource will auto-close the socket, so do not reuse it
        // ObjectOutputStream must be before the ObjectInputStream
        // https://stackoverflow.com/a/27736470/12405558
        try (ObjectOutputStream ostream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream istream = new ObjectInputStream(socket.getInputStream())) {
            Object raw = istream.readObject();
            System.out.println(raw);
            logger.trace(raw);
            if (raw instanceof TransactionRequest) {
                TransactionRequest req = (TransactionRequest) raw;
            } else if (raw instanceof GetUserRequest) {
                GetUserRequest req = (GetUserRequest) raw;
            } else if (raw instanceof NewUserRequest) {
                NewUserRequest req = (NewUserRequest) raw;
            } else {

            }

        } catch (Exception e) {
            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            logger.warn(address, e);
        }
    }

    private PrintWriter createPrintWriter() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    private BufferedReader createBufferedReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
