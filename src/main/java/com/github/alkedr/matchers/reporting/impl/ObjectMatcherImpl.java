package com.github.alkedr.matchers.reporting.impl;

/**
 * User: alkedr
 * Date: 12.01.2015
 */
public class ObjectMatcherImpl<T> extends ValueExtractingMatcherImpl<T, ObjectMatcherImpl<T>> {
    public ObjectMatcherImpl(Class<? super T> tClass) {
        super(tClass);
    }
}
