package gawr.oskar.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Main client class, accepting command line parameters using JCommander.
 */
final class Main {

    @Parameter(names = "-t", description = "Type of request (set, get, delete)")
    private String type;

    @Parameter(names = "-k", description = "Database index")
    private String index;

    @Parameter(names = "-v", description = "Message to set in the database")
    private String value;

    @Parameter(names = "-in", description = "Input file path")
    private String inFilePath;

    private Main() { }

    public static void main(final String[] args) {
        Main main = new Main();

        JCommander jCommander = JCommander.newBuilder()
                .addObject(main)
                .build();
        jCommander.parse(args);

        if (main.inFilePath != null) {
            new Client(main.inFilePath);
        } else {
            new Client(main.type, main.index, main.value);
        }
    }
}
