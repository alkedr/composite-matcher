package com.github.alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранит информацию о запуске обычного Matcher'а
 */
public class ExecutedSimpleCheck implements ExecutedCheck {
    @NotNull private final String matcherDescription;
    @Nullable private final String mismatchDescription;

    public ExecutedSimpleCheck(@NotNull String matcherDescription, @Nullable String mismatchDescription) {
        this.matcherDescription = matcherDescription;
        this.mismatchDescription = mismatchDescription;
    }

    @Override
    public Status getStatus() {
        return mismatchDescription == null ? Status.PASSED : Status.FAILED;
    }

    @NotNull
    public String getMatcherDescription() {
        return matcherDescription;
    }

    @Nullable
    public String getMismatchDescription() {
        return mismatchDescription;
    }
}
