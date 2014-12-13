package reports;

import alkedr.matchers.reporting.ObjectMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.FileWriter;
import java.io.IOException;

import static alkedr.matchers.reporting.reporters.HtmlReporter.generateHtmlReport;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.core.IsEqual.equalTo;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class ReportsTest {
    @Test
    public void reportsTest() {
        attachReports("Пустой отчёт", new Object(), new ObjectMatcher<>(Object.class));

        VeryComplexBean on = on(VeryComplexBean.class);
        attachReports("Сложный отчёт", new VeryComplexBean(),
                new ObjectMatcher<>(VeryComplexBean.class)
                        .property(on.getCorrect()).is(correctComplexBean())
                        .property(on.getIncorrect()).is(correctComplexBean())
        );
    }

    @Test
    public void fastReportsTest() throws IOException {
        VeryComplexBean on = on(VeryComplexBean.class);
        ObjectMatcher<VeryComplexBean> matcher = new ObjectMatcher<>(VeryComplexBean.class)
                .property(on.getCorrect()).is(correctComplexBean())
                .property(on.getIncorrect()).is(correctComplexBean());
        matcher.matches(new VeryComplexBean());
        try (FileWriter fileWriter = new FileWriter("/home/alkedr/programming/composite-matcher/example-report.html")) {
            fileWriter.write(generateHtmlReport(matcher.getLastCheckResult()));
        }
    }

    private static Matcher<ComplexBean> correctComplexBean() {
        ComplexBean on = on(ComplexBean.class);
        return new ObjectMatcher<>(ComplexBean.class)
                .property("booleanPropertyWithFancyName", on.getBooleanProperty()).is(equalTo(true))
                .property(on.getIntProperty()).is(equalTo(1))
                .property("longPropertyWithFancyName", on.getLongProperty()).is(2L)
                .property(on.getStringProperty()).is("3")
        ;
    }

    private static <T> void attachReports(String reportName, T actual, ObjectMatcher<? super T> matcher) {
        attachHtmlReport(reportName, actual, matcher);
//        attachPlainTextReport(reportName, actual, matcher);
    }

    @Attachment(value = "{0} (html)", type = "text/html")
    private static <T> String attachHtmlReport(@SuppressWarnings("UnusedParameters") String reportName, T actual, ObjectMatcher<? super T> matcher) {
        matcher.matches(actual);
        return generateHtmlReport(matcher.getLastCheckResult());
    }

//    @Attachment(value = "{0} (plain text)", type = "text/plain")
//    private static <T> String attachPlainTextReport(@SuppressWarnings("UnusedParameters") String reportName, T actual, ObjectMatcher<? super T> matcher) {
//        matcher.matches(actual);
//        return generatePlainTextReport(matcher.getLastCheckResult());
//    }

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
