package alkedr.matchers.reporting.matchers;

import alkedr.matchers.reporting.TypeSafeReportingMatcher;
import alkedr.matchers.reporting.checks.CheckExecutor;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public class ValueExtractingMatcher<T> extends TypeSafeReportingMatcher<T> {
    private final List<PlannedCheck> plannedChecks = new ArrayList<>();

    protected void addPlannedCheck(final ValueExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        addPlannedCheck(new ValueExtractorsExtractor<T>() {
            @Override
            public List<ValueExtractor<T>> extractValueExtractors(T item) {
                return asList(extractor);
            }
        }, newMatchers);
    }

    protected void addPlannedCheck(ValueExtractorsExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        plannedChecks.add(new PlannedCheck((ValueExtractorsExtractor<Object>)extractor, (Collection<Matcher<Object>>) newMatchers));
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
    }
}
