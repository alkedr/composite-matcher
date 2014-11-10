package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.Nullable;

public class ExecutedSimpleCheck {
    @Nullable private final String matcherDescription;
    @Nullable private final String mismatchDescription;


    public ExecutedSimpleCheck(@Nullable String matcherDescription, @Nullable String mismatchDescription) {
        this.matcherDescription = matcherDescription;
        this.mismatchDescription = mismatchDescription;
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
