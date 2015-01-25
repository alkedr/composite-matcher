package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.Map;

public interface MapMatcherForImplementing<Key, Value, U extends MapMatcherForImplementing<Key, Value, U>>
        extends ValueExtractingMatcherForImplementing<Map<Key, Value>, U> {
    U valueOf(Key key, Matcher<? super Value>... matchers);
    U valueOf(Key key, Collection<Matcher<? super Value>> matchers);
    U valueOf(Key key, Matcher<? super Value> matchers);
    U valuesOf(Matcher<Key> keyMatcher, Matcher<? super Value>... matchers);
    U valuesOf(Matcher<Key> keyMatcher, Collection<Matcher<? super Value>> matchers);
    U valuesOf(Matcher<Key> keyMatcher, Matcher<? super Value> matchers);
}
