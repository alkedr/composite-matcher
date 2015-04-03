package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;

// Всегда проходит по всему итераблу
public interface UnorderedIterableMatcher<E, T extends Iterable<E>> extends ReportingMatcher<Iterable<T>> {
    UnorderedIterableMatcher<E, T> item(Matcher<T> matcher);
    UnorderedIterableMatcher<E, T> item(Matcher<T>... matchers);
    UnorderedIterableMatcher<E, T> item(List<Matcher<T>> matchers);

    UnorderedIterableMatcher<E, T> items(int count, Matcher<T> matcher);
    UnorderedIterableMatcher<E, T> items(int count, Matcher<T>... matchers);
    UnorderedIterableMatcher<E, T> items(int count, List<Matcher<T>> matchers);

    UnorderedIterableMatcher<E, T> items(Matcher<T> matcher);
    UnorderedIterableMatcher<E, T> items(Matcher<T>... matchers);
    UnorderedIterableMatcher<E, T> items(List<Matcher<T>> matchers);
}
