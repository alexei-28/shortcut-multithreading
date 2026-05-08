package com.gmail.alexei28.shortcutmultithreading.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
     * ЗАДАЧА: Найдите и исправьте race condition в кэше.
     *
     * Проблема: Check-then-act операция не атомарна.
     * Два потока могут одновременно проверить, что ключа нет,
     * и оба начнут вычислять значение.
     *
     * Подсказка: Используйте ConcurrentHashMap.computeIfAbsent()
     * или синхронизацию.
     */
    public class BrokenCache {
        private final Map<String, String> cache = new ConcurrentHashMap<>();
        private int computeCount = 0;

        /**
         * Возвращает значение из кэша или вычисляет его.
         * BUG: Check-then-act не атомарно!
         */
        public String getOrCompute(String key) {
            return cache.computeIfAbsent(key, s -> computeValue(key));
        }

        /**
         * Имитация тяжелого вычисления.
         */
        private String computeValue(String key) {
            computeCount++;
            try {
                Thread.sleep(50);  // Имитация задержки
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "value_for_" + key;
        }

        /**
         * Возвращает количество вычислений (для тестирования).
         * При правильной реализации для одного ключа должно быть ровно 1 вычисление.
         */
        public int getComputeCount() {
            return computeCount;
        }

        public void clear() {
            cache.clear();
            computeCount = 0;
        }
}
