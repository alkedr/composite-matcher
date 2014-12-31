package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.checks.CheckExecutor;
import com.github.alkedr.matchers.reporting.checks.ExecutedCheck;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import com.github.alkedr.matchers.reporting.reporters.PlainTextReporter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;

public abstract class BaseReportingMatcher<T> extends BaseMatcher<T> implements ReportingMatcher<T> {
    private Object lastItem = null;
    private ExecutedCompositeCheck lastReport = null;

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
        if (!Objects.equals(lastItem, item)) {
            matches(item);
        }
        description.appendText(new PlainTextReporter<T>().reportCheck(lastReport));
    }
}
