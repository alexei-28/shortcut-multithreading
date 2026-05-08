package com.gmail.alexei28.shortcutmultithreading.debug;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ЗАДАЧА: Найдите и исправьте deadlock.
 *
 * Проблема: Два потока захватывают замки в разном порядке,
 * что приводит к взаимной блокировке.
 *
 * Поток 1: lock1 -> lock2
 * Поток 2: lock2 -> lock1
 *
 * Подсказки для исправления:
 * 1. Захватывайте замки в одинаковом порядке
 * 2. Или используйте tryLock() с тайм-аутом
 */
public class BrokenDeadlock {

    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    private int balance1 = 1000;
    private int balance2 = 1000;

    /**
     * Переводит деньги с balance1 на balance2.
     */
    public void transferTo2(int amount) {
        lock1.lock();  // Захватываем первый замок
        try {
            Thread.sleep(50);  // Имитация работы (увеличивает шанс deadlock)

            lock2.lock();  // Захватываем второй замок
            try {
                if (balance1 >= amount) {
                    balance1 -= amount;
                    balance2 += amount;
                }
            } finally {
                lock2.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock1.unlock();
        }
    }

    /**
     * Переводит деньги с balance2 на balance1.
     */
    public void transferTo1(int amount) {
        lock1.lock(); // Захватываем первый замок
        try {
            Thread.sleep(50);  // Имитация работы

            lock2.lock(); // Захватываем второй замок
            try {
                if (balance2 >= amount) {
                    balance2 -= amount;
                    balance1 += amount;
                }
            } finally {
                lock2.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock1.unlock();
        }
    }

    public int getBalance1() {
        return balance1;
    }

    public int getBalance2() {
        return balance2;
    }

    public int getTotalBalance() {
        return balance1 + balance2;
    }
}