package com.gmail.alexei28.shortcutmultithreading.practice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для работы с виртуальными потоками (Java 21+).
 *
 * Задание: Реализуйте метод executeWithVirtualThreads так, чтобы все тесты проходили.
 *
 * Подсказки:
 * - Используйте Executors.newVirtualThreadPerTaskExecutor() для создания ExecutorService
 * - Виртуальные потоки идеальны для I/O-bound задач
 * - Не забывайте закрывать ExecutorService (try-with-resources)
 */
public class VirtualThreadExecutor {
    // Потокобезопасный счётчик, чтобы считать завершённые задачи из разных потоков
    private final AtomicInteger completedTask = new AtomicInteger(0);

    /*
       Метод executeWithVirtualThreads:
        1. создаёт executor с виртуальными потоками
        2. запускает каждую задачу в отдельном виртуальном потоке
        3. увеличивает счётчик в finally (даже при ошибках)
        4. ждёт завершения всех задач через Future.get()
        5. закрывает executor
        6. возвращает количество завершённых задач

      finally означает, что задача завершилась.
      Но этого недостаточно, чтобы метод знал, что ВСЕ задачи завершены. Поэтому нужен Future.get()).
    */
    public int executeWithVirtualThreads(List<Runnable> tasksList) {
        /*
            - создаётся пул виртуальных потоков
            - каждая задача получает отдельный виртуальный поток
            - try-with-resources автоматически вызывает executor.close()
           Когда блок try заканчивается:
             - executor корректно завершает работу
             - новые задачи больше не принимаются
        */
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futuresList = new ArrayList<>();
            for (Runnable task : tasksList) {
                /*
                 Каждый task:
                    - оборачивается в новый Runnable
                    - отправляется в executor
                    - получает Future<?>
                */
                futuresList.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("try, " + LocalDateTime.now() + ", thread = " + Thread.currentThread() + ", completedTask = " + completedTask.get());
                            task.run();
                        } finally {
                            // гарантирует инкремент даже если task упал с исключением
                            // значит счётчик = "сколько задач реально завершилось", а не "успешно"
                            completedTask.incrementAndGet();
                            System.out.println("finally, " + LocalDateTime.now() + ", thread = " + Thread.currentThread() + ", completedTask = " + completedTask.get());
                        }
                    }
                }));
            } // end for

            /*
                Ожидание завершения задач.
                future.get():
                  - блокирует текущий поток
                  - ждёт завершения конкретной задачи
            */
            for (Future<?> future : futuresList) {
                try {
                    System.out.println("future_get, " + LocalDateTime.now() + ", thread = " + Thread.currentThread() + ", completedTask = " + completedTask.get());
                    future.get(); // гарантирует, что ты мы выйдем из метода, пока задачи не завершены
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } // try-with-resources
        /*
           После выхода из try-with-resources:
            - executor закрывается автоматически
            - виртуальные потоки завершаются
        */
        System.out.println("get_all_completedTask, " + LocalDateTime.now() + ", thread = " + Thread.currentThread() + ", completedTask = " + completedTask.get());
        // Метод не вернётся, пока ВСЕ задачи не завершены
        return completedTask.get();
    }
}
