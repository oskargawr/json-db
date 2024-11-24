package gawr.oskar.client;

import com.google.gson.Gson;
import gawr.oskar.server.commands.JsonController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
    private final String type;
    private final String index;
    private final String value;
    private final String inFilePath;
    private final Gson gson = new Gson();

    public RequestBuilder(final String type, final String index, final String value) {
        this.type = type;
        this.index = index;
        this.value = value;
        this.inFilePath = null;
    }

    public RequestBuilder(final String inFilePath) {
        this.type = null;
        this.index = null;
        this.value = null;
        this.inFilePath = inFilePath;
    }

    public String buildRequest() {
        if (inFilePath != null) {
            return createRequestFromFile();
        } else {
            return createCommandRequest();
        }
    }

    private String createCommandRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("type", type);

        if ("exit".equals(type)) {
            return gson.toJson(request);
        }

        if (index != null && !index.isEmpty()) {
            request.put("key", parseJson(index));
        }

        if (value != null && !value.isEmpty()) {
            request.put("value", parseJson(value));
        }

        return gson.toJson(request);
    }

    private String createRequestFromFile() {
        try {
            String filePath = System.getProperty("user.dir") + "/src/client/data/" + inFilePath;
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonController.createErrorResponse("Unable to read file");
        }
    }

    private Object parseJson(final String input) {
        try {
            return gson.fromJson(input, Object.class);
        } catch (Exception e) {
            return input;
        }
    }
}
