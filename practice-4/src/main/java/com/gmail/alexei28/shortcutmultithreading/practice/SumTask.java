package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.RecursiveTask;

/**
 * Задача для суммирования элементов массива с использованием ForkJoinPool.
 */
public class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start;
    private final int end;
    private final int threshold = 10_000; // Порог для разделения задачи

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= threshold) {
            // 1. Базовый случай: задача достаточно мала, считаем напрямую
            return sum();
        } else {
            // 2. Рекурсивный случай: задача большая, делим её
            int mid = start + length / 2;

            // Создаём задачу для левой половины
            SumTask leftTask = new SumTask(array, start, mid);
            // Создаём задачу для правой половины
            SumTask rightTask = new SumTask(array, mid, end);

            // Асинхронно выполняем левую задачу
            leftTask.fork(); // ← "Вилкуем" задачу в пул

            // Выполняем правую задачу в текущем потоке
            Long rightResult = rightTask.compute(); // ← Эффективность: не создаём лишний поток

            // Ждём завершения левой задачи и получаем результат
            Long leftResult = leftTask.join(); // ← "Соединяем" результат

            return leftResult + rightResult;
        }
    }

    private long sum() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }
}