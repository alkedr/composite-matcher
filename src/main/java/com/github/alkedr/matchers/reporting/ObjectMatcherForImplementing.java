package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface ObjectMatcherForImplementing<T, U extends ObjectMatcherForImplementing<T, U>> extends ValueExtractingMatcherForImplementing<T, U> {
    <V> ValueCheckAdder<T, V, U> field(String name);
    <V> ValueCheckAdder<T, V, U> field(Field field);
    <V> ValueCheckAdder<T, V, U> field(ValueExtractor<T, V> fieldValueExtractor);

    <V> ValueCheckAdder<T, V, U> method(String name, Object... arguments);
    <V> ValueCheckAdder<T, V, U> method(Method method, Object... arguments);
    <V> ValueCheckAdder<T, V, U> method(ValueExtractor<T, V> methodReturnValueExtractor);

    <V> ValueCheckAdder<T, V, U> prop(V lambdajPlaceholder);
    <V> ValueCheckAdder<T, V, U> property(V lambdajPlaceholder);

    interface ValueCheckAdder<T, V, U> {
        U is(Matcher<V> matcher, Matcher<V>... moreMatchers);
        U is(Matcher<V> matcher, List<Matcher<V>> moreMatchers);
        U is(List<Matcher<V>> matchers);
    }
}
