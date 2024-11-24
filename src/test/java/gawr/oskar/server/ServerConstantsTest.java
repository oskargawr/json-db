package gawr.oskar.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConstantsTest {

    @Test
    void testConstants() {
        assertEquals(3030, ServerConstants.PORT);
        assertEquals("exit", ServerConstants.STOP_STRING);
        assertEquals((System.getProperty("user.dir") + "/src/main/resources/db.json"), ServerConstants.DB_PATH);
    }

}