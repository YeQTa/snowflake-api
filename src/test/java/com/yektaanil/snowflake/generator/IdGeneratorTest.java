package com.yektaanil.snowflake.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author : Yekta Anil AKSOY
 * @since : 4.12.2021
 **/
class IdGeneratorTest {

    private static IdGenerator idGenerator;
    @BeforeAll
    static void setUp(){
        idGenerator = IdGenerator.getInstance();
    }

    @Test
    void should_generate_unique_id_concurrently() throws InterruptedException {
        Set<Long> uniqueSet = ConcurrentHashMap.newKeySet();
        final int numberOfThreads = 20;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
         service.submit(
          () -> {
            try {
              latch.countDown();
              latch.await();
              long id = idGenerator.nextId();
              uniqueSet.add(id);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
        }
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(numberOfThreads, uniqueSet.size());
    }
}
