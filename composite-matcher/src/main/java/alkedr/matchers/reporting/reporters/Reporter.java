package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;

public interface Reporter<T> {
    /**
     * Вызывается CompositeMatcher'ом в конце matches()
     *
     * @param currentActualValue
     * @param check
     * @return mismatch description
     */
    String reportCheck(T currentActualValue, ExecutedCompositeCheck check);
}
