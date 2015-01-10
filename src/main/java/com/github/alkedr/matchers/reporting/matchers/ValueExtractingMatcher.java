package com.github.alkedr.matchers.reporting.matchers;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public class ValueExtractingMatcher<T, U extends ValueExtractingMatcher<T, ?>> extends ReportingMatcher<T> {
    private final Collection<Matcher<?>> simpleMatchers = new ArrayList<>();
    private final List<PlannedCheck<T>> plannedChecks = new ArrayList<>();

    public ValueExtractingMatcher(Class<? super T> tClass) {
        super(tClass);
    }


    public U checkThat(Matcher<? super T> matcher, Matcher<? super T>... moreMatchers) {
        return checkThat(buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(Collection<? extends Matcher<?>> newMatchers) {
        simpleMatchers.addAll(newMatchers);
        return (U) this;
    }


    public U checkThat(ValueExtractor<T> extractor, Matcher<Object> matcher, Matcher<Object>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(final ValueExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        return checkThat(new ValueExtractorsExtractor<T>() {
            @Override
            public List<ValueExtractor<T>> extractValueExtractors(T item) {
                return asList(extractor);
            }
        }, newMatchers);
    }


    public U checkThat(ValueExtractorsExtractor<T> extractor, Matcher<Object> matcher, Matcher<?>... moreMatchers) {
        return checkThat(extractor, buildMatchersList(matcher, moreMatchers));
    }

    public U checkThat(ValueExtractorsExtractor<T> extractor, Collection<Matcher<?>> newMatchers) {
        plannedChecks.add(new PlannedCheck<T>(extractor, newMatchers));
        return (U) this;
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        Collection<PlannedCheck<T>> sortedAndAndMergedPlannedChecks = getSortedAndAndMergedPlannedChecks(plannedChecks);
        Collection<ExecutableCheck<T>> executableChecks = getExecutableChecks(sortedAndAndMergedPlannedChecks, item);
        Collection<ExecutableCheck<T>> sortedAndAndMergedExecutableChecks = getSortedAndAndMergedExecutableChecks(executableChecks);
        return executeExecutableChecks(sortedAndAndMergedExecutableChecks);
    }


    private static <T> Collection<PlannedCheck<T>> getSortedAndAndMergedPlannedChecks(List<PlannedCheck<T>> plannedChecks) {

        Comparator<PlannedCheck<T>> plannedCheckComparator = new Comparator<PlannedCheck<T>>() {
            @Override
            public int compare(PlannedCheck<T> o1, PlannedCheck<T> o2) {
                return o1.extractorsExtractor.compareTo(o2.extractorsExtractor);
            }
        };

        Collections.sort(plannedChecks, plannedCheckComparator);

        // TODO: пообъединять plannedChecks
        return null;
    }

    private static <T> Collection<ExecutableCheck<T>> getExecutableChecks(Iterable<PlannedCheck<T>> plannedChecks, @Nullable T item) {
        Collection<ExecutableCheck<T>> result = new ArrayList<>();
        for (PlannedCheck<T> plannedCheck : plannedChecks) {
            for (ValueExtractor<T> valueExtractor : plannedCheck.extractorsExtractor.extractValueExtractors(item)) {
                result.add(new ExecutableCheck<>(valueExtractor, plannedCheck.matchers));
            }
        }
        return result;
    }

    private static <T> Collection<ExecutableCheck<T>> getSortedAndAndMergedExecutableChecks(Collection<ExecutableCheck<T>> executableChecks) {
        return null;
    }

    private static <T> ExecutedCompositeCheck executeExecutableChecks(Collection<ExecutableCheck<T>> executableChecks) {
        return null;
    }


    private static <T> Collection<Matcher<?>> buildMatchersList(Matcher<? super T> matcher, Matcher<?>... moreMatchers) {
        Collection<Matcher<?>> result = new ArrayList<>();
        Collections.addAll(result, matcher);
        Collections.addAll(result, moreMatchers);
        return result;
    }


    private static class PlannedCheck<T> {
        private final ValueExtractorsExtractor<T> extractorsExtractor;
        private final Collection<Matcher<?>> matchers;

        private PlannedCheck(ValueExtractorsExtractor<T> extractorsExtractor, Collection<Matcher<?>> matchers) {
            this.extractorsExtractor = extractorsExtractor;
            this.matchers = matchers;
        }
    }

    private static class ExecutableCheck<T> {
        private final ValueExtractor<T> valueExtractor;
        private final Collection<Matcher<?>> matchers;

        private ExecutableCheck(ValueExtractor<T> valueExtractor, Collection<Matcher<?>> matchers) {
            this.valueExtractor = valueExtractor;
            this.matchers = matchers;
        }
    }


/*

    protected U addPlannedCheck(final ValueExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        return addPlannedCheck(new ValueExtractorsExtractor<T>() {
            @Override
            public List<ValueExtractor<T>> extractValueExtractors(T item) {
                return asList(extractor);
            }
        }, newMatchers);
    }

    protected U addPlannedCheck(ValueExtractorsExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        plannedChecks.add(new PlannedCheck((ValueExtractorsExtractor<Object>)extractor, (Collection<Matcher<Object>>) newMatchers));
        return (U) this;
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
        for (Map.Entry<ValueExtractor<Object>, Collection<Matcher<Object>>> extractorToMatchers : getExtractorToMatchersMap(item).entrySet()) {
            CheckExecutor<?> executorForExtractedValue = new CheckExecutor<>(extractorToMatchers.getKey().extractValue(item));
            for (Matcher<Object> matcher : extractorToMatchers.getValue()) {
                executorForExtractedValue.checkThat(matcher);
            }
            executor.addCompositeCheck(executorForExtractedValue.buildCompositeCheck());
        }
        return executor.buildCompositeCheck();
    }


    private Map<ValueExtractor<Object>, Collection<Matcher<Object>>> getExtractorToMatchersMap(@Nullable T item) {
        Map<ValueExtractor<Object>, Collection<Matcher<Object>>> result = new HashMap<>();
        for (PlannedCheck plannedCheck : plannedChecks) {
            for (ValueExtractor<Object> extractor : plannedCheck.extractorsExtractor.extractValueExtractors(item)) {
                Collection<Matcher<Object>> matchers = result.get(extractor);
                if (matchers == null) {
                    matchers = new ArrayList<>();
                    result.put(extractor, matchers);
                }
                matchers.addAll(plannedCheck.matchers);
            }
        }
        return result;
    }



    private static class PlannedCheck {
        private final ValueExtractorsExtractor<Object> extractorsExtractor;
        private final Collection<Matcher<Object>> matchers;

        private PlannedCheck(ValueExtractorsExtractor<Object> extractorsExtractor, Collection<Matcher<Object>> matchers) {
            this.extractorsExtractor = extractorsExtractor;
            this.matchers = matchers;
        }
    }*/
}
