package com.github.alkedr.matchers.reporting.reportingmatcher;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class GetActualItemClassTest {
    @Test
    public void simple() {
        assertSame(new NoOpReportingMatcherForNumber().getActualItemClass(), Number.class);
    }

    @Test
    public void runChecksIsDefinedNotInLeafClass() {
        assertSame(new ChildOfNoOpReportingMatcherForNumber().getActualItemClass(), Number.class);
    }

    @Test
    public void itemClassIsSetManually() {
        assertSame(new ReportingMatcherForNumberWithItemClassInteger().getActualItemClass(), Integer.class);
    }


    private static class NoOpReportingMatcherForNumber extends ReportingMatcher<Number> {
        @Override
        public void runChecks(@Nullable Number item, ExecutedCompositeCheckBuilder checker) {
        }
    }

    private static class ChildOfNoOpReportingMatcherForNumber extends NoOpReportingMatcherForNumber {
    }

    private static class ReportingMatcherForNumberWithItemClassInteger extends ReportingMatcher<Number> {
        private ReportingMatcherForNumberWithItemClassInteger() {
            super(Integer.class);
        }

        @Override
        public void runChecks(@Nullable Number item, ExecutedCompositeCheckBuilder checker) {
        }
    }
}
