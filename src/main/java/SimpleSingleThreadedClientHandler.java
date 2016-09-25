import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sanjo on 9/25/16.
 */
public class SimpleSingleThreadedClientHandler implements IClientHandler {
    public SimpleSingleThreadedClientHandler() {

    }

    public void handleClient(Socket clientSocket) throws IOException {
        if (clientSocket == null)
            throw new NullPointerException("Client socket can't be null");

        Logger logger = LoggerFactory.getLogger(SimpleSingleThreadedClientHandler.class);
        logger.debug("Handling client socket: {}", clientSocket.getInetAddress());

        InputStream input  = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();
        long time = System.currentTimeMillis();

        try {
            output.write(("HTTP/1.1 200 OK\n\n<html><body>" +
                    "Singlethreaded Server: " +
                    time +
                    "</body></html>").getBytes());
        }
        catch (IOException e) {
            logger.error("Error sending data to {}: {}", clientSocket.getInetAddress(), e);
        }
        finally {
            output.close();
        }

        input.close();
        logger.debug("Request for {} processed: {}", clientSocket.getInetAddress(), time);
    }
}
