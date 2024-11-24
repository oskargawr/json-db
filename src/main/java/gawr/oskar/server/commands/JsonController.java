package gawr.oskar.server.commands;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonController {
    private static final Gson GSON = new Gson();

    private JsonController() { }

    public static String createErrorResponse(final String reason) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("response", "ERROR");
        responseMap.put("reason", reason);
        return GSON.toJson(responseMap);
    }

    public static String createSuccessResponse() {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("response", "OK");
        return GSON.toJson(responseMap);
    }

    public static String createSuccessResponse(final Object result) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("response", "OK");
        responseMap.put("value", result);
        return GSON.toJson(responseMap);
    }
}
