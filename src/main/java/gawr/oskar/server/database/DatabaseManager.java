package gawr.oskar.server.database;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class DatabaseManager {
    private final Gson gson = new Gson();
    private final Lock readLock;
    private final Lock writeLock;

    public DatabaseManager(final Lock readLock, final Lock writeLock) {
        this.readLock = readLock;
        this.writeLock = writeLock;
    }

    public void loadDatabaseFromFile(final Map<String, Object> database,
                                     final String dbPath) {
        readLock.lock();
        try {
            if (Files.exists(Paths.get(dbPath))) {
                String content = Files.readString(Paths.get(dbPath), StandardCharsets.UTF_8);
                Map<String, Object> fileData = gson.fromJson(content, Map.class);
                if (fileData != null) {
                    database.putAll(fileData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    public void saveDatabaseToFile(final Map<String, Object> database,
                                   final String dbPath) {
        writeLock.lock();
        try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(dbPath), StandardCharsets.UTF_8)) {
            gson.toJson(database, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public Lock getWriteLock() {
        return writeLock;
    }
}
