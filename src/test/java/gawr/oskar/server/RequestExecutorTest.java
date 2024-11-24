package gawr.oskar.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


class RequestExecutorTest {
    private RequestExecutor requestExecutor;

    @BeforeEach
    void setUp() {
        requestExecutor = new RequestExecutor();
    }

    @AfterEach
    void tearDown() {
        requestExecutor.shutdown();
    }

    @Test
    void testSubmitTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        requestExecutor.submitTask(latch::countDown);

        boolean taskCompleted = latch.await(1, TimeUnit.SECONDS);

        assertTrue(taskCompleted, "Task should be executed by the request executor");
    }

    @Test
    void testShutdown() {
        requestExecutor.shutdown();

        assertTrue(requestExecutor.isClosed(), "ExecutorService should be shut down");
    }
}
