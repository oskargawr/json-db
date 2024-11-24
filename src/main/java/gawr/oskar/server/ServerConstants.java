package gawr.oskar.server;

public final class ServerConstants {
    public static final int PORT = 3030;
    public static final String STOP_STRING = "exit";
    public static final String DB_PATH = System.getProperty("user.dir") + "/src/main/resources/db.json";

    private ServerConstants() {
        // Prevent instantiation
    }
}
