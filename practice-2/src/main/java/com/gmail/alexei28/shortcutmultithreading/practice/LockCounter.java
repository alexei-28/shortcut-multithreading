package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Потокобезопасный счетчик с использованием ReentrantLock.
 * 3. Реализуйте счетчик с использованием ReentrantLock
 */
public class LockCounter {
    private final ReentrantLock reentrantLock = new ReentrantLock(); // Создаём замок
    private int count = 0;

    public void increment() {
        reentrantLock.lock(); // Захватываем замок
        try {
            count++;
        } finally {
            // Всегда вызывайте unlock() в блоке finally.
            // Это гарантирует, что замок будет освобождён, даже если в защищённом блоке произойдёт исключение.
            reentrantLock.unlock();
        }
    }

    public int getCount() {
        return count;
    }
}