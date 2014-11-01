package alkedr.matchers.reporting.tests.reports;

import alkedr.matchers.reporting.ObjectMatcher;
import ch.lambdaj.Lambda;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Attachment;

import static alkedr.matchers.reporting.reporters.HtmlReporter.generateHtmlReport;
import static alkedr.matchers.reporting.reporters.PlainTextReporter.generatePlainTextReport;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class ReportsTest {
    @Test
    public void reportsTest() {
        attachReports("Пустой отчёт", new Object(), new ObjectMatcher<>());

        ComplexBean on = Lambda.on(ComplexBean.class);
        attachReports("Сложный отчёт", new ComplexBean(), new ObjectMatcher<>()
                        .property("booleanPropertyWithFancyName", on.getBooleanProperty()).is(equalTo(true))
                        .property(on.getIntProperty()).is(equalTo(1))
                        .property("longPropertyWithFancyName", on.getLongProperty()).is(2L)
                        .property(on.getStringProperty()).is("3")
//                        .<Boolean>property("booleanPropertyWithFancyName", on.getBooleanProperty(), is(true))
//                        .<Integer>property(on.getIntProperty(), is(1))
//                        .<Long>property("longPropertyWithFancyName", on.getLongProperty(), 2L)
//                        .<String>property(on.getStringProperty(), "3")
        );
    }

    private static <T> void attachReports(String reportName, T actual, ObjectMatcher<? super T> matcher) {
        attachHtmlReport(reportName, actual, matcher);
        attachPlainTextReport(reportName, actual, matcher);
    }

    @Attachment(value = "{0} (html)", type = "text/html")
    private static <T> String attachHtmlReport(@SuppressWarnings("UnusedParameters") String reportName, T actual, ObjectMatcher<? super T> matcher) {
        matcher.matches(actual);
        return generateHtmlReport(matcher.getCheckResult());
    }

    @Attachment(value = "{0} (plain text)", type = "text/plain")
    private static <T> String attachPlainTextReport(@SuppressWarnings("UnusedParameters") String reportName, T actual, ObjectMatcher<? super T> matcher) {
        matcher.matches(actual);
        return generatePlainTextReport(matcher.getCheckResult());
    }

    private static class ComplexBean {
        private final boolean booleanProperty = false;
        private final int intProperty = 1;
        private final Long longProperty = 2L;
        private final String stringProperty = "3";

        public boolean getBooleanProperty() {
            return booleanProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public Long getLongProperty() {
            return longProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }
    }
}
