package com.github.alkedr.matchers.reporting.old;

import org.jetbrains.annotations.NotNull;

public class ObjectMatcher<T> extends ObjectMatcherForExtending<T, ObjectMatcher<T>> {
    public ObjectMatcher(@NotNull Class<T> tClass) {
        super(tClass);
    }
}
