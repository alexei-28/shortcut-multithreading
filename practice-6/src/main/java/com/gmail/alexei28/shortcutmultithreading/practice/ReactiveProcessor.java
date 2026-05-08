package com.gmail.alexei28.shortcutmultithreading.practice;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Класс для работы с реактивными потоками (Project Reactor).
 *
 * Задание: Реализуйте методы так, чтобы все тесты проходили.
 *
 * Подсказки:
 * - Mono.just() создает Mono с одним значением
 * - Flux.just() создает Flux с несколькими значениями
 * - map() преобразует каждый элемент в потоке
 * - filter() отфильтровывает элементы по условию
 * - flatMap() преобразует элемент в поток и "разворачивает" его
 * - Flux.merge() объединяет несколько потоков
 * - Flux.zip() комбинирует элементы из потоков попарно
 * - onErrorReturn() возвращает значение при ошибке
 * - onErrorResume() выполняет другой поток при ошибке
 */
public class ReactiveProcessor {

    /*
        Mono.just() — это фабричный метод из Project Reactor, который создаёт Mono с уже известным значением.
        Это способ обернуть готовый результат в реактивный поток.
    */
    public Mono<String> createMono(String hello) {
        return Mono.just(hello); // Вот значение hello, оберни его в Mono и отдай подписчику
    }

    // Flux.just(): Создает поток (Flux) из фиксированного набора элементов.
    public Flux<Integer> createFlux(int i, int i1, int i2, int i3, int i4) {
        return  Flux.just(i, i1, i2, i3, i4);
    }

    public Flux<String> transformToUpperCase(Flux<String> just) {
        return just.map(String::toUpperCase);
    }

    public Flux<Integer> filterEvenNumbers(Flux<Integer> just) {
        return just.filter(integer -> integer % 2 == 0);
    }

    // flatMap() преобразует каждый элемент в Publisher (обычно Mono или Flux) и “разворачивает” результат в один общий поток.
    // flatMap убирает вложенность, которая может возникать при работе с асинхронными операциями, возвращающими потоки.
    // flatMap не гарантирует сохранение порядка элементов, если внутренние потоки асинхронны.
    public Flux<Integer> expandWithFlatMap(Flux<Integer> flux) {
        return flux.flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Integer i) {
                // На каждый входящий элемент i создается новый Flux, содержащий сам элемент и его удвоенную сумму.
                // Если на вход придет последовательность 1, 2, 3, то flatMap сгенерирует три мини-потока: [1, 2], [2, 4] и [3, 6].
                return Flux.just(i, i + i);
            }
        });
    }

    // merge() - сливает несколько потоков в один. Элементы будут появляться в результирующем потоке по мере их готовности
    // из исходных потоков (неупорядоченно).
    public Flux<String> mergeFluxes(Flux<String> flux1, Flux<String> flux2) {
        return Flux.merge(flux1, flux2);
    }

    // zip() - комбинирует элементы из потоков попарно. Он ждет, пока в каждом потоке появится элемент, затем объединяет
    // их в кортеж (Tuple) и выдает его.
    // Результирующий поток завершится, когда завершится самый короткий из исходных потоков.
    public Flux<String> zipFluxes(Flux<String> names, Flux<Integer> ages) {
        return Flux.zip(names, ages, (s, i) -> s + " is " + i);
    }

    // onErrorReturn(value) - если в потоке происходит ошибка, он немедленно завершается и выдает указанное значение value.
    public Flux<String> handleErrorWithReturn(Flux<String> flux, String errorMessage) {
        return flux.onErrorReturn(errorMessage); // ← Возвращает альтернативное значение при ошибке
    }

    /*
        onErrorResume(function): Если в потоке происходит ошибка, вместо него выполняется другой реактивный поток, возвращаемый function.
        Это позволяет реализовать сложную логику восстановления.
        onErrorResume() - если в потоке произошла ошибка — не падать, а переключиться на другой Publisher
        Пример поведения
         flux:
            'A → B → ERROR → C'
         recovery:
            "X → Y → Z"
         итог:
            'A → B' → "X → Y → Z"

         'C' уже никогда не будет получен.
    */
    public Flux<String> handleErrorWithResume(Flux<String> flux, Flux<String> recovery) {
        return flux
                .onErrorResume(new Function<Throwable, Publisher<? extends String>>() {
                    @Override
                    public Publisher<? extends String> apply(Throwable throwable) {
                        // возвращается recovery -> подписка продолжается уже на recovery -> recovery полностью заменяет поток
                        return recovery;
                    }
                });
    }
}