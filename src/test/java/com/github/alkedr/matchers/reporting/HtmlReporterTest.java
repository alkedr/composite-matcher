package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.reporters.HtmlReporter;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static com.github.alkedr.matchers.reporting.ValueExtractingMatcher.*;
import static com.github.alkedr.matchers.reporting.extractors.map.MapValueExtractor.valueOfKey;
import static com.github.alkedr.matchers.reporting.extractors.object.FieldExtractor.field;
import static com.github.alkedr.matchers.reporting.extractors.object.LambdajArgumentExtractor.resultOf;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new HtmlReporter().report(veryComplexBeanMatcher().getReport(new VeryComplexBean())));
        }
    }


    // объекты, списки, мапы
    // извлечённые-непроверенные, извлечённые-проверенные-правильные и извлечённые-проверенные-неправильные, неизвлечённые с разными ошибками
    // только простые проверки, только сложные проверки, оба вида проверок



    private static ReportingMatcher<VeryComplexBean> veryComplexBeanMatcher() {
        map(Integer.class, Object.class)
                .checkThat(valueOfKey(1), is(1))
                .checkThat(valueOfKey(1), is(""))
                ;

        return object(VeryComplexBean.class)
                .checkThat(field(VeryComplexBean.class, "correctField"), correctComplexBean())
                .checkThat(field(VeryComplexBean.class, "incorrectField"), incorrectComplexBean())
                .checkThat(field(VeryComplexBean.class, "uncheckedField"), beanWithGetters(Object.class))
                ;
    }

    private static Matcher<ComplexBean> correctComplexBean() {
        return complexBean("3");
    }

    private static Matcher<ComplexBean> incorrectComplexBean() {
        return complexBean("4");
    }

    private static Matcher<ComplexBean> complexBean(String expectedStringPropertyValue) {
        ComplexBean on = on(ComplexBean.class);
        return beanWithGetters(ComplexBean.class)
                .checkThat(resultOf(on.isBooleanField()), is(false))
                .checkThat(resultOf(on.getIntField()), is(1))
                .checkThat(resultOf(on.getLongField()), is(2L))
                .checkThat(resultOf(on.getStringField()), is(expectedStringPropertyValue))
                ;
    }

    private static class VeryComplexBean {
        private final ComplexBean correctField = new ComplexBean();
        private final ComplexBean incorrectField = new ComplexBean();
        private final ComplexBean uncheckedField = new ComplexBean();
        private final Map<Object, String> map = new HashMap<>();
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
