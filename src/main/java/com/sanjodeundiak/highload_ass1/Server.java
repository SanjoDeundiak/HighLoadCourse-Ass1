package com.sanjodeundiak.highload_ass1;

import com.sanjodeundiak.highload_ass1.dispatchers.IClientDispatcher;
import com.sanjodeundiak.highload_ass1.dispatchers.ThreadPoolDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sanjo on 9/25/16.
 */
public class Server implements Runnable {
    protected int port;
    protected boolean stopped = false;
    private IClientDispatcher handler;

    public Server(int port, IClientDispatcher handler) {
        this.port = port;
        this.handler = handler;
    }

    private ServerSocket serverSocket;
    public void run() {
        Logger logger = LoggerFactory.getLogger(Server.class);

        serverSocket = openSocket();

        if (serverSocket == null) {
            stop();
            return;
        }

        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                logger.debug("Received new client: {}", clientSocket.getInetAddress());
            }
            catch (IOException e) {
                if (isStopped())
                    logger.debug("Server already stopped");
                else
                    logger.error("Error while accepting client socket: {}", e);
            }

            if (clientSocket == null) {
                continue;
            }

            try {
                handler.dispatchClient(clientSocket);
            }
            catch (IOException e) {
                logger.error("Error handling clientSocket {}: {}", clientSocket.getInetAddress(), e);
            }
        }
    }

    protected ServerSocket openSocket() {
        try {
            return new ServerSocket(this.port);
        }
        catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(Server.class);
            logger.error("Error while openning socket: {}", e);
        }

        return null;
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public synchronized void stop() {
        stopped = true;

        if (serverSocket == null)
            return;

        try {
            serverSocket.close();
        }
        catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(Server.class);
            logger.error("Error while closing server socket: {}", e);
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Server.class);
        logger.debug("Starting application");

        int port = 8080;
        logger.debug("Creating server on port: {}", port);

//        Server server = new Server(port, new SingleThreadedDispatcher());
        Server server = new Server(port, new ThreadPoolDispatcher());
        server.run();
    }
}
