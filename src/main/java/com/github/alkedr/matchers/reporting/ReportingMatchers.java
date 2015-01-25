package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.impl.ObjectMatcherImpl;

public final class ReportingMatchers {
    private ReportingMatchers() {
    }

    public static <T> ObjectMatcher<T> object(Class<? super T> objectClass) {
        return new ObjectMatcherImpl<>(objectClass);
    }

    public static <T> CollectionMatcher<T> collection(Class<? super T> itemClass) {
        return new CollectionMatcherImpl<>(itemClass);
    }

    public static <T> CollectionMatcher<T> list(Class<? super T> itemClass) {
        return new ListMatcherImpl<>(itemClass);
    }

    public static <T> CollectionMatcher<T> map(Class<? super T> keyClass, Class<? super T> valueClass) {
        return new MapMatcherImpl<>(keyClass, valueClass);
    }
}
