package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Задача, реализующая Runnable, которая увеличивает счетчик.
 */
public class CounterTask  implements Runnable {
    private final AtomicInteger counter;

    public CounterTask(AtomicInteger counter) {
        this.counter = counter;
    }

    /*
      Важно!
      run() никогда не вызывается напрямую из start() в том же потоке.
      То есть это НЕ так:
        main → start() → run()

      А вот так:
        main → start()
                     ↘
                      новый поток → run()
     */
    @Override
    public void run() {
        counter.incrementAndGet();
    }
}