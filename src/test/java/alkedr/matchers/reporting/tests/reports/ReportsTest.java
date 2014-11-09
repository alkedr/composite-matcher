package alkedr.matchers.reporting.tests.reports;

import alkedr.matchers.reporting.ObjectMatcher;
import ch.lambdaj.Lambda;
import org.hamcrest.Matcher;
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

        VeryComplexBean on = Lambda.on(VeryComplexBean.class);
        attachReports("Сложный отчёт", new VeryComplexBean(),
                new ObjectMatcher<VeryComplexBean>()
                        .property(on.getCorrect()).is(correctComplexBean())
                        .property(on.getIncorrect()).is(correctComplexBean())
        );
    }

    private static Matcher<ComplexBean> correctComplexBean() {
        ComplexBean on = Lambda.on(ComplexBean.class);
        return new ObjectMatcher<ComplexBean>()
                .property("booleanPropertyWithFancyName", on.getBooleanProperty()).is(equalTo(true))
                .property(on.getIntProperty()).is(equalTo(1))
                .property("longPropertyWithFancyName", on.getLongProperty()).is(2L)
                .property(on.getStringProperty()).is("3")
        ;
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

    private static class VeryComplexBean {
        private final ComplexBean correct = new ComplexBean();
        private final ComplexBean incorrect = new ComplexBean();
        private final ComplexBean unchecked = new ComplexBean();

        public ComplexBean getCorrect() {
            return correct;
        }

        public ComplexBean getIncorrect() {
            return incorrect;
        }

        public ComplexBean getUnchecked() {
            return unchecked;
        }
    }

    private static class ComplexBean {
        private final boolean booleanProperty = false;
        private final int intProperty = 1;
        private final Long longProperty;
        private final String stringProperty = "3";

        private ComplexBean() {
            this(2L);
        }

        private ComplexBean(Long longProperty) {
            this.longProperty = longProperty;
        }

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
