package com.github.alkedr.matchers.reporting;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static com.github.alkedr.matchers.reporting.ReportCheckingUtils.composite;
import static com.github.alkedr.matchers.reporting.ReportCheckingUtils.reportsShouldBeEqual;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.PASSED;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.UNCHECKED;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.describedAs;

/**
 * User: alkedr
 * Date: 04.02.2015
 */
public class ReportingMatcherGetReportTest {
    private static final Object item = new Object();
    private static final Object extractedItem = new Object();

    @Test
    public void reportIsEmpty() {
        reportsShouldBeEqual(new NoOpReportingMatcher().getReport(item), composite(item, UNCHECKED));
    }

    @Test
    public void innerReportingMatcher() {
        reportsShouldBeEqual(new ReportingMatcherWithInnerReportingMatcher().getReport(item), composite(item, PASSED, asList(composite(extractedItem, UNCHECKED))));
    }

    @Test
    public void innerReportingMatcherWithDecorator() {
        reportsShouldBeEqual(new ReportingMatcherWithInnerReportingMatcherWithDecorator().getReport(item), composite(item, PASSED, asList(composite())));
    }


    //TODO: вложенный ReportingMatcher, передача объекта неправильного класса, ReportingMatcher с одной проверкой

    private static class NoOpReportingMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
        }
    }

    private static class ReportingMatcherWithInnerReportingMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.subcheck().runMatcher(new NoOpReportingMatcher());
        }
    }

    private static class ReportingMatcherWithInnerReportingMatcherWithDecorator extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.subcheck().value(extractedItem).runMatcher(describedAs("description", new NoOpReportingMatcher()));
        }
    }
}
