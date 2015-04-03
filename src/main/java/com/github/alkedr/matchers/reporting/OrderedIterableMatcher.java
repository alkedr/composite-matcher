package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

/**
 * Плевать на случаи, когда проверки зависят от actual
 * Юзкейсы:
 * - просто перечислено несколько элементов
 *   orderedIterable("foo", "bar", "baz")  // хотя бы один элемент должен быть не нулл, можно Iterable
 *   orderedIterable(String.class, "foo", "bar", "baz")
 *   orderedIterable(String.class).items("foo", "bar", "baz");
 *   orderedIterable(String.class).item("foo").item("bar").item("baz");
 * - перечислено несколько матчеров
 *   orderedIterable(String.class, contains("foo"), contains("bar"), contains("baz"))  // нуллы пропускаются, можно Iterable
 *   orderedIterable(String.class).items(contains("foo"), contains("bar"), contains("baz"));
 *   orderedIterable(String.class).item(contains("foo")).item(contains("bar")).item(contains("baz"));
 * - некоторые элементы проверяются по значению, а некоторые матчерами
 *   orderedIterable(String.class).item("foo").item("bar").item(contains("baz"));
 * - проверяем не все элементы
 *   orderedIterable(String.class).item(2, "bar").item(3, contains("baz"));
 *
 *
 *   - элементы указаны в коде
 *   - элементы или матчеры для них берутся из списка
 *
 *   - порядок важен
 *   - прядок не важен
 *
 *   - непроверенные элементы - ошибка
 *   - непроверенные элементы - это нормально
 *   - непроверенные элементы не важны, даже в отчёте отображать не надо
 *
 * Нужно задать:
 *   - List<List<Matcher<E>>> - матчеры для элементов
 *   - Что делать с непроверенными элементами в конце списка?
 *   - Что делать если между двумя элементами, для которых есть матчеры, стоит третий?
 *       Элементы:  1, 2, 2, 3, 4
 *       Матчеры: equalTo(1), equalTo(2), equalTo(3), equalTo(4)
 *       Хотим в отчёте красную двойку
 *       actionOnMismatch(TRY_TO_SKIP)  ??   В идеале нужно в отчёте отображать стрелочками куда переместились элементы
 *   - Что делать если одного элемента нет?
 *       Элементы:  1, 3, 4
 *       Матчеры: equalTo(1), equalTo(2), equalTo(3), equalTo(4)
 *       Хотим в отчёте красную двойку
 *       actionOnMismatch(TRY_TO_SKIP)  ??   В идеале нужно в отчёте отображать стрелочками куда переместились элементы
 *
 *       НУЖЕН ПОЛНОЦЕННЫЙ ДИФФ?
 *
 *   *CollectionMatcher - умеет считать элементы с конца если передать отрицательный индекс?
 *
 *
 *   Матчеры для XML'ек и JSON, которые в отчёте отображают саму XML'ку или JSON и подсвечивают зелёным или красным
 *   проверенные вещи
 *   Надо скармливать матчеру текст, не бин
 *   Probably not worth it
 *
 **/
public interface OrderedIterableMatcher<E, T extends Iterable<E>> extends ReportingMatcher<T> {
    OrderedIterableMatcher<E, T> item(int index, Matcher<E> matcher);
    OrderedIterableMatcher<E, T> item(int index, Matcher<E>... matchers);
    OrderedIterableMatcher<E, T> item(int index, Iterable<Matcher<E>> matchers);

    OrderedIterableMatcher<E, T> item();
    OrderedIterableMatcher<E, T> item(Matcher<E> matcher);
    OrderedIterableMatcher<E, T> item(Matcher<E>... matchers);
    OrderedIterableMatcher<E, T> item(Iterable<Matcher<E>> matchers);

    // TODO: устанавливать поведение для unexpected

//    OrderedIterableMatcher<E, T> items(int count);
//    OrderedIterableMatcher<E, T> items(int count, Matcher<T> matcher);
//    OrderedIterableMatcher<E, T> items(int count, Matcher<T>... matchers);
//    OrderedIterableMatcher<E, T> items(int count, List<Matcher<T>> matchers);
//
//    OrderedIterableMatcher<E, T> items(int beginIndex, int count, Matcher<T> matcher);
//    OrderedIterableMatcher<E, T> items(int beginIndex, int count, Matcher<T>... matchers);
//    OrderedIterableMatcher<E, T> items(int beginIndex, int count, List<Matcher<T>> matchers);
//
//    OrderedIterableMatcher<E, T> itemsRange(int beginIndex, int endIndex, Matcher<T> matcher);
//    OrderedIterableMatcher<E, T> itemsRange(int beginIndex, int endIndex, Matcher<T>... matchers);
//    OrderedIterableMatcher<E, T> itemsRange(int beginIndex, int endIndex, List<Matcher<T>> matchers);
}
