package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.*;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.CompositeMatcher}'а
 */
public class ExecutedCompositeCheck implements ExecutedCheck {
    @NotNull private final String actualValueAsString;
    // сюда попадают матчеры, применяемые к полям, если несколько метчеров для одного поля, то мёржатся
    @NotNull private final Map<String, ExecutedCompositeCheck> innerCompositeChecks = new LinkedHashMap<>();
    // сюда попадают матчеры, применяемые ко всему actual
    @NotNull private final List<ExecutedSimpleCheck> simpleChecks = new ArrayList<>();


    public ExecutedCompositeCheck(@NotNull String actualValueAsString,
                                  @NotNull Iterable<? extends Map.Entry<String, ExecutedCompositeCheck>> innerCompositeChecks,
                                  @NotNull Collection<ExecutedSimpleCheck> simpleChecks) {
        this.actualValueAsString = actualValueAsString;
        for (Map.Entry<String, ExecutedCompositeCheck> entry : innerCompositeChecks) {
            this.innerCompositeChecks.put(entry.getKey(), entry.getValue());
        }
        this.simpleChecks.addAll(simpleChecks);
    }

    public ExecutedCompositeCheck(@NotNull String actualValueAsString) {
        this(actualValueAsString, new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(), new ArrayList<ExecutedSimpleCheck>());
    }


    @NotNull
    public String getActualValueAsString() {
        return actualValueAsString;
    }

    @Override
    @NotNull
    public ExecutedCheckStatus getStatus() {
        boolean hasPassedChecks = false;
        for (ExecutedCompositeCheck check : innerCompositeChecks.values()) {
            if (check.getStatus() == FAILED) return FAILED;
            if (check.getStatus() == PASSED) hasPassedChecks = true;
        }
        for (ExecutedSimpleCheck check : simpleChecks) {
            if (check.getStatus() == FAILED) return FAILED;
            if (check.getStatus() == PASSED) hasPassedChecks = true;
        }
        return hasPassedChecks ? PASSED : SKIPPED;
    }

    @NotNull
    public Map<String, ExecutedCompositeCheck> getInnerCompositeChecks() {
        return innerCompositeChecks;
    }

    @NotNull
    public List<ExecutedSimpleCheck> getSimpleChecks() {
        return simpleChecks;
    }
}
