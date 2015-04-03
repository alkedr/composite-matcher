package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;

public interface SublistMatcher<E, T extends Iterable<E>> extends ReportingMatcher<T> {

    SublistMatcher<E, T> sublist(String name, Matcher<E> elementSelector, Matcher<Iterable<E>> matcher);


    SublistCheckAdder<E, T> elementsThatAre(Matcher<E> matcher);


    interface SublistCheckAdder<E, T extends Iterable<E>> {
        SublistMatcher<E, T> alsoAre(Matcher<E> matcher);
        SublistMatcher<E, T> alsoAre(Matcher<E>... matchers);
        SublistMatcher<E, T> alsoAre(List<Matcher<E>> matchers);
    }
}
