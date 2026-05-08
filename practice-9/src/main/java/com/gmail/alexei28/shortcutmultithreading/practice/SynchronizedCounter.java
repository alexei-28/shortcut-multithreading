package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Счетчик с использованием synchronized для гарантии happens-before.
 */
/*
   Правило монитора (для synchronized):
   Разблокировка монитора(synchronized блок завершён) happens-before последующей блокировки того же монитора(другой поток вошёл в synchronized блок).
     - это значит, что все изменения, сделанные внутри synchronized блока, станут видны потоку, который следующим зайдёт в этот же блок.
 */
public class SynchronizedCounter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
