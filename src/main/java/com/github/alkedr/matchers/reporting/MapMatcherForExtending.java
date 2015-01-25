package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.Map;

public class MapMatcherForExtending<Key, Value, U extends MapMatcherForExtending<Key, Value, U>> extends ValueExtractingMatcherForExtending<Map<Key, Value>, U> {
    @SafeVarargs
    public final U valueOf(Key key, Matcher<? super Value>... matchers) {
        return null;
    }

    public U valueOf(Key key, Collection<Matcher<? super Value>> matchers) {
        return null;
    }

    public U valueOf(Key key, Matcher<? super Value> matchers) {
        return null;
    }


    @SafeVarargs
    public final U valuesOf(Matcher<Key> keyMatcher, Matcher<? super Value>... matchers) {
        return null;
    }

    public U valuesOf(Matcher<Key> keyMatcher, Collection<Matcher<? super Value>> matchers) {
        return null;
    }

    public U valuesOf(Matcher<Key> keyMatcher, Matcher<? super Value> matchers) {
        return null;
    }
}
