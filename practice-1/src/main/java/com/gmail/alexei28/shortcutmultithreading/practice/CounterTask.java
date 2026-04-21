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
        System.out.println("run, thread = " + Thread.currentThread().getName() + ", counter = " + counter.get());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("CounterTask, java.versio = " +  System.getProperty("java.version"));
        createThreadWithRunnable();
    }

    // Создает и запускает поток с Runnable
    private static void createThreadWithRunnable() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CounterTask task = new CounterTask(counter);
        Thread t = new Thread(task);
        t.start();
        t.join(); // Основной поток остановится здесь и будет ждать, пока 't' не завершится

        System.out.println("createThreadWithRunnable, thread = " + Thread.currentThread().getName() + ", counter = " + counter.get());
    }
}