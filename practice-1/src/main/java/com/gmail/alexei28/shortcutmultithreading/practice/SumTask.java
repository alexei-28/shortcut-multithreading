package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.Callable;

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
        return result;
    }
}