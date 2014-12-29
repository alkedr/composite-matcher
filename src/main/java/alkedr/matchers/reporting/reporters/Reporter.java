package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;

public interface Reporter {
    String reportCheck(ExecutedCompositeCheck check);
}
