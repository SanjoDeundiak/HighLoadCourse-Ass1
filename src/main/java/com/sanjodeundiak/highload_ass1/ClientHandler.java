package com.sanjodeundiak.highload_ass1;

import com.sanjodeundiak.highload_ass1.configs.Settings;
import com.sanjodeundiak.highload_ass1.controllers.IController;
import com.sanjodeundiak.highload_ass1.utils.HttpRequest;
import com.sanjodeundiak.highload_ass1.utils.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.ws.rs.HttpMethod;

/**
 * Created by sanjo on 10/8/16.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static String CRLF = "\r\n";
    private Logger logger;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.logger = LoggerFactory.getLogger(ClientHandler.class);
    }

    public void run() {
        if (clientSocket == null)
            throw new NullPointerException("Client socket can't be null");

        logger.debug("Handling client socket: {}", clientSocket.getInetAddress());

        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            while (true) {
                String line = br.readLine();

                if (line == null || line.equals("") || line.equals(CRLF)) {
                    break;
                }

                StringTokenizer tokenizer = new StringTokenizer(line);
                String method = tokenizer.nextToken();

                if (method.equals(HttpMethod.GET)) {
                    String resourceName = tokenizer.nextToken();

                    logger.info("{} Requesting GET {}", clientSocket.getInetAddress(), resourceName);

                    HttpResponseWriter responseWriter = new HttpResponseWriter(clientSocket);
                    responseWriter.setServerLine("My invalid server");

                    // check for static resource
                    FileInputStream fileInputStream;
                    IController controller;
                    if ((fileInputStream = getStaticResourceStream(resourceName)) != null) {
                        responseWriter.setStatusLine("HTTP/1.1 200 OK" + CRLF);
                        responseWriter.setContentTypeLine("Content-type: " + contentType(resourceName) + CRLF);
                        responseWriter.setBody(fileInputStream);
                    }
                    else if ((controller = getController(resourceName)) != null) {
                        responseWriter.setStatusLine("HTTP/1.1 200 OK" + CRLF);
                        responseWriter.setContentTypeLine("Content-type: " + contentType(".html") + CRLF);
                        controller.handleQuery(new HttpRequest(HttpMethod.GET, resourceName), responseWriter);
                    }
                    else {
                        responseWriter.setStatusLine("HTTP/1.1 404 Not Found" + CRLF);
                        responseWriter.setContentTypeLine("Content-type: " + contentType(".html") + CRLF);
                        responseWriter.setBody("<HTML>"
                                + "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"
                                + "<BODY>404 Not Found</BODY>"
                                + "</HTML>");
                    }

                    responseWriter.write(output);
                }
            }

            br.close();
            clientSocket.close();
            output.close();
            input.close();
        }
        catch (IOException ex) {
            logger.error("Exception: {}", ex);
        }

        long time = System.currentTimeMillis();
        logger.debug("Request for {} processed: {}", clientSocket.getInetAddress(), time);
    }

    private FileInputStream getStaticResourceStream(String resourceName) {
        String filePath = Settings.getConfig().getString("public_resources_path") + resourceName;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
        }
        catch (FileNotFoundException ex) {}

        return fileInputStream;
    }

    private IController getController(String resourceName) {
        return Router.getControllerForRoute(resourceName);
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")
                || fileName.endsWith(".txt")) {
            return "text/html";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}
