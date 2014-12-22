package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.FAILED;
import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.PASSED;

/**
 * Хранит информацию о запуске обычного Matcher'а
 */
public class ExecutedSimpleCheck implements ExecutedCheck {
    @Nullable private final String matcherDescription;
    @Nullable private final String mismatchDescription;

    public ExecutedSimpleCheck(boolean matches, Matcher<?> matcher, Object actual) {
        matcherDescription = StringDescription.toString(matcher);
        mismatchDescription = matches ? null : getMismatchDescription(matcher, actual);
    }

    public ExecutedSimpleCheck(@Nullable String matcherDescription, @Nullable String mismatchDescription) {
        this.matcherDescription = matcherDescription;
        this.mismatchDescription = mismatchDescription;
    }


    @Override
    @NotNull
    public ExecutedCheckStatus getStatus() {
        return mismatchDescription == null ? PASSED : FAILED;
    }

    @Nullable
    public String getMatcherDescription() {
        return matcherDescription;
    }

    @Nullable
    public String getMismatchDescription() {
        return mismatchDescription;
    }


    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }
}
