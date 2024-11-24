package gawr.oskar.server.database;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

class DatabaseManagerTest {
    private DatabaseManager databaseManager;
    private Map<String, Object> database;
    private Lock readLock;
    private Lock writeLock;
    private Gson gson;

    @BeforeEach
    void setUp() {
        readLock = new ReentrantLock();
        writeLock = new ReentrantLock();
        databaseManager = new DatabaseManager(readLock, writeLock);
        database = new HashMap<>();
        gson = new Gson();
    }

    @Test
    void testLoadDatabaseFromExistingFile() throws IOException {
        Path tempFile = Files.createTempFile("testDb", ".json");
        String sampleData = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Files.writeString(tempFile, sampleData);

        databaseManager.loadDatabaseFromFile(database, tempFile.toString());

        assertEquals("value1", database.get("key1"));
        assertEquals("value2", database.get("key2"));

        Files.delete(tempFile);
    }

    @Test
    void testLoadDatabaseFromNonExistingFile() {
        databaseManager.loadDatabaseFromFile(database, "nonExistentFile.json");

        assertTrue(database.isEmpty());

    }
    

    @Test
    void testSaveDatabaseToFile() throws IOException {
        database.put("key1", "value1");
        database.put("key2", "value2");

        Path tempFile = Files.createTempFile("testDb", ".json");

        databaseManager.saveDatabaseToFile(database, tempFile.toString());

        String fileContent = Files.readString(tempFile);
        Map<String, Object> loadedData = gson.fromJson(fileContent, Map.class);

        assertEquals("value1", loadedData.get("key1"));
        assertEquals("value2", loadedData.get("key2"));

        Files.delete(tempFile);
    }

    @Test
    void testGetWriteLock() {
        assertEquals(writeLock, databaseManager.getWriteLock());
    }
}