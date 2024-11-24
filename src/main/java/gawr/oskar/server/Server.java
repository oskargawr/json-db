package gawr.oskar.server;

import com.google.gson.Gson;
import gawr.oskar.server.database.DatabaseManager;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private static final Map<String, Object> DATABASE = new HashMap<>(1000);
    private final Gson gson = new Gson();
    private final SocketProvider socketProvider;
    private final RequestExecutor requestExecutor;
    private final DatabaseManager databaseManager;
    private boolean running = true;

    public Server(final SocketProvider socketProvider,
                  final RequestExecutor requestExecutor,
                  final DatabaseManager databaseManager) {
        this.socketProvider = socketProvider;
        this.requestExecutor = requestExecutor;
        this.databaseManager = databaseManager;
    }

    public static Server create() {
        SocketProvider socketProvider = new SocketProvider();
        RequestExecutor requestExecutor = new RequestExecutor();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        DatabaseManager databasemanager = new DatabaseManager(lock.readLock(), lock.writeLock());
        Server server = new Server(socketProvider, requestExecutor, databasemanager);
        server.init();
        return server;
    }

    private void init() {
        System.out.println("Server has started");
        databaseManager.loadDatabaseFromFile(DATABASE, ServerConstants.DB_PATH);
        startServer();
    }

    void startServer() {
        try {
            socketProvider.createServerSocket(ServerConstants.PORT);
            while (running) {
                try {
                    Socket clientSocket = socketProvider.acceptClient();
                    requestExecutor.submitTask(new ClientHandler(clientSocket, DATABASE, gson, databaseManager, this));
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server socket closed, stopping the server gracefully.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        try {
            socketProvider.close();
            requestExecutor.shutdown();
            System.out.println("Server has shut down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
