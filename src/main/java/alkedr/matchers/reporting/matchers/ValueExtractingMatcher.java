package alkedr.matchers.reporting.matchers;

import alkedr.matchers.reporting.TypeSafeReportingMatcher;
import alkedr.matchers.reporting.checks.CheckExecutor;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExtractedValue;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValueExtractingMatcher<T> extends TypeSafeReportingMatcher<T> {
    private final Map<ValuesExtractor<T>, Collection<Matcher<Object>>> extractorToMatchersMap = new LinkedHashMap<>();

    protected void addPlannedCheck(ValuesExtractor<T> extractor, Collection<? extends Matcher<?>> newMatchers) {
        Collection<Matcher<Object>> matchers = extractorToMatchersMap.get(extractor);
        if (matchers == null) {
            matchers = new ArrayList<>();
            extractorToMatchersMap.put(extractor, matchers);
        }
        matchers.addAll((Collection<? extends Matcher<Object>>) newMatchers);
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
        for (Map.Entry<ValuesExtractor<T>, Collection<Matcher<Object>>> extractorToMatchers : extractorToMatchersMap.entrySet()) {
            for (ExtractedValue extractedValue : extractorToMatchers.getKey().extractValues(item)) {
                CheckExecutor<?> executorForExtractedValue = new CheckExecutor<>(extractedValue);
                for (Matcher<Object> matcher : extractorToMatchers.getValue()) {
                    executorForExtractedValue.checkThat(matcher);
                }
                // TODO: run unchecked values extraction recursively here, add results to executorForExtractedValue
                executor.addCompositeCheck(executorForExtractedValue.buildCompositeCheck());
            }
        }
        return executor.buildCompositeCheck();
    }
}
