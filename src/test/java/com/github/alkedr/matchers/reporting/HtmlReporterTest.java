package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.reporters.PlainTextReporter;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static com.github.alkedr.matchers.reporting.ReportingMatchers.beanWithGetters;
import static com.github.alkedr.matchers.reporting.ReportingMatchers.object;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new PlainTextReporter().report(veryComplexBeanMatcher().getReport(new VeryComplexBean())));
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
        private final Map<Integer, String> map = new HashMap<>();
        private final List<Double> list = new ArrayList<>();

        private VeryComplexBean() {
            map.put(1, "123");
            map.put(22, "4321");
            list.add(2.34);
            list.add(5.34);
        }
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
