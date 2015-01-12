package com.github.alkedr.matchers.reporting;

/**
 * User: alkedr
 * Date: 12.01.2015
 */
public class ObjectMatcher<T> extends ValueExtractingMatcherImpl<T, ObjectMatcher<T>> {
    public ObjectMatcher(Class<? super T> tClass) {
        super(tClass);
    }
}
