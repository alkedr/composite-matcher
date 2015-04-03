package com.github.alkedr.matchers.reporting.implementations;

import com.github.alkedr.matchers.reporting.MapMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MapMatcherImpl<K, V, T extends Map<K, V>, U extends MapMatcherImpl<K, V, T, U>> extends PlanningMatcherImpl<T, MapMatcherImpl<K, V, T, U>> implements MapMatcher<K, V, T> {
    @Override
    public U entry(K key, Matcher<V> valueMatcher) {
        return entry(String.valueOf(key), key, valueMatcher);
    }

    @Override
    public U entry(K key, Matcher<V>... valueMatchers) {
        return entry(String.valueOf(key), key, valueMatchers);
    }

    @Override
    public U entry(K key, List<Matcher<V>> valueMatchers) {
        return entry(String.valueOf(key), key, valueMatchers);
    }


    @Override
    public U entry(String name, K key, Matcher<V> valueMatcher) {
        return entryImpl(name, key, valueMatcher);
    }

    @Override
    public U entry(String name, K key, Matcher<V>... valueMatchers) {
        return entryImpl(name, key, valueMatchers);
    }

    @Override
    public U entry(String name, K key, List<Matcher<V>> valueMatchers) {
        return entryImpl(name, key, valueMatchers);
    }


    @Override
    public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
        return false;
    }



    private U entryImpl(final String name, final K key, final Object matcherObject) {
        addPlannedCheck(new PlannedCheck() {
            @Override
            public boolean matches(@Nullable Object item, @NotNull CheckListener listener) {
                assert item instanceof Map;
                Map<K, V> map = (Map<K, V>)item;
                return map.containsKey(key) ? normalValue(listener, name, map.get(key), matcherObject) : missingValue(listener, name, matcherObject);
            }
        });
        return (U) this;
    }
}
