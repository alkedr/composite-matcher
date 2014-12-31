package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

public interface ReportingMatcher<T> extends Matcher<T> {
    ExecutedCompositeCheck getReport(@Nullable Object item);
}
