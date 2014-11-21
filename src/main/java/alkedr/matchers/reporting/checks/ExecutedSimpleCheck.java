package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.Nullable;

/**
 * Хранит информацию о запуске обычного Matcher'а
 */
public class ExecutedSimpleCheck {
    @Nullable private final String matcherDescription;
    @Nullable private final String mismatchDescription;

    public ExecutedSimpleCheck(boolean matches, Matcher<?> matcher, Object actual) {
        matcherDescription = StringDescription.toString(matcher);
        mismatchDescription = matches ? null : getMismatchDescription(matcher, actual);
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    public boolean isSuccessful() {
        return mismatchDescription == null;
    }

    @Nullable
    public String getMatcherDescription() {
        return matcherDescription;
    }

    @Nullable
    public String getMismatchDescription() {
        return mismatchDescription;
    }
}
