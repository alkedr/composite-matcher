package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class CollectionMatcherForExtending<T, U extends CollectionMatcherForExtending<T, U>> extends ObjectMatcherForExtending<Collection<T>, U> {
    public U size(int value) {
        return size(equalTo(value));
    }

    private U size(Matcher<? super Integer>... matchers) {
        return property(on(Collection.class).size()).is(matchers);
    }

    private U size(List<? extends Matcher<? super Integer>> matchers) {
        return property(on(Collection.class).size()).is(matchers);
    }
}
