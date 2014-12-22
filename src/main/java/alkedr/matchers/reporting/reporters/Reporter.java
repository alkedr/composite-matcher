package alkedr.matchers.reporting.reporters;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;

public interface Reporter {
    String reportCheck(ExecutedCompositeCheck check);

    // TODO: document why these are needed:
    boolean equals(Object obj);
    int hashCode();
    String toString();
}
