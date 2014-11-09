package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.*;

public class CheckResult {
    @Nullable private String actualValueName = null;
    @Nullable private String actualValueAsString = null;
    @Nullable private String matcherDescription = null;
    @Nullable private String mismatchDescription = null;
    @NotNull private List<CheckResult> fields = new ArrayList<>();
    @NotNull private List<CheckResult> nonFields = new ArrayList<>();

    public Boolean isSuccessful() {
        if (mismatchDescription != null) {
            return false;
        }
        for (CheckResult fieldCheck : fields) {
            if (!fieldCheck.isSuccessful()) {
                return false;
            }
        }
        for (CheckResult nonFieldCheck : nonFields) {
            if (!nonFieldCheck.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public String getActualValueName() {
        return actualValueName;
    }

    public void setActualValueName(@Nullable String actualValueName) {
        this.actualValueName = actualValueName;
    }

    @Nullable
    public String getActualValueAsString() {
        return actualValueAsString;
    }

    public void setActualValueAsString(@Nullable String actualValueAsString) {
        this.actualValueAsString = actualValueAsString;
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

    @NotNull
    public List<CheckResult> getFields() {
        return unmodifiableList(fields);
    }

    public void setFields(@NotNull List<CheckResult> fields) {
        this.fields = new ArrayList<>(fields);
    }

    @NotNull
    public List<CheckResult> getNonFields() {
        return unmodifiableList(nonFields);
    }

    public void setNonFields(@NotNull List<CheckResult> nonFields) {
        this.nonFields = new ArrayList<>(nonFields);
    }


    public boolean hasAtLeastOneMatcher() {
        if (matcherDescription != null) {
            return true;
        }
        for (CheckResult fieldCheck : fields) {
            if (fieldCheck.hasAtLeastOneMatcher()) {
                return true;
            }
        }
        for (CheckResult nonFieldCheck : nonFields) {
            if (nonFieldCheck.hasAtLeastOneMatcher()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLeaf() {
        return fields.isEmpty() && nonFields.isEmpty();
    }
}
