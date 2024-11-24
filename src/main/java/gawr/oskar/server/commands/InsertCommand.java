package gawr.oskar.server.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class InsertCommand implements Command {
    private final List<String> keyPath;
    private final Object value;
    private final Map<String, Object> database;
    private final Lock writeLock;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "Need to perform commands on actual database, not copy.")
    public InsertCommand(final List<String> keyPath, final Object value,
                         final Map<String, Object> database, final Lock writeLock) {
        this.keyPath = keyPath;
        this.value = value;
        this.database = database;
        this.writeLock = writeLock;
    }


    @Override
    public String execute() {
        writeLock.lock();
        try {
            Map<String, Object> currentMap = database;

            for (int i = 0; i < keyPath.size() - 1; i++) {
                String key = keyPath.get(i);

                if (!currentMap.containsKey(key) || !(currentMap.get(key) instanceof Map)) {
                    currentMap.put(key, new HashMap<>());
                }

                currentMap = (Map<String, Object>) currentMap.get(key);
            }

            String lastKey = keyPath.get(keyPath.size() - 1);
            currentMap.put(lastKey, value);

            return JsonController.createSuccessResponse();
        } finally {
            writeLock.unlock();
        }
    }
}
