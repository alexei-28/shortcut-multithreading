package com.gmail.alexei28.shortcutmultithreading.debug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки исправления багов.
 *
 * ВАЖНО: Не изменяйте эти тесты! Исправляйте только классы в src/main/java.
 *
 * Тесты специально написаны так, чтобы с высокой вероятностью обнаружить
 * проблемы многопоточности. Если тест иногда проходит, а иногда падает —
 * это признак того, что баг не исправлен.
 */
class DebugTest {

    /**
     * Тест 1: BrokenCounter - Race Condition
     *
     * 10 потоков по 10000 инкрементов = должно быть 100000.
     * Без синхронизации результат будет меньше.
     */
    @Test
    @Timeout(30)
    void testBrokenCounter_shouldBe100000() throws InterruptedException {
        BrokenCounter counter = new BrokenCounter();
        int threads = 10;
        int incrementsPerThread = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threads * incrementsPerThread, counter.get(),
                "Race condition! Ожидалось 100000, но получено " + counter.get() +
                        ". Подсказка: используйте AtomicInteger или synchronized");
    }

    /**
     * Тест 2: BrokenVisibility - Visibility Problem
     *
     * Поток-читатель должен увидеть изменение флага running.
     * Без volatile может зациклиться навсегда.
     */
    @Test
    @Timeout(5)
    void testBrokenVisibility_shouldStopWithin1Second() throws InterruptedException {
        BrokenVisibility visibility = new BrokenVisibility();

        Thread worker = new Thread(visibility::doWork);
        worker.start();

        // Даем поработать
        Thread.sleep(100);

        // Останавливаем
        visibility.stop();

        // Ждем завершения (максимум 1 секунда)
        worker.join(1000);

        assertFalse(worker.isAlive(),
                "Visibility problem! Поток не остановился. " +
                        "Подсказка: добавьте volatile к полю running");
    }

    /**
     * Тест 3: BrokenSingleton - DCL без volatile
     *
     * Множественные потоки должны получить один и тот же экземпляр,
     * и value должен быть инициализирован.
     */
    @Test
    @Timeout(10)
    void testBrokenSingleton_shouldReturnSameInstance() throws InterruptedException {
        BrokenSingleton.reset();

        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ConcurrentHashMap<BrokenSingleton, Boolean> instances = new ConcurrentHashMap<>();
        List<String> values = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();  // Все стартуют одновременно
                    BrokenSingleton instance = BrokenSingleton.getInstance();
                    instances.put(instance, true);
                    values.add(instance.getValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();  // Старт!
        doneLatch.await();
        executor.shutdown();

        assertEquals(1, instances.size(),
                "Singleton создан более одного раза! Instances: " + instances.size());

        for (String value : values) {
            assertEquals("initialized", value,
                    "DCL bug! Получен частично сконструированный объект. " +
                            "Value = " + value + ". Подсказка: добавьте volatile к instance");
        }
    }

    /**
     * Тест 4: BrokenCache - Check-then-act Race Condition
     *
     * При правильной реализации для одного ключа вычисление
     * должно произойти ровно 1 раз.
     */
    @Test
    @Timeout(10)
    void testBrokenCache_shouldComputeOnlyOnce() throws InterruptedException {
        BrokenCache cache = new BrokenCache();
        cache.clear();

        int threads = 20;
        String key = "testKey";
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    cache.getOrCompute(key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertEquals(1, cache.getComputeCount(),
                "Check-then-act race condition! Вычисление произошло " +
                        cache.getComputeCount() + " раз вместо 1. " +
                        "Подсказка: используйте ConcurrentHashMap.computeIfAbsent()");
    }

    /**
     * Тест 5: BrokenExecutor - Resource Leak
     *
     * После выполнения метода не должно остаться активных потоков
     * от ExecutorService.
     */
    @Test
    @Timeout(10)
    void testBrokenExecutor_shouldNotLeakThreads() throws InterruptedException {
        BrokenExecutor brokenExecutor = new BrokenExecutor();

        int initialThreadCount = Thread.activeCount();

        // Вызываем метод несколько раз
        for (int i = 0; i < 5; i++) {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);
            brokenExecutor.processInParallel(numbers);
        }

        // Даем время потокам завершиться (если они правильно закрыты)
        Thread.sleep(500);

        int finalThreadCount = Thread.activeCount();
        int leakedThreads = finalThreadCount - initialThreadCount;

        assertTrue(leakedThreads <= 2,  // Допускаем небольшую погрешность
                "Thread leak! Утекло " + leakedThreads + " потоков. " +
                        "Подсказка: всегда закрывайте ExecutorService в finally или try-with-resources");
    }

    /**
     * Тест 6: BrokenDeadlock - Deadlock
     *
     * Два потока должны успешно завершить переводы без взаимной блокировки.
     */
    @Test
    @Timeout(5)
    void testBrokenDeadlock_shouldNotDeadlock() throws InterruptedException {
        BrokenDeadlock bank = new BrokenDeadlock();

        int initialTotal = bank.getTotalBalance();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // Поток 1: переводит с balance1 на balance2
        executor.submit(() -> {
            for (int i = 0; i < 10; i++) {
                bank.transferTo2(10);
            }
            latch.countDown();
        });

        // Поток 2: переводит с balance2 на balance1
        executor.submit(() -> {
            for (int i = 0; i < 10; i++) {
                bank.transferTo1(10);
            }
            latch.countDown();
        });

        boolean completed = latch.await(4, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertTrue(completed,
                "Deadlock! Потоки заблокировали друг друга. " +
                        "Подсказка: захватывайте замки в одинаковом порядке (всегда lock1 -> lock2)");

        assertEquals(initialTotal, bank.getTotalBalance(),
                "Сумма балансов изменилась! Была " + initialTotal +
                        ", стала " + bank.getTotalBalance());
    }
}