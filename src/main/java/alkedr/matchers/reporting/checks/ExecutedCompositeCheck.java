package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class ExecutedCompositeCheck {
    @Nullable private final String actualValueName;
    @Nullable private final String actualValueAsString;
    @NotNull private final List<ExecutedCompositeCheck> innerCompositeChecks;  // сюда попадают ReportingMatcher'ы
    @NotNull private final List<ExecutedSimpleCheck> innerSimpleChecks;  // сюда попадают другие матчеры

    public ExecutedCompositeCheck(@Nullable String name, @Nullable String actualValueAsString,
                                  @NotNull List<ExecutedSimpleCheck> innerSimpleChecks,
                                  @NotNull List<ExecutedCompositeCheck> innerCompositeChecks) {
        this.actualValueName = name;
        this.actualValueAsString = actualValueAsString;
        this.innerSimpleChecks = innerSimpleChecks;
        this.innerCompositeChecks = innerCompositeChecks;
    }

    public boolean isSuccessful() {
        for (ExecutedCompositeCheck executedCompositeCheck : innerCompositeChecks) {
            if (!executedCompositeCheck.isSuccessful()) {
                return false;
            }
        }
        for (ExecutedSimpleCheck executedSimpleCheck : innerSimpleChecks) {
            if (!executedSimpleCheck.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAtLeastOneMatcher() {
        if (!innerSimpleChecks.isEmpty()) {
            return true;
        }
        for (ExecutedCompositeCheck executedCompositeCheck : innerCompositeChecks) {
            if (executedCompositeCheck.hasAtLeastOneMatcher()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLeaf() {
        return innerCompositeChecks.isEmpty();
    }


    @Nullable
    public String getActualValueName() {
        return actualValueName;
    }

    @Nullable
    public String getActualValueAsString() {
        return actualValueAsString;
    }

    @NotNull
    public List<ExecutedCompositeCheck> getInnerCompositeChecks() {
        return unmodifiableList(innerCompositeChecks);
    }

    @NotNull
    public List<ExecutedSimpleCheck> getInnerSimpleChecks() {
        return unmodifiableList(innerSimpleChecks);
    }
}
