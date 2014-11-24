package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.checks.CheckStatus.FAILED;
import static alkedr.matchers.reporting.checks.CheckStatus.PASSED;
import static alkedr.matchers.reporting.checks.CheckStatus.SKIPPED;
import static java.util.Collections.*;
import static java.util.Collections.unmodifiableList;

/**
 * Хранит информацию о запуске ReportingMatcher'а
 */
public class ExecutedCompositeCheck {
    @Nullable private final String actualValueAsString;

    // статус меняется по мере добавления проверок методами addSimpleCheck и #addCompositeCheck
    @NotNull private CheckStatus status = SKIPPED;

    // сюда попадают ReportingMatcher'ы, если несколько метчеров для одного поля, то мёржатся
    @NotNull private final Map<String, ExecutedCompositeCheck> valueNameToInnerCompositeCheck = new LinkedHashMap<>();

    // сюда попадают другие матчеры
    @NotNull private final List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();


    public ExecutedCompositeCheck(@Nullable String actualValueAsString) {
        this.actualValueAsString = actualValueAsString;
    }

    public void addCompositeCheck(String name, ExecutedCompositeCheck check) {
        if ((status == SKIPPED) || ((status == PASSED) && (check.status == FAILED))) {
            status = check.getStatus();
        }
        valueNameToInnerCompositeCheck.put(name, check);
    }

    public void addSimpleCheck(String name, String valueAsString, ExecutedSimpleCheck check) {
        if ((status == SKIPPED) || (status == PASSED)) {
            status = check.getStatus();
        }
        if (!valueNameToInnerCompositeCheck.containsKey(name)) {
            valueNameToInnerCompositeCheck.put(name, new ExecutedCompositeCheck(valueAsString));
        }
        valueNameToInnerCompositeCheck.get(name).innerSimpleChecks.add(check);
    }


    @Nullable
    public String getActualValueAsString() {
        return actualValueAsString;
    }

    @NotNull
    public CheckStatus getStatus() {
        return status;
    }

    @NotNull
    public Map<String, ExecutedCompositeCheck> getValueNameToInnerCompositeCheck() {
        return unmodifiableMap(valueNameToInnerCompositeCheck);
    }

    @NotNull
    public List<ExecutedSimpleCheck> getInnerSimpleChecks() {
        return unmodifiableList(innerSimpleChecks);
    }

    public Set<Map.Entry<String, ExecutedCompositeCheck>> valueNameToInnerCompositeCheckEntries() {
        return valueNameToInnerCompositeCheck.entrySet();
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

}
