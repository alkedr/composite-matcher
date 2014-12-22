package alkedr.matchers.reporting.matchers;

import alkedr.matchers.reporting.TypeSafeReportingMatcher;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValueExtractingMatcher<T> extends TypeSafeReportingMatcher<T> {
    private final Map<ValuesExtractor<T, ?>, Collection<Matcher<?>>> valueExtractorToMatchersMap = new LinkedHashMap<>();

    protected <U> void addPlannedCheck(ValuesExtractor<T, U> extractor, Collection<? extends Matcher<? super U>> newMatchers) {
        Collection<Matcher<?>> matchers = valueExtractorToMatchersMap.get(extractor);
        if (matchers == null) {
            matchers = new ArrayList<>();
            valueExtractorToMatchersMap.put(extractor, matchers);
        }
        matchers.addAll(newMatchers);
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        ExecutedCompositeCheck report = new ExecutedCompositeCheck(item);
        for (Map.Entry<ValuesExtractor<T, ?>, Collection<Matcher<?>>> valueExtractorToMatchers : valueExtractorToMatchersMap.entrySet()) {
            for (Map.Entry<String, ?> valueNameToValue : valueExtractorToMatchers.getKey().extractValues(item).entrySet()) {
                report.reportValue(valueNameToValue.getKey(), valueNameToValue.getValue());
                for (Matcher<?> matcher : valueExtractorToMatchers.getValue()) {
                    report.checkThat(valueNameToValue.getKey(), valueNameToValue.getValue(), matcher);
                }
            }
        }
        ExecutedCompositeCheck.INNER_CHECK_RESULT.set(report);  //FIXME
        return report;
    }
}
