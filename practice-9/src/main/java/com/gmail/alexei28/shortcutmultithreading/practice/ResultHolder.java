package com.gmail.alexei28.shortcutmultithreading.practice;

/**
 * Класс для демонстрации happens-before через Thread.join().
 */
/*
  Правило завершения потока*:
  Все действия в потоке happens-before успешного возврата из join() в другом потоке.
    - если поток А выполнил threadB.join(), то поток А гарантированно увидит все результаты работы потока B.
 */
public class ResultHolder {
    private int result;

    public void setResult(int i) {
        this.result = i;
    }

    public int getResult() {
        return result;
    }
}