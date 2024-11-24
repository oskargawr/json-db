package gawr.oskar.server.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonControllerTest {

    @Test
    void testCreateErrorResponse() {
        String result = JsonController.createErrorResponse("No such key");
        assertEquals("{\"response\":\"ERROR\",\"reason\":\"No such key\"}", result);
    }

    @Test
    void testCreateSuccessResponse() {
        String result = JsonController.createSuccessResponse();
        assertEquals("{\"response\":\"OK\"}", result);
    }

    @Test
    void testCreateSuccessResponseWithValue() {
        String result = JsonController.createSuccessResponse("testValue");
        assertEquals("{\"response\":\"OK\",\"value\":\"testValue\"}", result);
    }
}