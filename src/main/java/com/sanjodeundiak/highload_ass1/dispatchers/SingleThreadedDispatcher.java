package com.sanjodeundiak.highload_ass1.dispatchers;

import com.sanjodeundiak.highload_ass1.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by sanjo on 9/25/16.
 */
public class SingleThreadedDispatcher implements IClientDispatcher {
    private Logger logger;

    public SingleThreadedDispatcher() {
        this.logger = LoggerFactory.getLogger(SingleThreadedDispatcher.class);
    }

    public void dispatchClient(Socket clientSocket) throws IOException {
        logger.info("Dispatching client. Timestamp: {}", System.currentTimeMillis());
        new ClientHandler(clientSocket).run();
    }
}
