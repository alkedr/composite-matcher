package com.github.alkedr.matchers.reporting.old.reporters;

import com.github.alkedr.matchers.reporting.old.ReportingMatcher;

public interface Reporter {
    String report(ReportingMatcher.ExecutedCompositeCheck check);
}
