package com.sanjodeundiak.highload_ass1.dispatchers;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by sanjo on 9/25/16.
 */
public interface IClientDispatcher {
    void dispatchClient(Socket clientSocket) throws IOException;
}
