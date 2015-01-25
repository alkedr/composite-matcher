package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.ReportingMatcher;

public interface Reporter {
    String report(ReportingMatcher.ExecutedCompositeCheck check);
}
