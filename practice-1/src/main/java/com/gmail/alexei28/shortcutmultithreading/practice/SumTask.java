package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.*;

/**
 * Задача, реализующая Callable, которая суммирует числа от 1 до n.
 */
public class SumTask implements Callable<Integer> {
    private final int counter;

    public SumTask(int counter) {
        this.counter = counter;
    }

    @Override
    public Integer call() {
        int result = 0;
        for (int index = 1; index <= counter; index++) {
            result = result + index;
        }
        System.out.println("call, thread = " + Thread.currentThread().getName() + ", result = " + result);
        return result;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("SumTask, java.version = " +  System.getProperty("java.version"));
        executeCallable();
    }

    // Метод executeCallable, который выполняет Callable через ExecutorService
    private static void executeCallable() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            SumTask task = new SumTask(10);
            Future<Integer> future = executor.submit(task);
            int result = future.get();
            System.out.println("executeCallable, thread = " + Thread.currentThread().getName() + ", result = " + result);
        } finally {
            System.out.println("executeCallable, finally");
            executor.shutdown();
        }
    }
}