package com.sanjodeundiak.highload_ass1.dispatchers;

import com.sanjodeundiak.highload_ass1.ClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sanjo on 10/1/16.
 */
public class ThreadPoolDispatcher implements IClientDispatcher {
    private static int POOL_SIZE = 100;
    private ExecutorService executor;

    public ThreadPoolDispatcher() {
        super();

        executor = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public void dispatchClient(Socket clientSocket) throws IOException {
        executor.execute(new ClientHandler(clientSocket));
    }
}
