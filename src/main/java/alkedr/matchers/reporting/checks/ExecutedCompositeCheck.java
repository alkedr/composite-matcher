package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.*;
import static java.util.Collections.unmodifiableList;

/**
 * Хранит информацию о запуске ReportingMatcher'а
 */
public class ExecutedCompositeCheck {
    @Nullable private final String actualValueAsString;
    @NotNull private final Map<String, ExecutedCompositeCheck> valueNameToInnerCompositeCheck = new LinkedHashMap<>(); // сюда попадают ReportingMatcher'ы, если их несколько, то мёржатся
    @NotNull private final List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();  // сюда попадают другие матчеры

    public ExecutedCompositeCheck(@Nullable String actualValueAsString) {
        this.actualValueAsString = actualValueAsString;
    }

    public void addCompositeCheck(String name, ExecutedCompositeCheck check) {
        valueNameToInnerCompositeCheck.put(name, check);
    }

    public void addSimpleCheck(String name, String valueAsString, ExecutedSimpleCheck check) {
        if (!valueNameToInnerCompositeCheck.containsKey(name)) {
            valueNameToInnerCompositeCheck.put(name, new ExecutedCompositeCheck(valueAsString));
        }
        valueNameToInnerCompositeCheck.get(name).innerSimpleChecks.add(check);
    }

    public boolean isSuccessful() {
        for (ExecutedCompositeCheck executedCompositeCheck : valueNameToInnerCompositeCheck.values()) {
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
        for (ExecutedCompositeCheck executedCompositeCheck : valueNameToInnerCompositeCheck.values()) {
            if (executedCompositeCheck.hasAtLeastOneMatcher()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLeaf() {
        return valueNameToInnerCompositeCheck.isEmpty();
    }

    @Nullable
    public String getActualValueAsString() {
        return actualValueAsString;
    }

    @NotNull
    public Map<String, ExecutedCompositeCheck> getValueNameToInnerCompositeCheck() {
        return unmodifiableMap(valueNameToInnerCompositeCheck);
    }

    @NotNull
    public List<ExecutedSimpleCheck> getInnerSimpleChecks() {
        return unmodifiableList(innerSimpleChecks);
    }
}
