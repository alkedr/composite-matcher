package com.github.alkedr.matchers.reporting;

public interface MatcherWithUncheckedValues {
    MatcherWithUncheckedValues allValuesShouldBeChecked(boolean value);
    MatcherWithUncheckedValues deepUncheckedValuesExtractor(DeepUncheckedValuesExtractor extractor);

    interface DeepUncheckedValuesExtractor {
        void extract(Object valueToExtractFrom, ReportingMatcher.CheckListener listener);
    }
}
