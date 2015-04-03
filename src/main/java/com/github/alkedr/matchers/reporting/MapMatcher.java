package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;

public interface MapMatcher<K, V, T extends Map<K, V>> extends ReportingMatcher<T> {
    MapMatcher<K, V, T> entry(K key, Matcher<V> valueMatcher);
    MapMatcher<K, V, T> entry(K key, Matcher<V>... valueMatchers);
    MapMatcher<K, V, T> entry(K key, List<Matcher<V>> valueMatchers);

    MapMatcher<K, V, T> entry(String name, K key, Matcher<V> valueMatcher);
    MapMatcher<K, V, T> entry(String name, K key, Matcher<V>... valueMatchers);
    MapMatcher<K, V, T> entry(String name, K key, List<Matcher<V>> valueMatchers);



    // TODO: unchecked is an error
}
