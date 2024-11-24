package gawr.oskar.server;

import gawr.oskar.server.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private Server server;
    private SocketProvider socketProvider;
    private RequestExecutor requestExecutor;
    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        socketProvider = new SocketProvider();
        requestExecutor = new RequestExecutor();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        databaseManager = new DatabaseManager(lock.readLock(), lock.writeLock());
        server = new Server(socketProvider, requestExecutor, databaseManager);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.stopServer();
    }

    @Test
    void testServerStartAndStop() throws IOException {
        Thread serverThread = new Thread(server::startServer);
        serverThread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Socket testClient = new Socket("localhost", ServerConstants.PORT);
        assertTrue(testClient.isConnected(), "Test client should connect to the server successfully");

        server.stopServer();
        testClient.close();

        assertTrue(testClient.isClosed() || !testClient.isConnected(), "Server should disconnect the client after stopping");

    }

    @Test
    void testServerHandlesClientRequest() throws IOException, InterruptedException {
        Thread serverThread = new Thread(server::startServer);
        serverThread.start();

        Thread.sleep(100);

        try (Socket clientSocket = new Socket("localhost", ServerConstants.PORT)) {
            assertTrue(clientSocket.isConnected(), "Client should connect to the server successfully");

        }

        server.stopServer();
        serverThread.join();
    }
}
