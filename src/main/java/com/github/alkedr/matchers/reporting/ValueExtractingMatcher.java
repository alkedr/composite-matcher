package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.checks.CheckExecutor;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Arrays.asList;

public class ValueExtractingMatcher<T, U extends ValueExtractingMatcher<T, ?>> extends ReportingMatcher<T> {
    private final Collection<Matcher<? super T>> simpleMatchers = new ArrayList<>();
    private final Map<ValueExtractorsExtractor<? super T, ?>, Collection<Matcher<Object>>> plannedChecks = new LinkedHashMap<>();

    public ValueExtractingMatcher(Class<? super T> tClass) {
        super(tClass);
    }


    @SafeVarargs
    public final U checkThat(Matcher<? super T> matcher, Matcher<? super T>... moreMatchers) {
        return checkThat(buildMatchersList(matcher, moreMatchers));
    }

    @SafeVarargs
    public final <V> U checkThat(ValueExtractor<? super T, ? super V> extractor, Matcher<? super V> matcher, Matcher<? super V>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }

    @SafeVarargs
    public final <V> U checkThat(ValueExtractorsExtractor<? super T, ? super V> extractor, Matcher<? super V> matcher, Matcher<? super V>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }


    public U checkThat(Collection<? extends Matcher<? super T>> newMatchers) {
        simpleMatchers.addAll(newMatchers);
        return (U) this;
    }

    private <V> U checkThat(final ValueExtractor<? super T, ? super V> extractor, Collection<Matcher<? super V>> newMatchers) {
        return checkThat(new ValueExtractorsExtractor<T, Object>() {
            @Override
            public List<? extends ValueExtractor<? super T, Object>> extractValueExtractors(T item) {
                return asList((ValueExtractor<? super T, Object>)extractor);
            }
        }, newMatchers);
    }

    private <V> U checkThat(ValueExtractorsExtractor<? super T, ? super V> extractor, Collection<? extends Matcher<? super V>> newMatchers) {
        Collection<Matcher<Object>> matchers = plannedChecks.get(extractor);
        if (matchers == null) {
            matchers = new ArrayList<>();
            plannedChecks.put(extractor, matchers);
        }
        matchers.addAll((Collection<? extends Matcher<Object>>) newMatchers);
        return (U) this;
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
        for (Matcher<? super T> matcher : simpleMatchers) {
            executor.checkThat(matcher);
        }
        for (Map.Entry<ValueExtractor<? super T, ?>, Collection<Matcher<Object>>> extractorToMatchers : getExtractorToMatchersMap(item).entrySet()) {
            CheckExecutor<Object> executorForExtractedValue = new CheckExecutor<>(extractorToMatchers.getKey().extractValue(item));
            for (Matcher<Object> matcher : extractorToMatchers.getValue()) {
                executorForExtractedValue.checkThat(matcher);
            }
            executor.addCompositeCheck(executorForExtractedValue.buildCompositeCheck());
        }
        return executor.buildCompositeCheck();
    }


    private Map<ValueExtractor<? super T, ?>, Collection<Matcher<Object>>> getExtractorToMatchersMap(@Nullable T item) {
        Map<ValueExtractor<? super T, ?>, Collection<Matcher<Object>>> result = new LinkedHashMap<>();
        for (Map.Entry<ValueExtractorsExtractor<? super T, ?>, Collection<Matcher<Object>>> extractorsExtractorToMatchers : plannedChecks.entrySet()) {
            for (ValueExtractor<? super T, ?> extractor : extractorsExtractorToMatchers.getKey().extractValueExtractors(item)) {
                Collection<Matcher<Object>> matchers = result.get(extractor);
                if (matchers == null) {
                    matchers = new ArrayList<>();
                    result.put(extractor, matchers);
                }
                matchers.addAll(extractorsExtractorToMatchers.getValue());
            }
        }
        return result;
    }

    @SafeVarargs
    private static <T> Collection<Matcher<? super T>> buildMatchersList(Matcher<? super T> matcher, Matcher<? super T>... moreMatchers) {
        Collection<Matcher<? super T>> result = new ArrayList<>();
        Collections.addAll(result, matcher);
        Collections.addAll(result, moreMatchers);
        return result;
    }





    public static <T> ValueExtractingMatcher<T, ValueExtractingMatcher<T, ?>> object(Class<T> tClass) {
        return new ValueExtractingMatcher<>(tClass);
    }

    public static <T> ValueExtractingMatcher<T, ValueExtractingMatcher<T, ?>> beanWithGetters(Class<T> tClass) {
        // TODO
        return new ValueExtractingMatcher<>(tClass);
    }

    public static <T, U> ValueExtractingMatcher<Map<T, U>, ValueExtractingMatcher<Map<T, U>, ?>> map(Class<T> keyClass, Class<U> valueClass) {
        return new ValueExtractingMatcher<>(Map.class);
    }

//    public static <T> ObjectMatcher<T> beanWithFields(Class<T> tClass) {
//        // TODO: универсальный матчер для непроверенных полей, который знает про коллекции, мапы и пр.
//        // TODO: он должен будет как-то поддерживвть blacklisting полей и методов на случай  Object getThis() { return this; } ?
//        ObjectMatcher<Object> recursiveFieldsMatcher = object(Object.class);
//        recursiveFieldsMatcher.allFieldsAre(recursiveFieldsMatcher);
//        return object(tClass).allFieldsAre(recursiveFieldsMatcher);
//    }
//
//    // TODO: beanWithPrivateFields
//
//    public static <T> ObjectMatcher<T> beanWithGetters(Class<T> tClass) {
//        ObjectMatcher<Object> recursiveGettersMatcher = object(Object.class);
//        recursiveGettersMatcher.allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
//        return object(tClass).allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
//    }
//
//    // TODO: beanWithPrivateGetters?
//
//
//
//
//    public static <T, U> MapMatcher<T, U> map() {
//        return new MapMatcher<>();
//    }
//
//    public static <T> IterableMatcher<T> iterable() {
//        return new IterableMatcher<>();
//    }
}
