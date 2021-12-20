package com.seasidechachacha.payment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receive a request and answer with a respone, then close the socket
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);
    private Gson gson;
    private Socket socket;

    public ClientHandler(Socket clientSocket) {
        gson = new Gson();
        socket = clientSocket;
    }

    @Override
    public void run() {
        // try-with-resource will auto-close the socket, so do not reuse it
        try (InputStream in = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String raw = br.readLine();
            // Client must have close the socket
            if (raw == null) {
                return;
            }
            logger.trace(raw);
            
        } catch (Exception e) {
            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            logger.warn(address, e);
        }
    }
}
