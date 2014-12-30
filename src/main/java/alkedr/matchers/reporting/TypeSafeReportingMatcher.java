package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.CheckExecutor;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExtractedValue;
import org.jetbrains.annotations.Nullable;

import static org.hamcrest.Matchers.isA;

public abstract class TypeSafeReportingMatcher<T> extends BaseReportingMatcher<T> {
    private final Class<? super T> tClass;

    protected TypeSafeReportingMatcher(Class<? super T> tClass) {
        this.tClass = tClass;
    }

    public Class<? super T> getActualItemClass() {
        return tClass;
    }

    @Override
    public ExecutedCompositeCheck getReport(@Nullable Object item) {
        if (tClass.isInstance(item)) {
            return getReportSafely((T) item);
        } else {
            CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
            executor.checkThat(isA(tClass));
            executor.addDataFrom(getReportSafely(null));
            return executor.buildCompositeCheck();
        }
    }

    public abstract ExecutedCompositeCheck getReportSafely(@Nullable T item);
}
