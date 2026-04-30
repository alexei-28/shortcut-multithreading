package com.gmail.alexei28.shortcutmultithreading.debug;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ЗАДАЧА: Найдите и исправьте race condition в этом счетчике.
 *
 * Проблема: При одновременном вызове increment() из нескольких потоков
 * итоговое значение счетчика меньше ожидаемого.
 *
 * Подсказка: Операция count++ не атомарна (read-modify-write).
 */
public class BrokenCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * Увеличивает счетчик на 1.
     * BUG: Эта операция не атомарна!
     */
    public void increment() {
        count.incrementAndGet();
    }

    /**
     * Возвращает текущее значение счетчика.
     */
    public int get() {
        return count.get();
    }
}