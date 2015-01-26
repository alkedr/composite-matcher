package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class MapMatcherForExtending<Key, Value, U extends MapMatcherForExtending<Key, Value, U>> extends ObjectMatcherForExtending<Map<Key, Value>, U> {
    public U size(int value) {
        return size(equalTo(value));
    }

    private U size(Matcher<? super Integer>... matchers) {
        return property(on(Map.class).size()).is(matchers);
    }

    private U size(List<? extends Matcher<? super Integer>> matchers) {
        return property(on(Map.class).size()).is(matchers);
    }


    @SafeVarargs
    public final U valueOf(Key key, Matcher<? super Value>... matchers) {
        return null;
    }

    public U valueOf(Key key, Collection<Matcher<? super Value>> matchers) {
        return null;
    }


    @SafeVarargs
    public final U valuesOf(Matcher<Key> keyMatcher, Matcher<? super Value>... matchers) {
        return null;
    }

    public U valuesOf(Matcher<Key> keyMatcher, Collection<Matcher<? super Value>> matchers) {
        return null;
    }
}
