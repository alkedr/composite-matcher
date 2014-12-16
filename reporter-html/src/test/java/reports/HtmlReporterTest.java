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
        try (FileWriter fileWriter = new FileWriter("/home/alkedr/programming/composite-matcher/example-report.html")) {
            fileWriter.write(mismatchDescription.toString());
        }
    }

    private static CompositeMatcher<VeryComplexBean> veryComplexBeanMatcher() {
        return new CompositeMatcher<VeryComplexBean>() {
            @Override
            protected void check(@Nullable VeryComplexBean actualValue) {
                checkThat(notNullValue());
                checkThat("correct", actualValue.correct, correctComplexBean());
                checkThat("incorrect", actualValue.incorrect, incorrectComplexBean());
                checkThat("unchecked", actualValue.unchecked);
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
                checkThat("booleanProperty", actualValue.booleanProperty, equalTo(false));
                checkThat("intProperty", actualValue.intProperty, equalTo(1));
                checkThat("longProperty", actualValue.longProperty, equalTo(2L));
                checkThat("stringProperty", actualValue.stringProperty, equalTo(expectedStringPropertyValue));
            }
        };
    }

    private static class VeryComplexBean {
        private final ComplexBean correct = new ComplexBean();
        private final ComplexBean incorrect = new ComplexBean();
        private final ComplexBean unchecked = new ComplexBean();
    }

    private static class ComplexBean {
        private final boolean booleanProperty = false;
        private final int intProperty = 1;
        private final Long longProperty = 2L;
        private final String stringProperty = "3";
    }
}
