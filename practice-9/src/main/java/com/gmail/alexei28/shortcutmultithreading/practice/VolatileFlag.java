package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Класс с volatile флагом для демонстрации видимости изменений.
 */
public class VolatileFlag {
    // Правило volatile переменной*: Запись в volatile поле happens-before каждого последующего чтения этого же поля.
    private volatile int waitForReady;

    public void setReady(int i) {
        this.waitForReady = i;
    }

    public int waitForReady() {
        while (waitForReady == 0) {
            // Ждем, пока флаг не будет установлен
        }
        return waitForReady;
    }
}