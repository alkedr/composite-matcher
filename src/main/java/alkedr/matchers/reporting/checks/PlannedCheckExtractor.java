package alkedr.matchers.reporting.checks;

import java.util.List;

public interface PlannedCheckExtractor<T, U> {
    List<PlannedCheck<U>> extractChecks(T actual);
}
