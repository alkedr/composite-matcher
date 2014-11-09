package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class ExecutedCompositeCheck {
    @Nullable private String actualValueName = null;
    @Nullable private String actualValueAsString = null;
    @NotNull private List<ExecutedCompositeCheck> innerCompositeChecks = new ArrayList<>();  // сюда попадают ReportingMatcher'ы
    @NotNull private List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();  // сюда попадают другие матчеры

    public Boolean isSuccessful() {
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

    @NotNull
    public List<ExecutedCompositeCheck> getInnerCompositeChecks() {
        return unmodifiableList(innerCompositeChecks);
    }

    public void setInnerCompositeChecks(@NotNull List<ExecutedCompositeCheck> innerCompositeChecks) {
        this.innerCompositeChecks = new ArrayList<>(innerCompositeChecks);
    }

    @NotNull
    public List<ExecutedSimpleCheck> getInnerSimpleChecks() {
        return unmodifiableList(innerSimpleChecks);
    }

    public void setInnerSimpleChecks(@NotNull List<ExecutedSimpleCheck> innerSimpleChecks) {
        this.innerSimpleChecks = new ArrayList<>(innerSimpleChecks);
    }
}
