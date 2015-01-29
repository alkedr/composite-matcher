package com.github.alkedr.matchers.reporting;

public class ListMatcherForExtending<T, U extends ListMatcherForExtending<T, U>> extends CollectionMatcherForExtending<T, U> {
    public ListMatcherForExtending(Type type) {
        super(type);
    }
}
