package com.sanjodeundiak.highload_ass1.utils;

import com.sanjodeundiak.highload_ass1.configs.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sanjo on 09.10.2016.
 */
public class HttpResponseWriter {
    private static String CRLF = "\r\n";

    private Socket socket;
    private Logger logger;

    public HttpResponseWriter(Socket socket) {
        this.socket = socket;
        this.logger = LoggerFactory.getLogger(HttpResponseWriter.class);
    }

    private String serverLine;
    private String statusLine;
    private String contentTypeLine;
    private String contentLengthLine;

    private String entityBody;
    private FileInputStream inputStream;

    public void setBody(String body) {
        this.entityBody = body;
    }

    public void setBody(FileInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void sendFile(FileInputStream fileInputStream, OutputStream output) throws IOException {
        byte[] buffer = new byte[Settings.getConfig().getInt("file_buffer_size")];
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
            logger.error("Error sending data to {}: {}", socket.getInetAddress(), e);
            try {
                output.close();
            }
            catch (IOException ex) {
                logger.error("Error closing output stream {}: {}", socket.getInetAddress(), ex);
            }
        }
    }

    public void write(OutputStream output) {
        int length = 0;
        if (inputStream != null) {
            try {
                length = inputStream.available();
            }
            catch (IOException ex) { }
        }
        else if (entityBody != null) {
            length = entityBody.getBytes().length;
        }

        setContentLengthLine("Content-Length: " + Integer.toString(length) + CRLF);

        // Send the status line.
        sendBytes(statusLine.getBytes(), output);

        // Send the server line.
        sendBytes(serverLine.getBytes(), output);

        // Send the content type line.
        sendBytes(contentTypeLine.getBytes(), output);

        // Send the Content-Length
        sendBytes(contentLengthLine.getBytes(), output);

        // Send a blank line to indicate the end of the header lines.
        sendBytes(CRLF.getBytes(), output);

        // Send the entity body.
        if (inputStream != null) {
            try {
                sendFile(inputStream, output);
            }
            catch (IOException ex) {
                logger.error("Error reading file: {}", ex);
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (IOException ex) { }
            }
        } else if (entityBody != null) {
            sendBytes(entityBody.getBytes(), output);
        }
    }

    public String getServerLine() {
        return serverLine;
    }

    public void setServerLine(String serverLine) {
        this.serverLine = serverLine;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public String getContentTypeLine() {
        return contentTypeLine;
    }

    public void setContentTypeLine(String contentTypeLine) {
        this.contentTypeLine = contentTypeLine;
    }

    public String getContentLengthLine() {
        return contentLengthLine;
    }

    public void setContentLengthLine(String contentLengthLine) {
        this.contentLengthLine = contentLengthLine;
    }
}
