package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Класс для работы с асинхронными задачами через CompletableFuture.
 *
 * Задание: Реализуйте методы так, чтобы все тесты проходили.
 *
 * Подсказки:
 * - Используйте CompletableFuture.supplyAsync() для создания асинхронных задач
 * - thenApply() преобразует результат и возвращает новый CompletableFuture
 * - thenCompose() "разворачивает" вложенный CompletableFuture (аналог flatMap)
 * - thenCombine() комбинирует результаты двух независимых CompletableFuture
 * - allOf() ждет завершения всех задач
 * - exceptionally() обрабатывает только ошибки
 * - handle() обрабатывает и успех, и ошибку
 */
public class AsyncTaskProcessor {
    // supplyAsync() создает CompletableFuture, который асинхронно выполняет задачу, описанную в Supplier.
    // thenApply() принимает функцию, которая преобразует результат CompletableFuture
    // и возвращает новый CompletableFuture с преобразованным результатом.
    public CompletableFuture<String> createTransformationChain(String hello) {
        return CompletableFuture.supplyAsync(() -> hello)
                .thenApply(s -> (s + " world").toUpperCase());
    }

    // thenCompose() позволяет плоско скомбинировать две асинхронные операции, где вторая зависит от результата первой.
    // thenCompose автоматически “расплющивает” результат.
    // Это похоже на flatMap в Stream API.
    public CompletableFuture<String> createComposition(int i) {
        return CompletableFuture.supplyAsync(() -> i)
                .thenCompose((Function<Integer, CompletableFuture<String>>)
                        firstResult -> CompletableFuture.supplyAsync(()
                                -> "Result: "+ firstResult));
    }

    // thenCombine() комбинирует результаты двух независимых CompletableFuture после их завершения.
    public CompletableFuture<String> combineResults(String hello, String world) {
        // thenCombine() - комбинирует два независимых CompletableFuture
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> hello);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> world);
        return future1.thenCombine(future2, (result1, result2) -> result1 + " " + result2);
    }

    // allOf() - ждет завершения всех CompletableFuture
    public CompletableFuture<Void> waitForAll(CompletableFuture<String> future1, CompletableFuture<String> future2, CompletableFuture<String> future3) {
        return CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> future1),
                CompletableFuture.supplyAsync(() -> future2),
                CompletableFuture.supplyAsync(() -> future3)
        );
    }

    // exceptionally() обрабатывает исключение и предоставляет альтернативный результат. Этот метод похож на блок catch.
    public CompletableFuture<String> handleError(boolean b) {
        return
                CompletableFuture.supplyAsync(() -> {
                    if (b) {
                        throw new RuntimeException("Что-то пошло не так!");
                    }
                    return "Успешный результат";
                }).exceptionally(new Function<Throwable, String>() { // вызывается только при ошибке
                    @Override
                    public String apply(Throwable throwable) {
                        return "Обработано";
                    }
                });
    }

    // handle() нужен для универсальной обработки результата — и успеха, и ошибки — в одном месте.
    // Это как finally + можно изменить результат. Т.е. вызывается всегда и МОЖЕТ изменить результат.
    public CompletableFuture<String> handleSuccessOrError(boolean b) {
        return CompletableFuture.supplyAsync(() -> {
            if (b) {
                throw new RuntimeException("Что-то пошло не так!");
            }
            return "УСПЕХ";
        }).handle(new BiFunction<String, Throwable, String>() {
            @Override
            public String apply(String result, Throwable throwable) { // всегда вызывается, независимо от исхода
                if (throwable != null) {
                    return "Обработана ошибка";
                }
                return result; // значение, если всё прошло успешно
            }
        });
    }

    // thenAccept() принимает Consumer, который потребляет результат, но ничего не возвращает.
    public void processResult(int i, Consumer<Integer> consumer) {
         CompletableFuture.supplyAsync(() -> i)
                 .thenAccept(consumer);
    }
}