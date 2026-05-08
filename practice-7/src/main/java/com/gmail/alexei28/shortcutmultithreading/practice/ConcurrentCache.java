package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Потокобезопасный кэш с использованием ConcurrentHashMap.
 *
 * Задание: Реализуйте методы так, чтобы все тесты проходили.
 *
 * Подсказки:
 * - ConcurrentHashMap использует тонкую блокировку на уровне сегментов
 * - computeIfAbsent() атомарно добавляет значение, если ключа нет
 * - compute() атомарно обновляет значение
 * - merge() атомарно объединяет значения
 */
public class ConcurrentCache {
    private final ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();

    public void put(String key, int value) {
        cache.put(key, value);
    }

    public int size() {
        return cache.size();
    }

    /*-
        Атомарно увеличивает счётчик по ключу.
        Потокобезопасность:
         - полностью потокобезопасно
        Почему:
        - compute() выполняется атомарно для конкретного ключа
        - блокировка идёт на bucket/segment уровня внутри ConcurrentHashMap
     */
    public void incrementCounter(String key) {
        cache.compute(key, (k, v) -> v == null ? 1 : v + 1);
    }

    public AtomicInteger getCounter(String key) {
        return new AtomicInteger(cache.getOrDefault(key, 0));
    }

    /*
        Позволяет пользователю изменить значение по ключу через функцию.
        Потокобезопасность:
         - безопасно (atomic per key)
     */
    public void computeValue(String key, BiFunction<String, Integer, Integer> biFunction) {
        cache.compute(key, biFunction);
    }

    public int get(String key) {
        return cache.getOrDefault(key, 0); // Безопасно
    }

    /*
        Объединяет текущее значение с новым через функцию.
        Потокобезопасность:
         - полностью потокобезопасно
     */
    public void mergeValue(String key, int i, BiFunction<Integer, Integer, Integer> biFunction) {
        cache.merge(key, i, biFunction);
    }
}