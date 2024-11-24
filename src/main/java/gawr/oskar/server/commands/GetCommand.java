package gawr.oskar.server.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.Map;

public class GetCommand implements Command {
    private final Map<String, Object> database;
    private final List<String> keyPath;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "Need to perform commands on actual database, not copy.")
    public GetCommand(final List<String> keyPath,
                      final Map<String, Object> database) {
        this.keyPath = keyPath;
        this.database = database;
    }


    @Override
    public String execute() {
        Object result = findNestedValue(database, keyPath);
        if (result == null) {
            return JsonController.createErrorResponse("No such key");
        }
        return JsonController.createSuccessResponse(result);
    }

    private Object findNestedValue(final Map<String, Object> map,
                                   final List<String> keys) {
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map && ((Map<?, ?>) current).containsKey(key)) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current;
    }
}
