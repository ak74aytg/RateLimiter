package com.lsd.rate_limiter.service;
import com.lsd.rate_limiter.strategy.SlidingWindowStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class SlidingWindowStrategyTest {
    @Autowired
    private SlidingWindowStrategy rateLimiter;

    @Test
    void contextLoads() {
    }

    @Test
    void onlyOneOfTwoConcurrentRequestsShouldPassWhenUserHas99Requests() throws Exception {
        String user = "user-" + UUID.randomUUID();

        for (int i = 0; i < 99; i++) {
            assertTrue(rateLimiter.RateLimiter(user, "Sliding Window"));
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Boolean> task = () -> {
            start.await();
            return rateLimiter.RateLimiter(user, "Sliding Window");
        };

        Future<Boolean> first = executor.submit(task);
        Future<Boolean> second = executor.submit(task);

        start.countDown();

        boolean result1 = first.get();
        boolean result2 = second.get();

        long allowedCount = Stream.of(result1, result2)
                .filter(Boolean::booleanValue)
                .count();

        assertEquals(1, allowedCount);

        executor.shutdown();
    }

    @Test
    void onlyRemainingCapacityShouldBeAllowedUnderConcurrentRequests() throws Exception {
        int attempts = 1000;
        int existingRequests = 99;
        int concurrentRequests = 10;
        int expectedAllowed = 100 - existingRequests;

        for (int attempt = 0; attempt < attempts; attempt++) {
            String user = "user-" + UUID.randomUUID();

            for (int i = 0; i < existingRequests; i++) {
                assertTrue(rateLimiter.RateLimiter(user, "Sliding Window"));
            }

            ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<Boolean>> futures = new ArrayList<>();

            try {
                Callable<Boolean> task = () -> {
                    start.await();
                    return rateLimiter.RateLimiter(user, "Sliding Window");
                };

                for (int i = 0; i < concurrentRequests; i++) {
                    futures.add(executor.submit(task));
                }

                start.countDown();

                long allowedCount = 0;
                for (Future<Boolean> future : futures) {
                    if (future.get()) {
                        allowedCount++;
                    }
                }

                assertEquals(expectedAllowed, allowedCount);
            } finally {
                executor.shutdownNow();
            }
        }
    }
}