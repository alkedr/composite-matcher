package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.impl.ClassifyingMatcher;
import com.github.alkedr.matchers.reporting.reporters.HtmlReporter;
import com.github.alkedr.matchers.reporting.reporters.HtmlWithJsonReporter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new HtmlReporter().report(veryComplexBeanMatcher().getReport(new VeryComplexBean())));
        }
    }

    @Test
    public void memoryTest() throws IOException {
        System.out.println("start");
        Collection<String> strings = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            strings.add(RandomStringUtils.randomAlphanumeric(10));
        }
        System.out.println("strings are generated");
        ReportingMatcher.ExecutedCompositeCheck report = new ClassifyingMatcher().items(any(String.class), 1000000).getReport(strings);
        System.out.println("report is built");
        String s = new HtmlWithJsonReporter().report(report);
        System.out.println("html size " + s.length());
        System.gc();

        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new HtmlWithJsonReporter().report(report));
        }
    }


    // объекты, списки, мапы
    // извлечённые-непроверенные, извлечённые-проверенные-правильные и извлечённые-проверенные-неправильные, неизвлечённые с разными ошибками
    // только простые проверки, только сложные проверки, оба вида проверок



    private static ReportingMatcher<VeryComplexBean> veryComplexBeanMatcher() {
        return new ObjectMatcher<VeryComplexBean>()
                .field("correctField").is(correctComplexBean())
                .field("incorrectField").is(incorrectComplexBean())
                .field("uncheckedField").is(correctComplexBean())
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
        return new ObjectMatcher<ComplexBean>()
                .property(on.isBooleanField()).is(equalTo(false))
                .property(on.getIntField()).is(equalTo(1))
                .property(on.getLongField()).is(equalTo(2L))
                .property(on.getStringField()).is(equalTo(expectedStringPropertyValue))
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
