package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Класс для демонстрации happens-before через Thread.start().
 */
/*
    В Java Memory Model есть гарантия:
      - все действия в потоке до вызова Thread.start() happens-before любых действий внутри запущенного потока.
 */
public class DataHolder {
    private int value;

    public void setValue(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}