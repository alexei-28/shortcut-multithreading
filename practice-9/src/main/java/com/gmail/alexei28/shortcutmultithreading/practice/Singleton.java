package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Singleton с правильным Double-Checked Locking и volatile.
 */
/*
    Почему volatile здесь критически важен?
    Операция instance = new Singleton(); не является атомарной.
    Она состоит из трёх шагов:
      1. Выделить память под объект.
      2. Вызвать конструктор для инициализации полей.
      3. Присвоить ссылку на объект переменной instance.

    Без volatile процессор может переупорядочить шаги 2 и 3.
    Что произойдет:
      1. Поток А заходит в synchronized, выделяет память и присваивает ссылку переменной instance (шаг 3).
      2. В этот момент поток Б вызывает getInstance(). Он видит, что instance уже не null (первая проверка), и возвращает эту ссылку.
      3. Проблема: Поток Б получает ссылку на _не до конца инициализированный объект_! Конструктор ещё не был вызван (шаг 2), потому что поток А ещё его выполняет.

     volatile предотвращает такое переупорядочивание, гарантируя, что ссылка на объект будет присвоена переменной
     только после полной инициализации объекта.
 */

public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) { // Первая проверка (без блокировки)
            synchronized (Singleton.class) {
                if (instance == null) { // Вторая проверка (с блокировкой)
                    instance = new Singleton(); // Опасная строка! Не является атомарной
                }
            }
        }
        return instance;
}
}