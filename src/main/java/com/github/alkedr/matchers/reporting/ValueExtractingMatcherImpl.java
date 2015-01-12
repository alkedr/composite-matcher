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

public class ValueExtractingMatcherImpl<T, U extends ValueExtractingMatcherImpl<T, ?>> extends ReportingMatcher<T> {
    private final Collection<Matcher<? super T>> simpleMatchers = new ArrayList<>();
    private final Map<ValueExtractorsExtractor<? super T, ?>, Collection<Matcher<Object>>> plannedChecks = new LinkedHashMap<>();

    public ValueExtractingMatcherImpl(Class<? super T> tClass) {
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
}
