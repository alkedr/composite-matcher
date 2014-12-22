package alkedr.matchers.reporting;

import alkedr.matchers.reporting.reporters.HtmlReporter;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

import static alkedr.matchers.reporting.matchers.object.ObjectMatcher.beanWithGetters;
import static alkedr.matchers.reporting.matchers.object.ObjectMatcher.object;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.notNullValue;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        ReportingMatcher<VeryComplexBean> matcher = veryComplexBeanMatcher();
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new HtmlReporter().reportCheck(matcher.getReport(new VeryComplexBean())));
        }
    }

    private static ReportingMatcher<VeryComplexBean> veryComplexBeanMatcher() {
        return object(VeryComplexBean.class)
                .<ComplexBean>field("correctField").is(correctComplexBean())
                .<ComplexBean>field("incorrectField").is(incorrectComplexBean())
                .<ComplexBean>field("uncheckedField").is(beanWithGetters(Object.class))
                ;
    }

    private static Matcher<? super ComplexBean> correctComplexBean() {
        return complexBean("3");
    }

    private static Matcher<? super ComplexBean> incorrectComplexBean() {
        return complexBean("4");
    }

    private static Matcher<? super ComplexBean> complexBean(String expectedStringPropertyValue) {
        ComplexBean on = on(ComplexBean.class);
        return beanWithGetters(ComplexBean.class)
                .property(on.isBooleanField()).isEqualTo(false)
                .property(on.getIntField()).isEqualTo(1)
                .property(on.getLongField()).isEqualTo(2L)
                .property(on.getStringField()).isEqualTo(expectedStringPropertyValue)
                ;
    }

    private static class VeryComplexBean {
        private final ComplexBean correctField = new ComplexBean();
        private final ComplexBean incorrectField = new ComplexBean();
        private final ComplexBean uncheckedField = new ComplexBean();
    }

    private static class ComplexBean {
        private final boolean booleanField = false;
        private final int intField = 1;
        private final Long longField = 2L;
        private final String stringField = "3";
        private final String uncheckedStringField = "3";

        public boolean isBooleanField() {
            return booleanField;
        }

        public int getIntField() {
            return intField;
        }

        public Long getLongField() {
            return longField;
        }

        public String getStringField() {
            return stringField;
        }

        public String getUncheckedStringField() {
            return uncheckedStringField;
        }
    }
}
