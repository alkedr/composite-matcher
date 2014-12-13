package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;

public interface Reporter {
    /**
     * Вызывается CompositeMatcher'ом в конце matches()
     * @param check
     * @return mismatch description
     */
    String reportCheck(ExecutedCompositeCheck check);
}
