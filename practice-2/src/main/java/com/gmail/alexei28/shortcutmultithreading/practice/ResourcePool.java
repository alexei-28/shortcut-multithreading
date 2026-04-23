package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Пул ресурсов с использованием Semaphore.
 * 5. Реализуйте пул ресурсов с использованием Semaphore
 */

/*
    Общая логика работы:
    Допустим permits = 3

    Шаг 1
        T1.acquire → permits 2, activeCount=1
        T2.acquire → permits 1, activeCount=2
        T3.acquire → permits 0, activeCount=3

    Шаг 2
        T4.acquire → WAITING (нет permits)

    Шаг 3
        T1.release → permits 1, activeCount=2
        → T4 просыпается и заходит

 */
public class ResourcePool {
    /*
        Главный ограничитель:
         - хранит число доступных "разрешений" (permits)
         - контролирует, сколько потоков могут одновременно войти

        Semaphore не даёт больше N потокам работать одновременно.
    */
    private final Semaphore semaphore;

    /*
        Счётчик активных потоков.
        - увеличивается при входе в пул
        - уменьшается при выходе
        - потокобезопасен (не нужны synchronized)

       AtomicInteger показывает, сколько потоков сейчас внутри
     */
    private final AtomicInteger activeCount; // потокобезопасный счётчик без блокировок

    public ResourcePool(int permits) {
        this.semaphore = new Semaphore(permits);
        this.activeCount = new AtomicInteger(0);
    }

    /*
        1.semaphore.acquire():
          Если есть permits
            - уменьшает счётчик semaphore на 1
            - поток сразу проходит
          Если нет permits
            - поток блокируется (ждёт)
            - просыпается, когда кто-то вызовет release()

          Пример:
            permits = 3
            T1 → acquire → 2
            T2 → acquire → 1
            T3 → acquire → 0
            T4 → acquire → WAITING

        2.activeCountAtomic.incrementAndGet():
          Увеличиваем число активных потоков:
            - было 2 → стало 3
            - это отражает, что поток "внутри работы"

          Почему Atomic?
            - несколько потоков делают это одновременно
            - без Atomic была бы race condition
     */
    public void acquire() throws InterruptedException {
        semaphore.acquire();

        activeCount.incrementAndGet();
    }

    /*
        1. semaphore.release():
            - увеличивает permits на 1
            - будит один из ожидающих потоков (если есть)
          Если забыли release() → вечная блокировка

          Пример:
            permits: 0
            T4 ждёт

            T1 → release → permits = 1
            → T4 просыпается

        2. activeCountAtomic.decrementAndGet():
             Просто возвращает, потокобезопасно, текущее значение счётчика
    */
    public void release() {
        semaphore.release();

        activeCount.decrementAndGet();
    }

    public int getActiveCount() {
        return activeCount.get();
    }
}