package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.checks.CheckExecutor;
import com.github.alkedr.matchers.reporting.checks.ExecutedCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.reporters.PlainTextReporter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.hamcrest.Matchers.isA;

/**
 * Override getReportSafely unless you need to handle instances of several different classes or nulls.
 */
public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    @NotNull private final Class<? super T> tClass;
    private Object lastItem = null;
    private ExecutedCompositeCheck lastReport = null;

    protected ReportingMatcher() {
        this(Object.class);
    }

    protected ReportingMatcher(@NotNull Class<? super T> tClass) {
        this.tClass = tClass;
    }


    public Class<? super T> getActualItemClass() {
        return tClass;
    }


    @Override
    public boolean matches(Object item) {
        lastItem = item;
        lastReport = getReport(item);
        CheckExecutor.INNER_CHECK_RESULT.set(lastReport);
        return lastReport.getStatus() != ExecutedCheck.Status.FAILED;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (!Objects.equals(lastItem, item)) matches(item);
        description.appendText(new PlainTextReporter().report(lastReport));
    }


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
