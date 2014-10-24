package alkedr.matchers.reporting.tests.reports;

import alkedr.matchers.reporting.ObjectMatcher;
import ch.lambdaj.Lambda;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Attachment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class ReportsTest {
    @Test
    public void reportsTest() {
        attachReport("Пустой отчёт", new Object(), new ObjectMatcher<>());
        ComplexBean on = Lambda.on(ComplexBean.class);
        attachReport("Сложный отчёт", new ComplexBean(), new ObjectMatcher<>()
                        .property("booleanPropertyWithFancyName", on.getBooleanProperty(), is(true))
                        .property(on.getIntProperty(), is(1))
                        .property("longPropertyWithFancyName", on.getLongProperty(), 2L)
                        .property(on.getStringProperty(), "3")
        );
    }

    @Attachment(value = "{0}", type = "text/html")
    private static <T> String attachReport(@SuppressWarnings("UnusedParameters") String reportName, T actual, ObjectMatcher<? super T> matcher) {
        matcher.matches(actual);
        return matcher.generateHtmlReport();
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
