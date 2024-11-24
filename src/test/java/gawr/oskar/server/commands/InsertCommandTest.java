package gawr.oskar.server.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class InsertCommandTest {
    private Map<String, Object> database;
    private Lock writeLock;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();
        writeLock = new ReentrantLock();
    }

    @Test
    void testInsertNewValue() {
        InsertCommand insertCommand = new InsertCommand(List.of("key1"), "value1", database, writeLock);

        insertCommand.execute();

        assertEquals("value1", database.get("key1"));
    }

    @Test
    void testUpdateExistingValue() {
        database.put("key1", "oldValue");
        InsertCommand insertCommand = new InsertCommand(List.of("key1"), "newValue", database, writeLock);

        insertCommand.execute();

        assertEquals("newValue", database.get("key1"));
    }

    @Test
    void testInsertNestedValue() {
        InsertCommand insertCommand = new InsertCommand(List.of("level1", "level2", "key"), "value", database, writeLock);

        insertCommand.execute();

        assertTrue(database.containsKey("level1"));
        assertTrue(((Map<String, Object>) database.get("level1")).containsKey("level2"));
        assertEquals("value", ((Map<String, Object>) ((Map<String, Object>) database.get("level1")).get("level2")).get("key"));
    }

    @Test
    void testInsertIntoExistingNestedValue() {
        Map<String, Object> level2Map = new HashMap<>();
        database.put("level1", level2Map);
        level2Map.put("existingKey", "existingValue");

        InsertCommand insertCommand = new InsertCommand(List.of("level1", "level2", "newKey"), "newValue", database, writeLock);

        insertCommand.execute();

        assertTrue(((Map<String, Object>) database.get("level1")).containsKey("level2"), "The nested level 'level2' should be created.");
        assertEquals("newValue", ((Map<String, Object>) ((Map<String, Object>) database.get("level1")).get("level2")).get("newKey"),
                "The new nested key 'newKey' should have the value 'newValue'.");
        assertEquals("existingValue", ((Map<String, Object>) database.get("level1")).get("existingKey"),
                "The existing key 'existingKey' should still have the value 'existingValue'.");
    }

}