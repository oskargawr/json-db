package gawr.oskar.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketProviderTest {
    private SocketProvider socketProvider;
    private ServerSocket serverSocket;

    @BeforeEach
    void setUp() {
        socketProvider = new SocketProvider();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    @Test
    void testCreateServerSocket() throws IOException {
        serverSocket = socketProvider.createServerSocket(0);
        assertNotNull(serverSocket, "ServerSocket should not be null");
        assertTrue(serverSocket.getLocalPort() > 0, "ServerSocket should be created on a valid port");

        serverSocket.close();
    }

    @Test
    void testAcceptClientThrowsExceptionWhenNotInitialized() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            socketProvider.acceptClient();
        });
        assertEquals("Server socket is not initialized", exception.getMessage(), "Should throw exception if server socket is not initialized");
    }

    @Test
    void testAcceptClient() throws IOException, InterruptedException {
        serverSocket = socketProvider.createServerSocket(0);

        Thread clientThread = new Thread(() -> {
            try {
                Thread.sleep(100);
                new Socket("localhost", serverSocket.getLocalPort());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        clientThread.start();

        Socket clientSocket = socketProvider.acceptClient();

        assertNotNull(clientSocket, "Client socket should not be null when accepted");
        assertTrue(clientSocket.isConnected(), "Client socket should be connected");

        clientSocket.close();
        clientThread.join();
    }

    @Test
    void testCloseServerSocket() throws IOException {
        serverSocket = socketProvider.createServerSocket(0);

        socketProvider.close();

        assertTrue(serverSocket.isClosed(), "Server socket should be closed after calling close()");
    }
}
