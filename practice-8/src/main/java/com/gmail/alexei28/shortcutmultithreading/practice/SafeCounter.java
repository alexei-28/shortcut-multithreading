package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Потокобезопасный счетчик без состояния гонки.
 */
public class SafeCounter {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        counter.incrementAndGet();
    }

    public int get() {
        return counter.get();
    }
}