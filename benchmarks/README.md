# Бенчмарки многопоточности

Этот модуль демонстрирует **реальную разницу в производительности** между различными подходами к многопоточности.

## Требования

- **Java 21+** (для Virtual Threads)

## Запуск

```bash
cd benchmarks
mvn compile exec:java
```

## Что измеряем

### 1. CPU-bound задачи (вычисления)
Задачи, где основная нагрузка на процессор.

**Ожидаемый результат:**
- `ForkJoinPool` и `FixedThreadPool(cores)` — быстрее всего
- Virtual Threads — такие же или чуть медленнее
- Слишком много потоков — overhead от переключения контекста

### 2. IO-bound задачи (сеть, диск)
Задачи, где потоки в основном ждут.

**Ожидаемый результат:**
- Virtual Threads — максимально быстро
- `FixedThreadPool(100+)` — быстро, но дорого по памяти
- `FixedThreadPool(4)` — медленно (потоки заблокированы)

### 3. Mixed (смешанные)
Комбинация CPU и IO операций.

## Пример вывода

```
=== CPU-BOUND BENCHMARK (100,000 задач) ===
Sequential:           12,340 ms
FixedThreadPool(4):    3,120 ms  (x3.95 faster)
FixedThreadPool(100):  3,450 ms  (x3.58 faster)
Virtual Threads:       3,280 ms  (x3.76 faster)
ForkJoinPool:          2,980 ms  (x4.14 faster)

=== IO-BOUND BENCHMARK (1,000 задач, 100ms delay) ===
Sequential:          100,120 ms
FixedThreadPool(4):   25,030 ms  (x4.00 faster)
FixedThreadPool(100):  1,120 ms  (x89.4 faster)
Virtual Threads:       1,050 ms  (x95.4 faster)

=== THREAD CREATION OVERHEAD ===
Platform Thread:       1.2 ms / thread
Virtual Thread:        0.002 ms / thread
```

## Ключевые выводы

| Тип задачи | Лучший выбор | Почему |
|------------|--------------|--------|
| CPU-bound | ForkJoinPool | Work-stealing, нет overhead |
| IO-bound | Virtual Threads | Миллионы потоков, дешево |
| Mixed | Virtual Threads | Универсальность |
| Legacy (Java < 21) | FixedThreadPool | Баланс ресурсов |

## Типичные ошибки

1. **CachedThreadPool для IO-bound** — создаст тысячи потоков, убьет систему
2. **FixedThreadPool(4) для IO-bound** — потоки простаивают, медленно
3. **Virtual Threads для CPU-bound** — нет выигрыша, может быть overhead
4. **Создание потоков вручную** — дорого, нет переиспользования
