package com.github.alkedr.matchers.reporting.reporters;

import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;

public interface Reporter {
    String reportCheck(ExecutedCompositeCheck check);
}
