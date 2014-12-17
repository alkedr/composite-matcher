package reports;

import alkedr.matchers.reporting.CompositeMatcher;
import alkedr.matchers.reporting.reporters.HtmlReporter;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class HtmlReporterTest {
    @Test
    public void fastReportsTest() throws IOException {
        CompositeMatcher<VeryComplexBean> matcher = veryComplexBeanMatcher();
        matcher.setReporter(new HtmlReporter());
        VeryComplexBean bean = new VeryComplexBean();
        matcher.matches(bean);
        StringDescription mismatchDescription = new StringDescription();
        matcher.describeMismatch(bean, mismatchDescription);
        try (FileWriter fileWriter = new FileWriter("example-report.html")) {
            fileWriter.write(mismatchDescription.toString());
        }
    }

    private static CompositeMatcher<VeryComplexBean> veryComplexBeanMatcher() {
        return new CompositeMatcher<VeryComplexBean>() {
            @Override
            protected void check(@Nullable VeryComplexBean actualValue) {
                checkThat(notNullValue());
                checkThat("correctField", actualValue.correctField, correctComplexBean());
                checkThat("incorrectField", actualValue.incorrectField, incorrectComplexBean());
                checkThat("uncheckedField", actualValue.uncheckedField);
            }
        };
    }

    private static Matcher<? super ComplexBean> correctComplexBean() {
        return complexBean("3");
    }

    private static Matcher<? super ComplexBean> incorrectComplexBean() {
        return complexBean("4");
    }

    private static Matcher<? super ComplexBean> complexBean(final String expectedStringPropertyValue) {
        return new CompositeMatcher<ComplexBean>() {
            @Override
            protected void check(@Nullable ComplexBean actualValue) {
                checkThat(notNullValue());
                checkThat("booleanField", actualValue.booleanField, equalTo(false));
                checkThat("intField", actualValue.intField, equalTo(1));
                checkThat("longField", actualValue.longField, equalTo(2L));
                checkThat("stringField", actualValue.stringField, equalTo(expectedStringPropertyValue));
            }
        };
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
    }
}
