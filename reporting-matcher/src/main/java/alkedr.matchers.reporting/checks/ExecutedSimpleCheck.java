package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static alkedr.matchers.reporting.checks.CheckStatus.FAILED;
import static alkedr.matchers.reporting.checks.CheckStatus.PASSED;

/**
 * Хранит информацию о запуске обычного Matcher'а
 */
public class ExecutedSimpleCheck {
    @NotNull private final CheckStatus status;
    @Nullable private final String matcherDescription;
    @Nullable private final String mismatchDescription;

    public ExecutedSimpleCheck(boolean matches, Matcher<?> matcher, Object actual) {
        matcherDescription = StringDescription.toString(matcher);
        mismatchDescription = matches ? null : getMismatchDescription(matcher, actual);
        status = matches ? PASSED : FAILED;
    }

    public ExecutedSimpleCheck(@NotNull CheckStatus status, @Nullable String matcherDescription, @Nullable String mismatchDescription) {
        this.status = status;
        this.matcherDescription = matcherDescription;
        this.mismatchDescription = mismatchDescription;
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    @NotNull
    public CheckStatus getStatus() {
        return status;
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
