package com.github.alkedr.matchers.reporting;

/**
 * User: alkedr
 * Date: 12.01.2015
 */
public class IterableMatcher<T> extends ValueExtractingMatcherImpl<Iterable<T>, IterableMatcher<T>> {
    public IterableMatcher() {
        super(Iterable.class);
    }
}
