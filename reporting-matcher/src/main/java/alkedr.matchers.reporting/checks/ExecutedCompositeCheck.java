package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.checks.CheckStatus.*;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.ReportingMatcher}'а
 */
public class ExecutedCompositeCheck {
    @Nullable private final String actualValueAsString;

    // статус меняется по мере добавления проверок методами addSimpleCheck и addCompositeCheck
    @NotNull private CheckStatus status = SKIPPED;

    // сюда попадают ReportingMatcher'ы, если несколько метчеров для одного поля, то мёржатся
    @NotNull private final Map<String, ExecutedCompositeCheck> valueNameToInnerCompositeCheck = new LinkedHashMap<>();

    // сюда попадают другие матчеры
    @NotNull private final List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();

    public ExecutedCompositeCheck(@Nullable String actualValueAsString, @NotNull CheckStatus status,
                                  @NotNull Iterable<? extends Map.Entry<String, ExecutedCompositeCheck>> valueNameToInnerCompositeCheck,
                                  @NotNull Collection<ExecutedSimpleCheck> innerSimpleChecks) {
        this.actualValueAsString = actualValueAsString;
        this.status = status;
        for (Map.Entry<String, ExecutedCompositeCheck> entry : valueNameToInnerCompositeCheck) {
            this.valueNameToInnerCompositeCheck.put(entry.getKey(), entry.getValue());
        }
        this.innerSimpleChecks.addAll(innerSimpleChecks);
    }

    public ExecutedCompositeCheck(@Nullable String actualValueAsString) {
        this(actualValueAsString, SKIPPED, new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(), new ArrayList<ExecutedSimpleCheck>());
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
        valueNameToInnerCompositeCheck.get(name).status = check.getStatus();
        valueNameToInnerCompositeCheck.get(name).innerSimpleChecks.add(check);
    }

    public void addSimpleCheck(ExecutedSimpleCheck check) {
        if ((status == SKIPPED) || (status == PASSED)) {
            status = check.getStatus();
        }
        innerSimpleChecks.add(check);
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
}
