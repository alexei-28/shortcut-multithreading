package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Потокобезопасный счетчик с использованием synchronized.
 * 1. Реализуйте потокобезопасный счетчик с использованием synchronized
 */
public class SynchronizedCounter {
    private final Object lock = new Object(); // Специальный объект-замок
    private int count;

    public void increment() {
        synchronized (lock) {
            count++;
        }
    }

    public int getCount() {
        return count;
    }
}