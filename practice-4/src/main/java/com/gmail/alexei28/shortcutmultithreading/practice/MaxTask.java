package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/**
 * Задача для поиска максимального элемента в массиве с использованием ForkJoinPool.
 */
public class MaxTask extends RecursiveTask<Integer> {
    private final int[] array;
    private final int start;
    private final int end;
    private final int threshold = 500; // Порог для разделения задачи

    public MaxTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int length = end - start;
        if (length <= threshold) {
            // 1. Базовый случай: задача достаточно мала, считаем напрямую
            return max();
        } else {
            // 2. Рекурсивный случай: задача большая, делим её
            int mid = start + length / 2;

            // Создаём задачу для левой половины
            MaxTask leftTask = new MaxTask(array, start, mid);
            // Создаём задачу для правой половины
            MaxTask rightTask = new MaxTask(array, mid, end);

            // Асинхронно выполняем левую задачу
            leftTask.fork(); // ← "Вилкуем" задачу в пул

            // Выполняем правую задачу в текущем потоке
            Integer rightResult = rightTask.compute(); // ← Эффективность: не создаём лишний поток

            // Ждём завершения левой задачи и получаем результат
            Integer leftResult = leftTask.join(); // ← "Соединяем" результат

            return leftResult > rightResult ? leftResult: rightResult;
        }
    }

    private int max() {
        return Arrays.stream(array)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
    }
}