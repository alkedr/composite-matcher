package com.github.alkedr.matchers.reporting.ifaces;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/* alkedr 07.03.15 */
public interface ObjectMatcherForImplementing<T, U extends ObjectMatcherForImplementing<T, U>> extends ReportingMatcherForImplementing<T, U> {

    <V> ValueCheckAdder<V, U> field(String nameForReportAndExtraction);

    <V> ValueCheckAdder<V, U> field(String nameForReport, String nameForExtraction);

    <V> ValueCheckAdder<V, U> field(Field field);

    <V> ValueCheckAdder<V, U> field(String nameForReport, Field field);


    <V> ValueCheckAdder<V, U> method(String nameForExtraction, Object... arguments);

    <V> ValueCheckAdder<V, U> method(String nameForReport, String nameForExtraction, Object... arguments);

    <V> ValueCheckAdder<V, U> method(ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<V, U> method(String nameForReport, ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<V, U> method(Method method, Object... arguments);

    <V> ValueCheckAdder<V, U> method(String nameForReport, Method method, Object... arguments);


    <V> ValueCheckAdder<V, U> getter(String nameForExtraction);

    <V> ValueCheckAdder<V, U> getter(ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<V, U> getter(Method method);


    <V> ValueCheckAdder<V, U> property(V lambdajPlaceholder);

    <V> ValueCheckAdder<V, U> property(String nameForReport, V lambdajPlaceholder);


    // TODO: value(ValueExtractor<T> valueExtractor) - вызывает valueExtractor, не объединяется ни с чем


    <V> T expect(Matcher<? super V>... matchers);

    <V> T expect(Collection<? extends Matcher<? super V>> matchers);


    interface ValueCheckAdder<V, U> {
        U is(Matcher<?> matcher);
        U is(Matcher<?>... matchers);
        U is(Collection<? extends Matcher<?>> matchers);
        U returns(Matcher<?> matcher);
        U returns(Matcher<?>... matchers);
        U returns(Collection<? extends Matcher<?>> matchers);
        // TODO: throws()
        // TODO: (Matcher matcher) versions
    }

    interface ValueExtractor<V> {
        V extract(@Nullable Object o);
    }
}
