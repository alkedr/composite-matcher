package com.github.alkedr.matchers.reporting.old;

import org.jetbrains.annotations.NotNull;

public class ExhaustiveMapMatcher<Key, Value> extends ExhaustiveMapMatcherForExtending<Key, Value, ExhaustiveMapMatcher<Key, Value>> {
    public ExhaustiveMapMatcher(@NotNull Class<?> tClass) {
        super(tClass);
    }
}
