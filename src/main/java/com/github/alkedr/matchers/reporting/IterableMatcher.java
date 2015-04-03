package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IterableMatcher<E, T extends Iterable<E>> extends ReportingMatcher<T> {
    Checker<E, T> getCheckerForIterable(T iterable);

    interface Checker<E, T> {
        boolean item(T iterable, int index, @Nullable E item, @NotNull CheckListener listener);
    }
}
