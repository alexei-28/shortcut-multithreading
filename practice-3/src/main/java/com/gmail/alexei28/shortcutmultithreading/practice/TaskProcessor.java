package com.gmail.alexei28.shortcutmultithreading.practice;

import java.util.List;
import java.util.concurrent.*;

/**
 * Класс для обработки задач через ExecutorService.
 * <p>
 * Задание: Реализуйте методы так, чтобы все тесты проходили.
 * <p>
 * Подсказки:
 * - Используйте Executors.newFixedThreadPool() для создания пула потоков
 * - Не забывайте вызывать shutdown() и awaitTermination()
 * - Правильно обрабатывайте исключения при получении результатов Future
 */

/*
    Обёртка над пулом потоков.
     - принимает задачи (Callable<Integer>)
     - отправляет их в пул потоков
     - возвращает Future<Integer> (результат “в будущем”)
     - умеет корректно завершать работу пула

    По сути это мини-менеджер асинхронного выполнения задач.

    Пример:
    TaskProcessor p = new TaskProcessor(2);
    И у тебя:
      - 2 потока (threadCount): T1, T2
      - 10 задач (task)
    выполнится так:
      - T1 выполняет task1 → task3 → task5...
      - T2 выполняет task2 → task4 → task6...
      - остальные ждут в очереди
 */
public class TaskProcessor {
    private final ExecutorService executor;

    public TaskProcessor(int threadCount) {
        // Создаем пул потоков фиксированного размерам..
        // Если задач больше, чем потоков, они будут поставлены в очередь и выполнены по мере освобождения потоков.
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    /*
        processTask — универсальный исполнитель задач, а не “конкретная бизнес-логика”.
        Этот метод:
            - ничего не знает о taskId (in test testFixedThreadPool)
            - не знает про Thread.sleep (in test testFixedThreadPool)
            - просто выполняет любую задачу

        То есть это “выполни что угодно асинхронно”.
        1. Callable<Integer> - это задача, которая:
             - выполняется в другом потоке
             - возвращает результат (Integer)
             - может бросать исключение
        2. submit(task)
             - он НЕ выполняет задачу сразу в том же потоке
             - он только ставит её в пул
             - один из потоков из ExecutorService берёт её
             - выполняет call() (т.е. лямбду из теста testFixedThreadPool())
             - возвращает Future<Integer> - "обещание результата"
    */
    public Future<Integer> processTask(Callable<Integer> task) {
        // Выполняется именно тот код, который был передан в submit(), но выполняется он в отдельном потоке из пула, а не в тесте.
        return executor.submit(task);
    }

    public boolean shutdownGracefully(long timeout, TimeUnit unit) {
        executor.shutdown(); // Перестаём принимать новые задачи. Уже отправленные задачи продолжают выполняться
        try {
            if (!executor.awaitTermination(timeout, unit)) { // “подожди, пока все задачи завершатся”
                List<Runnable> droppedTasks = executor.shutdownNow(); // всё НЕ успело завершиться
                System.out.println("shutdownGracefully, всё НЕ успело завершиться, отменено " + droppedTasks.size() + " задач");
                return executor.awaitTermination(timeout, unit); // снова ждём
            }
            return true; // всё успело завершиться
        } catch (InterruptedException e) { // Если поток прервали:
            // Method shutdownNow():
            // - пытается остановить всё
            // - прерывает потоки
            // - возвращает список задач, которые НЕ успели выполниться
            executor.shutdownNow();

            Thread.currentThread().interrupt(); // восстанавливаем статус прерывания, чтобы не потерять сигнал прерывания
            return false;
        }
    }

    /*
        Метод executor.isTerminated() возвращает true ТОЛЬКО если:
          - shutdown() уже вызван
          - все задачи выполнены
          - все потоки остановлены

        Важно: isShutdown() не равно isTerminated()

        Жизненный цикл:
        RUNNING
           ↓ shutdown()
        SHUTTING DOWN
           ↓ все задачи завершены
        TERMINATED → true
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }
}
