package com.gmail.alexei28.shortcutmultithreading.practice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Задача: Реализуйте классы и методы так, чтобы все тесты проходили.
 *
 * Требования:
 * 1. Реализуйте TaskProcessor, который обрабатывает задачи через ExecutorService
 * 2. Реализуйте параллельную обработку списка задач через ParallelProcessor
 * 3. Реализуйте использование виртуальных потоков через VirtualThreadExecutor (Java 21+)
 * 4. Реализуйте правильное завершение ExecutorService
 *
 * Подсказки:
 * - Используйте Executors.newFixedThreadPool() для создания пула потоков
 * - Не забывайте вызывать shutdown() и awaitTermination()
 * - Для виртуальных потоков используйте Executors.newVirtualThreadPerTaskExecutor()
 */
class ExecutorServiceTest {

    /**
     * Тест проверяет работу TaskProcessor с фиксированным пулом потоков.
     * TaskProcessor должен обрабатывать задачи и корректно завершаться.
     */
    @Test
    @Timeout(10)
    void testFixedThreadPool() throws InterruptedException, ExecutionException {
        TaskProcessor processor = new TaskProcessor(5);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            Future<Integer> future = processor.processTask(() -> {
                Thread.sleep(100);
                return taskId * 2;
            });
            futures.add(future);
        }

        for (int i = 0; i < futures.size(); i++) {
            assertEquals(i * 2, futures.get(i).get(),
                    "Результат задачи должен быть корректным");
        }

        assertTrue(processor.shutdownGracefully(5, TimeUnit.SECONDS),
                "ExecutorService должен завершиться");
        assertTrue(processor.isTerminated(),
                "ExecutorService должен быть завершен");
    }

}
