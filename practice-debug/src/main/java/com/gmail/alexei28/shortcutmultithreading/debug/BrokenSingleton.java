package com.gmail.alexei28.shortcutmultithreading.debug;

/**
 * ЗАДАЧА: Найдите и исправьте проблему в Double-Checked Locking.
 *
 * Проблема: Без volatile возможна ситуация, когда поток увидит
 * частично сконструированный объект (instruction reordering).
 *
 * Подсказка: JVM может переупорядочить инструкции:
 * 1. Выделить память
 * 2. Присвоить ссылку instance
 * 3. Вызвать конструктор (!)
 *
 * Другой поток может увидеть non-null instance до завершения конструктора.
 */
public class BrokenSingleton {
    private volatile static BrokenSingleton instance;

    private final String value;

    private BrokenSingleton() {
        // Имитация тяжелой инициализации
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.value = "initialized";
    }

    /**
     * Возвращает единственный экземпляр.
     */
    public static BrokenSingleton getInstance() {
        if (instance == null) {  // Первая проверка (без синхронизации)
            synchronized (BrokenSingleton.class) {
                if (instance == null) {  // Вторая проверка
                    instance = new BrokenSingleton();
                }
            }
        }
        return instance;
    }

    public String getValue() {
        return value;
    }

    /**
     * Сброс для тестирования (не используйте в production!)
     */
    public static void reset() {
        synchronized (BrokenSingleton.class) {
            instance = null;
        }
    }
}