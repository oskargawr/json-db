package gawr.oskar.server;

import com.google.gson.Gson;
import gawr.oskar.server.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.jupiter.api.Assertions.*;

class ClientHandlerTest {
    private Map<String, Object> database;
    private Gson gson;
    private DatabaseManager databaseManager;
    private Server server;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();
        gson = new Gson();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        databaseManager = new DatabaseManager(lock.readLock(), lock.writeLock());
        server = new Server(new SocketProvider(), new RequestExecutor(), databaseManager);
        Thread serverThread = new Thread(server::startServer);
        serverThread.start();
    }

    @AfterEach
    void tearDown() {
        server.stopServer();
    }

    @Test
    void testValidRequest() throws IOException {
        try (Socket clientSocket = new Socket("localhost", ServerConstants.PORT);
             PipedOutputStream clientOutputStream = new PipedOutputStream();
             PipedInputStream serverInputStream = new PipedInputStream(clientOutputStream)) {

            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            output.writeUTF("{\"type\": \"set\", \"key\": \"testKey\", \"value\": \"testValue\"}");
            output.flush();

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            String response = input.readUTF();

            assertNotNull(response);
            assertTrue(response.contains("OK"), "Expected success message in response");
        }
    }

    @Test
    void testStopRequest() throws IOException {
        try (Socket clientSocket = new Socket("localhost", ServerConstants.PORT);
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {

            output.writeUTF("{\"type\": \"exit\"}");
            output.flush();

            String response = input.readUTF();

            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("Server shutting down"), "Expected response to indicate server shutdown");
        }
    }

    @Test
    void testDeleteNonExistingKeyCommandSavesDatabase() throws IOException {
        try (Socket clientSocket = new Socket("localhost", ServerConstants.PORT);
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {

            output.writeUTF("{\"type\": \"delete\", \"key\": \"testKey\"}");
            output.flush();

            String response = input.readUTF();

            assertTrue(response.contains("ERROR"), "Expected success message in response");
        }
    }

}
