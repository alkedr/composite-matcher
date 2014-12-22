package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.jetbrains.annotations.Nullable;

public abstract class TypeSafeReportingMatcher<T> extends BaseReportingMatcher<T> {
    @Override
    public ExecutedCompositeCheck getReport(@Nullable Object item) {
        try {
            return getReportSafely((T) item);
        } catch (ClassCastException e) {
            return getReportSafely(null);   // TODO: найти способ получше
        }
    }

    public abstract ExecutedCompositeCheck getReportSafely(@Nullable T item);
}
