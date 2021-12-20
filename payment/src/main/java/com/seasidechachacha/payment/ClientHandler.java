package com.seasidechachacha.payment;

import java.net.Socket;

import com.google.gson.Gson;

public class ClientHandler implements Runnable {
    private Gson gson;
    private Socket socket;

    public ClientHandler(Socket clientSocket) {
        gson = new Gson();
        socket = clientSocket;
    }

    @Override
    public void run() {
        
    }
}
