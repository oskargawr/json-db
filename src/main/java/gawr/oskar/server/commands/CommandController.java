package gawr.oskar.server.commands;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class CommandController {
    private Command command;

    public void setCommand(final Map<String, Object> request,
                           final Map<String, Object> database,
                           final Lock writeLock) {
        String type = (String) request.get("type");
        Object keyObject = request.get("key");
        List<String> keyPath;

        if (keyObject instanceof String) {
            keyPath = List.of((String) keyObject);
        } else if (keyObject instanceof Double) {
            keyPath = List.of(String.valueOf(keyObject));
        } else if (keyObject instanceof List) {
            keyPath = (List<String>) keyObject;
        } else {
            throw new IllegalArgumentException("Invalid key type: " + keyObject);
        }

        Object value = request.get("value");

        switch (type) {
            case "set" -> command = new InsertCommand(keyPath, value, database, writeLock);
            case "get" -> command = new GetCommand(keyPath, database);
            case "delete" -> command = new DeleteCommand(keyPath, database, writeLock);
            default -> {
                throw new IllegalArgumentException("Invalid command type: " + type);
            }
        }
    }


    public String executeCommand() {
        if (command != null) {
            return command.execute();
        }
        return JsonController.createErrorResponse("Command not set");
    }
}
