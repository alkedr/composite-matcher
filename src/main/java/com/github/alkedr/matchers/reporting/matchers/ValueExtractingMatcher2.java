package com.github.alkedr.matchers.reporting.matchers;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import com.github.alkedr.matchers.reporting.checks.CheckExecutor;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Arrays.asList;

public class ValueExtractingMatcher2<T, U extends ValueExtractingMatcher2<T, ?>> extends ReportingMatcher<T> {
    private final Collection<Matcher<? super T>> simpleMatchers = new ArrayList<>();
    private final Collection<PlannedCheck<T>> plannedChecks = new ArrayList<>();

    public ValueExtractingMatcher2(Class<? super T> tClass) {
        super(tClass);
    }


    public U checkThat(Matcher<? super T> matcher, Matcher<? super T>... moreMatchers) {
        return checkThat(buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(Collection<? extends Matcher<? super T>> newMatchers) {
        simpleMatchers.addAll(newMatchers);
        return (U) this;
    }


    public U checkThat(ValueExtractor<T> extractor, Matcher<Object> matcher, Matcher<Object>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(final ValueExtractor<T> extractor, Collection<Matcher<? super Object>> newMatchers) {
        return checkThat(new ValueExtractorsExtractor<T>() {
            @Override
            public List<ValueExtractor<T>> extractValueExtractors(T item) {
                return asList(extractor);
            }
        }, newMatchers);
    }


    public U checkThat(ValueExtractorsExtractor<T> extractor, Matcher<? super Object> matcher, Matcher<? super Object>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(ValueExtractorsExtractor<T> extractor, Collection<Matcher<? super Object>> newMatchers) {
        plannedChecks.add(new PlannedCheck<>(extractor, newMatchers));
        return (U) this;
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
        for (Map.Entry<ValueExtractor<T>, Collection<Matcher<? super Object>>> extractorToMatchers : getExtractorToMatchersMap(item).entrySet()) {
            CheckExecutor<?> executorForExtractedValue = new CheckExecutor<>(extractorToMatchers.getKey().extractValue(item));
            for (Matcher<? super Object> matcher : extractorToMatchers.getValue()) {
                executorForExtractedValue.checkThat(matcher);
            }
            executor.addCompositeCheck(executorForExtractedValue.buildCompositeCheck());
        }
        return executor.buildCompositeCheck();
    }


    private Map<ValueExtractor<T>, Collection<Matcher<? super Object>>> getExtractorToMatchersMap(@Nullable T item) {
        Map<ValueExtractor<T>, Collection<Matcher<? super Object>>> result = new HashMap<>();
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            for (ValueExtractor<T> extractor : plannedCheck.extractorsExtractor.extractValueExtractors(item)) {
                Collection<Matcher<? super Object>> matchers = result.get(extractor);
                if (matchers == null) {
                    matchers = new ArrayList<>();
                    result.put(extractor, matchers);
                }
                matchers.addAll(plannedCheck.matchers);
            }
        }
        return result;
    }

    private static <T> Collection<Matcher<? super T>> buildMatchersList(Matcher<? super T> matcher, Matcher<? super T>... moreMatchers) {
        Collection<Matcher<? super T>> result = new ArrayList<>();
        Collections.addAll(result, matcher);
        Collections.addAll(result, moreMatchers);
        return result;
    }

    private static class PlannedCheck<T> {
        private final ValueExtractorsExtractor<T> extractorsExtractor;
        private final Collection<Matcher<? super Object>> matchers;

        private PlannedCheck(ValueExtractorsExtractor<T> extractorsExtractor, Collection<Matcher<? super Object>> matchers) {
            this.extractorsExtractor = extractorsExtractor;
            this.matchers = matchers;
        }
    }
}
