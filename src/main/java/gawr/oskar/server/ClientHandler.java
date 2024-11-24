package gawr.oskar.server;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gawr.oskar.server.commands.CommandController;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import gawr.oskar.server.database.DatabaseManager;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Map<String, Object> database;
    private final Gson gson;
    private final DatabaseManager databaseManager;
    private final Server server;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "Need to perform commands on actual database, a not copy.")
    public ClientHandler(final Socket clientSocket,
                         final Map<String, Object> database,
                         final Gson gson,
                         final DatabaseManager databaseManager,
                         final Server server) {
        this.clientSocket = clientSocket;
        this.database = database;
        this.gson = gson;
        this.databaseManager = databaseManager;
        this.server = server;
    }

    @Override
    public void run() {
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

            String requestJson = input.readUTF();
            Map<String, Object> request = gson.fromJson(requestJson, Map.class);
            System.out.println("Received: " + requestJson);

            if (ServerConstants.STOP_STRING.equals(request.get("type"))) {
                output.writeUTF(gson.toJson(Map.of("response", "Server shutting down")));
                output.flush();
                server.stopServer();
                return;
            }

            CommandController controller = new CommandController();
            controller.setCommand(request, database, databaseManager.getWriteLock());
            String response = controller.executeCommand();
            System.out.println("Response: " + response);
            output.writeUTF(response);
            output.flush();

            if ("set".equals(request.get("type")) || "delete".equals(request.get("type"))) {
                databaseManager.saveDatabaseToFile(database, ServerConstants.DB_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
