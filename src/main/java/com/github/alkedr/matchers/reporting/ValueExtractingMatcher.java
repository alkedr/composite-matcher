package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.NotNull;

public class ValueExtractingMatcher<T> extends ValueExtractingMatcherForExtending<T, ValueExtractingMatcher<T>> {
    public ValueExtractingMatcher() {
    }

    public ValueExtractingMatcher(@NotNull Class<T> tClass) {
        super(tClass);
    }
}
