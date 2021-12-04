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
            service.submit(() -> {
                long id = idGenerator.nextId();
                //System.out.println(Thread.currentThread().getName()+ "      |      id:    "+id);
                uniqueSet.add(id);
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(numberOfThreads, uniqueSet.size());
    }
}
