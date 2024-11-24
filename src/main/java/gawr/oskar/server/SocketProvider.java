package gawr.oskar.server;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketProvider {
    private ServerSocket serverSocket;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
            justification = "Not sure how to resolve this one.")
    public ServerSocket createServerSocket(final int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        return serverSocket;
    }

    public Socket acceptClient() throws IOException {
        if (serverSocket != null) {
            return serverSocket.accept();
        }
        throw new IllegalStateException("Server socket is not initialized");
    }

    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
