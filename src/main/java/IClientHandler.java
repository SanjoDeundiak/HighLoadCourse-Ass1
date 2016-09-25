import java.io.IOException;
import java.net.Socket;

/**
 * Created by sanjo on 9/25/16.
 */
public interface IClientHandler {
    void handleClient(Socket clientSocket) throws IOException;
}
