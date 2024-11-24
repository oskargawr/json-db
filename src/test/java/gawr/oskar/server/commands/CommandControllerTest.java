package gawr.oskar.server.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class CommandControllerTest {
    private CommandController commandController;
    private Map<String, Object> database;
    private Lock writeLock;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();
        writeLock = new ReentrantLock();
        commandController = new CommandController();
    }

    @Test
    void testSetCommand() {
        Map<String, Object> request = Map.of("type", "set", "key", "testKey", "value", "testValue");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\"}", result);
        assertEquals("testValue", database.get("testKey"));
    }

    @Test
    void testGetCommand() {
        database.put("testKey", "testValue");
        Map<String, Object> request = Map.of("type", "get", "key", "testKey");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\",\"value\":\"testValue\"}", result);
    }

    @Test
    void testDeleteCommand() {
        database.put("testKey", "testValue");
        Map<String, Object> request = Map.of("type", "delete", "key", "testKey");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\"}", result);
        assertEquals(null, database.get("testKey"));
    }

    @Test
    void testInvalidCommandType() {
        Map<String, Object> request = Map.of("type", "invalid", "key", "testKey");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commandController.setCommand(request, database, writeLock);
        });

        assertEquals("Invalid command type: invalid", exception.getMessage());
    }

    @Test
    void testSetCommandWithStringKey() {
        Map<String, Object> request = Map.of("type", "set", "key", "testKey", "value", "testValue");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\"}", result);
        assertEquals("testValue", database.get("testKey"));
    }

    @Test
    void testSetCommandWithDoubleKey() {
        Map<String, Object> request = Map.of("type", "set", "key", 123.45, "value", "testValue");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\"}", result);
        assertEquals("testValue", database.get("123.45"));
    }

    @Test
    void testSetCommandWithListKey() {
        Map<String, Object> request = Map.of("type", "set", "key", List.of("parentKey", "childKey"), "value", "testValue");

        commandController.setCommand(request, database, writeLock);
        String result = commandController.executeCommand();

        assertEquals("{\"response\":\"OK\"}", result);
        assertEquals("testValue", ((Map<String, Object>) database.get("parentKey")).get("childKey"));
    }

    @Test
    void testExecuteCommandWithoutSetting() {
        String result = commandController.executeCommand();
        assertEquals("{\"response\":\"ERROR\",\"reason\":\"Command not set\"}", result);
    }

}