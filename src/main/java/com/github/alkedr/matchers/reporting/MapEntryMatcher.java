package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;

public interface MapEntryMatcher<K, V, T extends Map.Entry<K, V>> extends ObjectMatcher<T> {
    MapEntryMatcher<K, V, T> key(Matcher<K> matcher);
    MapEntryMatcher<K, V, T> key(Matcher<K>... matchers);
    MapEntryMatcher<K, V, T> key(List<Matcher<K>> matchers);

    MapEntryMatcher<K, V, T> value(Matcher<V> matcher);
    MapEntryMatcher<K, V, T> value(Matcher<V>... matchers);
    MapEntryMatcher<K, V, T> value(List<Matcher<V>> matchers);
}
