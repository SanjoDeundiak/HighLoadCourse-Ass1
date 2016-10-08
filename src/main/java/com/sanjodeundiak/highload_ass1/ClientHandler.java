package com.sanjodeundiak.highload_ass1;

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

    private static String PUBLIC_RESOURCES_PATH = "resources/static/public";
    private static int FILE_BUFFER_SIZE = 1024;

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

                if (line.equals("") || line.equals(CRLF)) {
                    break;
                }

                StringTokenizer tokenizer = new StringTokenizer(line);
                String method = tokenizer.nextToken();

                if (method.equals(HttpMethod.GET)) {
                    String resourceName = tokenizer.nextToken();

                    logger.info("{} Requesting GET {}", clientSocket.getInetAddress(), resourceName);

                    String filePath = PUBLIC_RESOURCES_PATH + resourceName;

                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(filePath);
                    }
                    catch (FileNotFoundException ex) {}

                    String serverLine = "My invalid server";
                    String statusLine;
                    String contentTypeLine;
                    String contentLengthLine = null;
                    String entityBody = "";
                    if (fileInputStream != null) {
                        statusLine = "HTTP/1.1 200 OK" + CRLF;
                        contentTypeLine = "Content-type: " + contentType(resourceName)
                                + CRLF;
                        contentLengthLine = "Content-Length: "
                                + Integer.toString(fileInputStream.available()) + CRLF;
                    } else {
                        statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                        contentTypeLine = "text/html";
                        entityBody = "<HTML>"
                                + "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"
                                + "<BODY>404 Not Found</BODY>"
                                + "</HTML>";
                        contentLengthLine = "Content-Length: "
                                + Integer.toString(entityBody.getBytes().length) + CRLF;
                    }


                    // Send the status line.
                    sendBytes(statusLine.getBytes(), output);

                    // Send the server line.
                    sendBytes(serverLine.getBytes(), output);

                    // Send the content type line.
                    sendBytes(contentTypeLine.getBytes(), output);

                    // Send the Content-Length
                    sendBytes(contentLengthLine.getBytes(), output);

                    // Send a blank line to indicate the end of the header lines.
                    output.write(CRLF.getBytes());

                    // Send the entity body.
                    if (fileInputStream != null) {
                        try {
                            sendFile(fileInputStream, output);
                        }
                        catch (IOException ex) {
                            logger.error("Error reading file: {}", ex);
                        }
                        finally {
                            fileInputStream.close();
                        }
                    } else {
                        sendBytes(entityBody.getBytes(), output);
                    }
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

    private void sendFile(FileInputStream fileInputStream, OutputStream output) throws IOException {
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        int bytes = 0;

        while ((bytes = fileInputStream.read(buffer)) != -1) {
            sendBytes(buffer, bytes, output);
        }
    }

    private void sendBytes(byte[] bytes, OutputStream output) {
        sendBytes(bytes, -1, output);
    }

    private void sendBytes(byte[] bytes, int length, OutputStream output) {
        if (length == -1)
            length = bytes.length;

        try {
            output.write(bytes, 0, length);
        } catch (IOException e) {
            logger.error("Error sending data to {}: {}", clientSocket.getInetAddress(), e);
            try {
                output.close();
            }
            catch (IOException ex) {
                logger.error("Error closing output stream {}: {}", clientSocket.getInetAddress(), ex);
            }
        }
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
