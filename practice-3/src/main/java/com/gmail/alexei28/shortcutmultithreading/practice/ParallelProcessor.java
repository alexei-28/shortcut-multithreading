package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Класс для параллельной обработки данных.
 * <p>
 * Задание: Реализуйте метод processInParallel так, чтобы все тесты проходили.
 * <p>
 * Подсказки:
 * - Используйте ExecutorService для параллельного выполнения
 * - Преобразуйте каждый элемент входного списка в задачу
 * - Соберите результаты из Future
 */

/*
    Метод processInParallel
        - берёт список чисел
        - применяет к каждому function
        - делает это параллельно с помощью пула потоков
        - возвращает список результатов

    Что происходит параллельно:
    Допустим:
        input = [1,2,3,4,5]
        threadCount = 3

    Пул делает примерно так:
        Thread-1 → обрабатывает 1
        Thread-2 → обрабатывает 2
        Thread-3 → обрабатывает 3

        (после завершения)
        Thread-1 → обрабатывает 4
        Thread-2 → обрабатывает 5
 */
public class ParallelProcessor {

    public List<Integer> processInParallel(List<Integer> input, Function<Integer, Integer> function, int threadCount) {
        /*
            Что происходит:
                - создаётся пул из фиксированного количества потоков
                - максимум threadCount задач выполняются одновременно
                - остальные — ждут в очереди
            Почему это важно:
                - контролируешь нагрузку (не создаём 1000 потоков)
                - избегаем OutOfMemoryError
        */
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futuresList = new ArrayList<>();
        try {
            for (Integer number : input) {
            /*
              Для каждого элемента number:
                 1. создаётся задача (Callable)
                 2. отправляется в пул (submit)
                 3. возвращается Future

              Future<Integer> — это:
                - "обещание", что результат появится позже
                - даёт доступ к результату через блокирующий метод get()
             */
                Future<Integer> future = executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        /*
                            Каждый поток делает: function.apply(number);
                            То есть:
                              - берёт своё число
                              - обрабатывает его
                              - возвращает результат
                        */
                        Integer result = function.apply(number); // возьми функцию и примени её к значению number
                        return result;
                    }
                });
                futuresList.add(future);
            }

            // Сбор результатов
            List<Integer> resultsList = new ArrayList<>();
            for (Future<Integer> future : futuresList) {
                /*
                    - future.get() — блокирующий вызов
                    - если задача не завершена → поток ждёт
                   Несмотря на параллельное выполнение, результаты собираются последовательно.
                   Но:
                    - сами задачи уже выполняются параллельно
                    - get() просто дожидается готовности
                */
                resultsList.add(future.get());
            }
            return resultsList;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // важно восстановить interrupt-флаг
            throw new RuntimeException("Thread was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Task execution failed", e);
        } finally {
            /*
               shutdown() это мягкое завершение:
                - Говорит пулу: "новые задачи больше не принимаем"
                - Но уже отправленные задачи:
                  - продолжают выполняться
                  - НЕ прерываются

               Если не вызвать shutdown():
                  -потоки остаются живыми
                - JVM не завершится (в тестах может зависнуть)
                - утечка ресурсов
                - особенно критично в long-running приложениях
            */
            executor.shutdown();
            try {
                /*
                   - ждём максимум 10 секунд
                   - если не завершился -> форс остановка (shutdownNow)
                 */
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                /*
                   shutdownNow() это жёсткое завершение:
                     - пытается прервать потоки (interrupt)
                     - не гарантирует завершение
                */
                executor.shutdownNow();
                // восстанавливаем interrupt
                Thread.currentThread().interrupt();
            }
        }
    }
}
