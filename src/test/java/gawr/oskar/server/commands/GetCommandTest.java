package gawr.oskar.server.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

class GetCommandTest {
    private Map<String, Object> database;

    @BeforeEach
    void setUp() {
        database = new HashMap<>();
    }

    @Test
    void testGetExistingKey() {
        database.put("key1", "value1");
        GetCommand getCommand = new GetCommand(List.of("key1"), database);

        String result = getCommand.execute();

        assertEquals("{\"response\":\"OK\",\"value\":\"value1\"}", result);
    }

    @Test
    void testGetExistingNestedKey() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nestedKey", "nestedValue");
        database.put("key1", nestedMap);

        GetCommand getCommand = new GetCommand(List.of("key1", "nestedKey"), database);

        String result = getCommand.execute();

        assertEquals("{\"response\":\"OK\",\"value\":\"nestedValue\"}", result);
    }

    @Test
    void testGetNonExistentKey() {
        GetCommand getCommand = new GetCommand(List.of("nonExistentKey"), database);

        String result = getCommand.execute();

        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }
}