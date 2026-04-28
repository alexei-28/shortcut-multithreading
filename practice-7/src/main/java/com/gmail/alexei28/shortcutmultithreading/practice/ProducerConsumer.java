package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Реализация паттерна "Производитель-Потребитель" с использованием BlockingQueue.
 *
 * Задание: Реализуйте методы так, чтобы все тесты проходили.
 *
 * Подсказки:
 * - BlockingQueue.put() блокируется, если очередь полна
 * - BlockingQueue.take() блокируется, если очередь пуста
 * - BlockingQueue.offer() с тайм-аутом возвращает false, если не удалось добавить
 */

/*
    BlockingQueue - это потокобезопасная очередь.
    Она:
     - блокирует поток, если очередь пустая (при take())
     - блокирует поток, если очередь переполнена (при put())
    Это ключевой механизм синхронизации между производителем и потребителем.
*/
public class ProducerConsumer {
    private final BlockingQueue<Integer> queue;
    private final AtomicInteger producedCount = new AtomicInteger(0);
    private final AtomicInteger consumedCount = new AtomicInteger(0);

    public ProducerConsumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    /*
        - queue.put() — потокобезопасный и блокирующий
        - если очередь заполнена → поток остановится
        Это предотвращает переполнение буфера.
     */
    public void produce(int i) throws InterruptedException {
        for (int index = 0; index <  i; index++) {
            queue.put(index);
            producedCount.incrementAndGet();
        }
    }

    /*
        - take() блокирует поток, пока не появится элемент
        - гарантирует корректную синхронизацию с producer
    */
    public void consume(int i) throws InterruptedException {
        for (int index = 0; index < i; index++) {
            queue.take();
            consumedCount.incrementAndGet();
        }
    }

    public int getProducedCount() {
        return producedCount.get();
    }

    public int getConsumedCount() {
        return consumedCount.get();
    }
}