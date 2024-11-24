package gawr.oskar.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestExecutor {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void submitTask(final Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean isClosed() {
        return executorService.isShutdown();
    }
}
