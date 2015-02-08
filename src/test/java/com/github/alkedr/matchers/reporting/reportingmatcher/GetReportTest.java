package com.github.alkedr.matchers.reporting.reportingmatcher;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static com.github.alkedr.matchers.reporting.ReportMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

/**
 * User: alkedr
 * Date: 04.02.2015
 */
public class GetReportTest {
    private static final Object item = new Object();
    private static final Object extractedItem = new Object();

    @Test
    public void reportIsEmpty() {
        assertThat(new NoOpReportingMatcher().getReport(item), emptyCompositeCheck(null, item));
    }

    @Test
    public void innerSimpleMatcher() {
        assertThat(new ReportingMatcherWithInnerSimpleMatcher().getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(simpleCheck("ANYTHING")),
                        compositeChecks(empty())
                )
        );
    }

    @Test
    public void innerNoOpReportingMatcher() {
        assertThat(new ReportingMatcherWithInnerNoOpReportingMatcher().getReport(item),
                uncheckedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(emptyCompositeCheck(null, extractedItem))
                )
        );
    }

    @Test
    public void innerReportingMatcher() {
        assertThat(new ReportingMatcherWithInnerReportingMatcherWithInnerSimpleMatcher().getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck(null, extractedItem,
                                    simpleChecks(simpleCheck("ANYTHING")),
                                    compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void innerReportingMatcherWithDecorator() {
        assertThat(new ReportingMatcherWithInnerNoOpReportingMatcherWithDecorator().getReport(item),
                uncheckedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(emptyCompositeCheck(null, extractedItem))
                )
        );
    }


    // TODO: передача объекта неправильного класса
    // TODO: ReportingMatcher со вложенным обычным матчером, который вызывает ReportingMatcher
    // TODO: отдельный тест на очистку глобальных переменных
    // TODO: отдельный тест на очистку глобальных переменных с исключениями?

    private static class NoOpReportingMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
        }
    }

    private static class ReportingMatcherWithInnerSimpleMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.runMatcher(anything());
        }
    }

    private static class ReportingMatcherWithInnerNoOpReportingMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.subcheck().value(extractedItem).runMatcher(new NoOpReportingMatcher());
        }
    }

    private static class ReportingMatcherWithInnerReportingMatcherWithInnerSimpleMatcher extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.subcheck().value(extractedItem).runMatcher(new ReportingMatcherWithInnerSimpleMatcher());
        }
    }

    private static class ReportingMatcherWithInnerNoOpReportingMatcherWithDecorator extends ReportingMatcher<Object> {
        @Override
        public void runChecks(@Nullable Object item, ExecutedCompositeCheckBuilder checker) {
            checker.subcheck().value(extractedItem).runMatcher(describedAs("description", new NoOpReportingMatcher()));
        }
    }
}
