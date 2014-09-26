package alkedr;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class CompositeMatcher <T> extends TypeSafeMatcher<T> {
    private final String descriptionString;
    private final List<Check> checks = new ArrayList<Check>();

    protected CompositeMatcher() {
        this("is correct");
    }

    protected CompositeMatcher(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    @Override
    protected boolean matchesSafely(T item) {
        this.mismatchDescription = new StringDescription();
        this.matches = true;
        check(item);
        return matches;
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText(this.mismatchDescription.toString());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }

    protected abstract void check(T item);

    protected <U> boolean checkThat(String objectDescription, U object, Matcher<? super U> matcher) {
        try {
            assertThat(objectDescription, object, matcher);
            return true;
        } catch (AssertionError e) {
            return false;
        }




//        try {
//            checkThatStepHelper(objectDescription, object, matcher);
//            return true;
//        } catch (AssertionError e) {
//            if (mismatchDescription.toString().isEmpty()) {
//                mismatchDescription.appendText("\n");
//            }
//            mismatchDescription.appendText("          Expected: ");
//            matcher.describeTo(mismatchDescription);
//            mismatchDescription.appendText("\n               but: ").appendText(e.getMessage()).appendText("\n");
//            matches = false;
//            return false;
//        }
    }

    protected <U> boolean checkThat(U object, Matcher<? super U> matcher) {
//        return checkThat(object.toString(), object, matcher);
    }

//    @Step("Проверяем что {0} {2}")
//    private static void checkThatStepHelper(String objectDescription, Object object, Matcher<?> matcher) {
//        if (!matcher.matches(object)) {
//            StringDescription description = new StringDescription();
//            matcher.describeMismatch(object, description);
//            if (!(matcher instanceof CompositeMatcher<?>)) {
//                checkThatStepFailureLog(description.toString());
//            }
//            throw new AssertionError(description.toString());
//        }
//    }
//
//    @Step("{0}")
//    private static void checkThatStepFailureLog(String stepName) {
//    }


    private static class Check {
        private boolean isSuccesful;
        private String actualValueName;
        private String actualValue;
        private String expectedValue;  // для equalTo и is()
        private String matcherDescription;
        private String mismatchDescription;

    }

}