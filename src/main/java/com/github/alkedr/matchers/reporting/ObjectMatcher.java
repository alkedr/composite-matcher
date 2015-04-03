package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public interface ObjectMatcher<T> extends PlanningMatcher<T> {
    <V> ValueCheckAdder<T, V> field(String nameForReportAndExtraction);

    <V> ValueCheckAdder<T, V> field(String nameForReport, String nameForExtraction);

    <V> ValueCheckAdder<T, V> field(Field field);

    <V> ValueCheckAdder<T, V> field(String nameForReport, Field field);


    <V> ValueCheckAdder<T, V> method(String nameForExtraction, Object... arguments);

    <V> ValueCheckAdder<T, V> method(String nameForReport, String nameForExtraction, Object... arguments);

    <V> ValueCheckAdder<T, V> method(ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<T, V> method(String nameForReport, ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<T, V> method(Method method, Object... arguments);

    <V> ValueCheckAdder<T, V> method(String nameForReport, Method method, Object... arguments);


    <V> ValueCheckAdder<T, V> getter(String nameForExtraction);

    <V> ValueCheckAdder<T, V> getter(ValueExtractor<T> valueExtractor);

    <V> ValueCheckAdder<T, V> getter(Method method);


    <V> ValueCheckAdder<T, V> property(V lambdajPlaceholder);

    <V> ValueCheckAdder<T, V> property(String nameForReport, V lambdajPlaceholder);


    <V> T expect(Matcher<? super V>... matchers);

    <V> T expect(Iterable<? extends Matcher<? super V>> matchers);


    interface ValueExtractor<T> {
        @Nullable Object extract(@NotNull T valueToExtractFrom);
    }

    interface ValueCheckAdder<T, V> {
        ObjectMatcher<T> is(Matcher<?> matcher);
        ObjectMatcher<T> is(Matcher<?>... matchers);
        ObjectMatcher<T> is(Collection<? extends Matcher<?>> matchers);
        ObjectMatcher<T> returns(Matcher<? super V> matcher);
        ObjectMatcher<T> returns(Matcher<? super V>... matchers);
        ObjectMatcher<T> returns(Collection<? extends Matcher<? super V>> matchers);
    }
}
