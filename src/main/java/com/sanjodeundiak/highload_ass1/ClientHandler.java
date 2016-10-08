package com.sanjodeundiak.highload_ass1;

import com.sanjodeundiak.highload_ass1.dispatchers.SingleThreadedDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sanjo on 10/8/16.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        if (clientSocket == null)
            throw new NullPointerException("Client socket can't be null");

        Logger logger = LoggerFactory.getLogger(SingleThreadedDispatcher.class);
        logger.debug("Handling client socket: {}", clientSocket.getInetAddress());

        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            try {
                output.write(("HTTP/1.1 200 OK\n\n<html><body>" +
                        "Test" +
                        "</body></html>").getBytes());
            } catch (IOException e) {
                logger.error("Error sending data to {}: {}", clientSocket.getInetAddress(), e);
            } finally {
                output.close();
            }

            input.close();
        }
        catch (IOException ex) {
            logger.error("Exception: {}", ex);
        }

        long time = System.currentTimeMillis();
        logger.debug("Request for {} processed: {}", clientSocket.getInetAddress(), time);
    }
}
