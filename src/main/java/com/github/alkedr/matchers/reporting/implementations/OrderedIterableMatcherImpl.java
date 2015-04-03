package com.github.alkedr.matchers.reporting.implementations;

import com.github.alkedr.matchers.reporting.OrderedIterableMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OrderedIterableMatcherImpl<E, T extends Iterable<E>, U extends OrderedIterableMatcherImpl<E, T, U>> extends ReportingMatcherImpl<T, U> implements OrderedIterableMatcher<E, T> {
    private final List<Collection<Matcher<E>>> matchers = new ArrayList<>();
    private int currentIndex = 0;


    @Override
    public OrderedIterableMatcher<E, T> item(int index, Matcher<E> matcher) {
        getMatchersCollectionForItemIndex(index).add(matcher);
        return this;
    }

    @Override
    public OrderedIterableMatcher<E, T> item(int index, Matcher<E>... matchers) {
        Collections.addAll(getMatchersCollectionForItemIndex(index), matchers);
        return this;
    }

    @Override
    public OrderedIterableMatcher<E, T> item(int index, Iterable<Matcher<E>> matchers) {
        Collection<Matcher<E>> matcherCollection = getMatchersCollectionForItemIndex(index);
        for (Matcher<E> matcher : matchers) matcherCollection.add(matcher);
        return this;
    }

    @Override
    public OrderedIterableMatcher<E, T> item() {
        return item(currentIndex++);
    }

    @Override
    public OrderedIterableMatcher<E, T> item(Matcher<E> matcher) {
        return item(currentIndex++, matcher);
    }

    @Override
    public OrderedIterableMatcher<E, T> item(Matcher<E>... matchers) {
        return item(currentIndex++, matchers);
    }

    @Override
    public OrderedIterableMatcher<E, T> item(Iterable<Matcher<E>> matchers) {
        return item(currentIndex++, matchers);
    }


    @Override
    public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
        boolean result = true;
        int i = 0;
        if (item instanceof Iterable) {
            Iterator<E> iterator = ((Iterable<E>) item).iterator();
            while (iterator.hasNext() && i < matchers.size()) {
                E element = iterator.next();
                result &= normalValue(listener, String.valueOf(i), element, matchers.get(i));
                i++;
            }
            while (iterator.hasNext()) {
                E element = iterator.next();
                result &= unexpectedValue(listener, String.valueOf(i), element, null);
                i++;
            }
        }
        for (; i < matchers.size(); i++) {
            result &= missingValue(listener, String.valueOf(i), matchers.get(i));
        }
        return result;
    }


    private Collection<Matcher<E>> getMatchersCollectionForItemIndex(int index) {
        while (matchers.size() <= index) matchers.add(null);
        Collection<Matcher<E>> matchersForIndex = matchers.get(index);
        if (matchersForIndex == null) {
            matchersForIndex = new ArrayList<>();
            matchers.set(index, matchersForIndex);
        }
        return matchersForIndex;
    }
}
