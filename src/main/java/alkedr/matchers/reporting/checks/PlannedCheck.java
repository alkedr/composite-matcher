package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlannedCheck<ActualValueType> {
    @NotNull private String actualValueName;
    @NotNull private ActualValueType actualValue;
    @NotNull private Matcher<? super ActualValueType> matcher;

    public PlannedCheck(@NotNull String actualValueName, @NotNull ActualValueType actualValue,
                        @NotNull Matcher<? super ActualValueType> matcher) {
        this.actualValueName = actualValueName;
        this.actualValue = actualValue;
        this.matcher = matcher;
    }

    @NotNull
    public String getActualValueName() {
        return actualValueName;
    }

    public void setActualValueName(@NotNull String actualValueName) {
        this.actualValueName = actualValueName;
    }

    @NotNull
    public ActualValueType getActualValue() {
        return actualValue;
    }

    public void setActualValue(@NotNull ActualValueType actualValue) {
        this.actualValue = actualValue;
    }

    @NotNull
    public Matcher<? super ActualValueType> getMatcher() {
        return matcher;
    }

    public void setMatcher(@NotNull Matcher<? super ActualValueType> matcher) {
        this.matcher = matcher;
    }
}
