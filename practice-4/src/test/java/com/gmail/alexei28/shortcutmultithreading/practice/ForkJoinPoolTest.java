package com.gmail.alexei28.shortcutmultithreading.practice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Задача: Реализуйте классы и методы так, чтобы все тесты проходили.
 *
 * Требования:
 * 1. Реализуйте SumTask, который суммирует элементы массива рекурсивно
 * 2. Реализуйте MaxTask, который находит максимальный элемент в массиве
 * 3. Используйте правильный порог (threshold) для разделения задач
 * 4. Правильно используйте fork() и join()
 */
class ForkJoinPoolTest {

    /**
     * Тест проверяет суммирование элементов массива через SumTask.
     * Задача должна рекурсивно разделяться на подзадачи до достижения порога.
     */
    @Test
    @Timeout(10)
    void testSumTask() {
        long[] array = new long[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        ForkJoinPool pool = ForkJoinPool.commonPool();
        long expectedSum = array.length * (array.length + 1) / 2;

        SumTask task = new SumTask(array, 0, array.length);
        long result = pool.invoke(task);

        assertEquals(expectedSum, result,
                "Сумма должна быть вычислена корректно");
    }
}
