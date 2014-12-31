package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.reporters.HtmlReporter;
import com.github.alkedr.matchers.reporting.reporters.ObjectVisitor;
import com.github.alkedr.matchers.reporting.reporters.ValuesEnumerator;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static com.github.alkedr.matchers.reporting.ReportingMatchers.beanWithGetters;
import static com.github.alkedr.matchers.reporting.ReportingMatchers.object;
import static java.lang.reflect.Modifier.isStatic;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        ReportingMatcher<VeryComplexBean> matcher = veryComplexBeanMatcher();
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(new HtmlReporter().reportCheck(matcher.getReport(new VeryComplexBean())));
        }
    }

    @Test
    public void fastObjectVisitorTest() throws IOException {
        new ObjectVisitor() {
            private int indent = -1;
            @Override protected void onObjectBegin() { indent++; System.out.println(); }
            @Override protected void onObjectEnd()   { indent--; }
            @Override protected void onMapBegin()    { indent++; System.out.println(); }
            @Override protected void onMapEnd()      { indent--; }
            @Override protected void onArrayBegin()  { indent++; System.out.println(); }
            @Override protected void onArrayEnd()    { indent--; }
            @Override
            protected void onKey(String key) {
                for (int i = 0; i < indent; i++) System.out.print("  ");
                System.out.print(key + ": ");
            }
            @Override
            protected void onPrimitiveValue(Object value) {
                System.out.println(value);
            }
        }
                .objectValuesEnumerator(new ValuesEnumerator<Object>() {
                    @Override
                    public void enumerateValues(Object o, Functor functor) {
                        for (Field field : o.getClass().getDeclaredFields()) {
                            if (!isStatic(field.getModifiers())) {
                                field.setAccessible(true);
                                try {
                                    functor.call(field.getName(), field.get(o));
                                } catch (IllegalAccessException ignored) {   // TODO: report extraction errors
                                }
                            }
                        }
                    }
                })
                .mapValuesEnumerator(new ValuesEnumerator<Map<Object, Object>>() {
                    @Override
                    public void enumerateValues(Map<Object, Object> map, Functor functor) {
                        for (Map.Entry<Object, Object> entry : map.entrySet()) {
                            functor.call(String.valueOf(entry.getKey()), entry.getValue());
                        }
                    }
                })
                .accept(new VeryComplexBean());
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
