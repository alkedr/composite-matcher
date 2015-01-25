package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ObjectMatcherForExtending<T, U extends ObjectMatcherForExtending<T, U>> extends ValueExtractingMatcherForExtending<T, U> {
    public <V> ValueCheckAdder<T, V, U> field(String name) {
        return null;
    }

    public <V> ValueCheckAdder<T, V, U> field(Field field) {
        return null;
    }

    public <V> ValueCheckAdder<T, V, U> field(ValueExtractor<T, V> fieldValueExtractor) {
        return null;
    }


    public <V> ValueCheckAdder<T, V, U> method(String name, Object... arguments) {
        return null;
    }

    public <V> ValueCheckAdder<T, V, U> method(Method method, Object... arguments) {
        return null;
    }

    public <V> ValueCheckAdder<T, V, U> method(ValueExtractor<T, V> methodReturnValueExtractor) {
        return null;
    }


    public <V> ValueCheckAdder<T, V, U> prop(V lambdajPlaceholder) {
        return null;
    }

    public <V> ValueCheckAdder<T, V, U> property(V lambdajPlaceholder) {
        return null;
    }


    public interface ValueCheckAdder<T, V, U> {
        U is(Matcher<V> matcher, Matcher<V>... moreMatchers);
        U is(Matcher<V> matcher, List<Matcher<V>> moreMatchers);
        U is(List<Matcher<V>> matchers);
    }
}
