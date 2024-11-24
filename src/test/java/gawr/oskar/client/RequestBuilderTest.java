package gawr.oskar.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class RequestBuilderTest {
    private Gson gson;
    private Type mapType;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        mapType = new TypeToken<Map<String, Object>>() {}.getType();
    }

    @Test
    void testBuildRequest_withCommandParameters_setType() {
        RequestBuilder requestBuilder = new RequestBuilder("set", "myKey", "myValue");
        String json = requestBuilder.buildRequest();
        Map<String, Object> jsonMap = gson.fromJson(json, mapType);

        assertEquals("set", jsonMap.get("type"));
        assertEquals("myKey", jsonMap.get("key"));
        assertEquals("myValue", jsonMap.get("value"));
    }

    @Test
    void testBuildRequest_withCommandParameters_getType() {
        RequestBuilder requestBuilder = new RequestBuilder("get", "myKey", null);
        String json = requestBuilder.buildRequest();
        Map<String, Object> jsonMap = gson.fromJson(json, mapType);

        assertEquals("get", jsonMap.get("type"));
        assertEquals("myKey", jsonMap.get("key"));
    }

    @Test
    void testBuildRequest_withCommandParameters_exitType() {
        RequestBuilder requestBuilder = new RequestBuilder("exit", null, null);
        String json = requestBuilder.buildRequest();
        Map<String, Object> jsonMap = gson.fromJson(json, mapType);

        assertEquals("exit", jsonMap.get("type"));
    }

    @Test
    void testBuildRequest_withInputFile_success() {
        String filePath = "sample.json";
        String fileContent = "{\"type\":\"get\",\"key\":\"myKey\"}";

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {

            filesMock.when(() -> Files.readAllBytes(any())).thenReturn(fileContent.getBytes());
            pathsMock.when(() -> Paths.get(any())).thenReturn(null);

            RequestBuilder requestBuilder = new RequestBuilder(filePath);
            String json = requestBuilder.buildRequest();
            Map<String, Object> jsonMap = gson.fromJson(json, mapType);

            assertEquals("get", jsonMap.get("type"));
            assertEquals("myKey", jsonMap.get("key"));
        }
    }

    @Test
    void testBuildRequest_withInputFile_failure() {
        String filePath = "nonexistent.json";
        String expectedError = "Unable to read file";

        try (MockedStatic<Files> filesMock = mockStatic(Files.class);
             MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {

            filesMock.when(() -> Files.readAllBytes(any())).thenThrow(new IOException("File not found"));
            pathsMock.when(() -> Paths.get(any())).thenReturn(null);

            RequestBuilder requestBuilder = new RequestBuilder(filePath);
            String json = requestBuilder.buildRequest();
            Map<String, Object> jsonMap = gson.fromJson(json, mapType);

            assertEquals("ERROR", jsonMap.get("response"));
            assertEquals(expectedError, jsonMap.get("reason"));
        }
    }

    @Test
    void testBuildRequest_withInvalidJsonValue() {
        RequestBuilder requestBuilder = new RequestBuilder("set", "myKey", "{invalidJson}");
        String json = requestBuilder.buildRequest();
        Map<String, Object> jsonMap = gson.fromJson(json, mapType);

        assertEquals("set", jsonMap.get("type"));
        assertEquals("myKey", jsonMap.get("key"));
        assertEquals("{invalidJson}", jsonMap.get("value"));
    }
}
