package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.implementations.ObjectMatcherImpl;

public final class ReportingMatchers {
    private ReportingMatchers() {
    }


    public static <T> ObjectMatcher<T> object(Class<T> tClass) {
        return new ObjectMatcherImpl<>(tClass);
    }
}
