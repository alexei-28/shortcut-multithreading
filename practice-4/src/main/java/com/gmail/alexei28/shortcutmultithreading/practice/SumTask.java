package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.RecursiveTask;

/**
 * Задача для суммирования элементов массива с использованием ForkJoinPool.
 */
public class SumTask extends RecursiveTask<Long> {

    @Override
    protected Long compute() {
        return 0L;
    }
}