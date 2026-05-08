package com.gmail.alexei28.shortcutmultithreading.debug;

/**
 * ЗАДАЧА: Найдите и исправьте проблему видимости.
 *
 * Проблема: Поток-читатель может никогда не увидеть изменение флага running,
 * сделанное потоком-писателем, и зациклиться навсегда.
 *
 * Подсказка: Без volatile или synchronized изменения могут быть
 * закэшированы в регистрах/кэше процессора.
 */
public class BrokenVisibility {
    private volatile boolean running = true;
    private int counter = 0;

    /**
     * Останавливает выполнение.
     */
    public void stop() {
        running = false;
    }

    /**
     * Выполняет работу пока running == true.
     */
    public void doWork() {
        while (running) {
            counter++;
            // Имитация работы
            Thread.yield();
        }
    }

    public int getCounter() {
        return counter;
    }

    public boolean isRunning() {
        return running;
    }
}