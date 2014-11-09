package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.Nullable;

public class ExecutedSimpleCheck {
    @Nullable private String matcherDescription = null;
    @Nullable private String mismatchDescription = null;

    public boolean isSuccessful() {
        return mismatchDescription != null;
    }

    @Nullable
    public String getMatcherDescription() {
        return matcherDescription;
    }

    public void setMatcherDescription(@Nullable String matcherDescription) {
        this.matcherDescription = matcherDescription;
    }

    @Nullable
    public String getMismatchDescription() {
        return mismatchDescription;
    }

    public void setMismatchDescription(@Nullable String mismatchDescription) {
        this.mismatchDescription = mismatchDescription;
    }
}
