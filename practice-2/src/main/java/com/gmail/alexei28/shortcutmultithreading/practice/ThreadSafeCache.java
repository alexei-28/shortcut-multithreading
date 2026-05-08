package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
     ReadWriteLock значительно повышает производительность для данных, которые часто читают, но редко меняют.
     ReadWriteLock разделяет доступ на две фазы: чтение и запись.
     Суть проста: «Читатели» не мешают друг другу, но «Писатель» требует абсолютного одиночества.
     В отличие от обычного Lock (или synchronized), где только один поток может работать, ReadWriteLock позволяет:
       - нескольким потокам одновременно читать
       - только одному потоку писать
       - во время записи никто не читает и не пишет

     Используйте ReadWriteLock, если:
       - много чтений, мало записей
       - кэш
       - справочники (map, config)
       - статистика

      Методы интерфейса:
       - Lock readLock() — возвращает замок для чтения.
       - Lock writeLock() — возвращает замок для записи.
 */

/**
 * Потокобезопасный кэш с использованием ReadWriteLock.
 * 4. Реализуйте кэш с использованием ReadWriteLock
 */
public class ThreadSafeCache<K, V> {
    private final Map<String, String> cache = new HashMap<>();
    private final ReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    public String get(String key) {
        // Read Lock (Shared):
        // Если ни один поток не удерживает блокировку на запись,
        // то любое количество потоков может одновременно удерживать блокировку на чтение.
        reentrantReadWriteLock.readLock().lock(); // Захватываем замок на чтение -> Вход разрешен многим читателям
        try {
            return cache.get(key);
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }

    public void put(String key, String value) {
        // Write Lock (Exclusive):
        // Если поток хочет записать данные,он должен дождаться, пока все «читатели» закончат работу и пока другие «писатели» освободят замок.
        // Пока активен писатель, никто другой (ни читатель, ни другой писатель) не может получить доступ.
        reentrantReadWriteLock.writeLock().lock(); // Захватываем эксклюзивный замок на запись -> Ждет, пока уйдут все читатели и писатели
        try {
            cache.put(key, value);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }
}