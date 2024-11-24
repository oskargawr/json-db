package gawr.oskar.server.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class DeleteCommandTest {
    private Map<String, Object> database;
    private Lock writeLock;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();
        writeLock = new ReentrantLock();
    }

    @Test
    void testDeleteExistingKey() {
        database.put("key1", "value1");
        DeleteCommand deleteCommand = new DeleteCommand(List.of("key1"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"OK\"}", result);
        assertFalse(database.containsKey("key1"), "The key 'key1' should be removed from the database.");
    }

    @Test
    void testDeleteExistingNestedKey() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nestedKey", "nestedValue");
        database.put("key1", nestedMap);

        DeleteCommand deleteCommand = new DeleteCommand(List.of("key1", "nestedKey"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"OK\"}", result);
        assertFalse(((Map<String, Object>) database.get("key1")).containsKey("nestedKey"),
                "The nested key 'nestedKey' should be removed from the database.");
    }

    @Test
    void testDeleteNonExistentKey() {
        DeleteCommand deleteCommand = new DeleteCommand(List.of("nonExistentKey"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }

    @Test
    void testDeleteNonExistentNestedKey() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("existingKey", "value");
        database.put("level1", nestedMap);

        DeleteCommand deleteCommand = new DeleteCommand(List.of("level1", "nonExistentKey"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }

    @Test
    void testDeleteFromEmptyDatabase() {
        DeleteCommand deleteCommand = new DeleteCommand(List.of("someKey"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }

    @Test
    void testDeleteKeyPointingToNonMapValue() {
        database.put("key1", "notAMap");  // key1 points to a string, not a map
        DeleteCommand deleteCommand = new DeleteCommand(List.of("key1", "subKey"), database, writeLock);

        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }

    @Test
    void testDeleteKeyWithEmptyNestedMap() {
        Map<String, Object> nestedMap = new HashMap<>();
        database.put("key1", nestedMap);

        DeleteCommand deleteCommand = new DeleteCommand(List.of("key1", "subKey"), database, writeLock);
        String result = deleteCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
        assertTrue(nestedMap.isEmpty(), "The nested map should remain empty.");
    }
}