package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReportingMatcher<T> extends Matcher<T> {

    boolean matches(@Nullable Object item, @NotNull CheckListener listener);

    ReportingMatcher<T> describedAs(String newDescriptionString);

    /**
     * {@link com.github.alkedr.matchers.reporting.implementations.ReportingMatcherImpl#normalValue}
     * {@link com.github.alkedr.matchers.reporting.implementations.ReportingMatcherImpl#brokenValue}
     * {@link com.github.alkedr.matchers.reporting.implementations.ReportingMatcherImpl#missingValue}
     * {@link com.github.alkedr.matchers.reporting.implementations.ReportingMatcherImpl#unexpectedValue}
     */
    interface CheckListener {
        void onMatcher(@NotNull String matcherDescription, @Nullable String mismatchDescription);
        void onStartValue(@NotNull String name, @Nullable Object value);
        void onStartBrokenValue(@NotNull String name, @NotNull Exception extractionException);
        void onStartMissingValue(@NotNull String name);
        void onStartUnexpectedValue(@NotNull String name, @Nullable Object value);
        void onEndValue();
    }
}
