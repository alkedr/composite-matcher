package alkedr.matchers.reporting.checks;

import ch.lambdaj.Lambda;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.*;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.ReportingMatcher}'а
 */
public class ExecutedCompositeCheck implements ExecutedCheck {
    @Nullable private final Object actualValue;
    @Nullable private ExecutedCheckStatus status = null;
    @NotNull private final List<ExecutedSimpleCheck> simpleChecks = new ArrayList<>();
    @NotNull private final Map<String, Map<Object, ExecutedCompositeCheck>> compositeChecks = new LinkedHashMap<>();


    public ExecutedCompositeCheck(@Nullable Object actualValue,
                                  @NotNull Iterable<? extends Map.Entry<String, Map<Object, ExecutedCompositeCheck>>> compositeChecks,
                                  @NotNull Collection<ExecutedSimpleCheck> simpleChecks) {
        this.actualValue = actualValue;
        for (Map.Entry<String, Map<Object, ExecutedCompositeCheck>> entry : compositeChecks) {
            this.compositeChecks.put(entry.getKey(), entry.getValue());
        }
        this.simpleChecks.addAll(simpleChecks);
    }

    public ExecutedCompositeCheck(@Nullable Object actualValue) {
        this(actualValue, new ArrayList<Map.Entry<String, Map<Object, ExecutedCompositeCheck>>>(), new ArrayList<ExecutedSimpleCheck>());
    }


    @Nullable
    public Object getActualValue() {
        return actualValue;
    }

    @Override
    @NotNull
    public ExecutedCheckStatus getStatus() {
        if (status != null) return status;
        boolean hasPassedChecks = false;
        for (ExecutedCompositeCheck check : Lambda.<ExecutedCompositeCheck>flatten(compositeChecks)) {
            if (!check.getStatus().isSuccessful()) return FAILED;
            if (check.getStatus() == PASSED) hasPassedChecks = true;
        }
        for (ExecutedCheck check : simpleChecks) {
            if (!check.getStatus().isSuccessful()) return FAILED;
            if (check.getStatus() == PASSED) hasPassedChecks = true;
        }
        return hasPassedChecks ? PASSED : SKIPPED;
    }

    @NotNull
    public Map<String, Map<Object, ExecutedCompositeCheck>> getCompositeChecks() {
        return unmodifiableMap(compositeChecks);
    }

    @NotNull
    public List<ExecutedSimpleCheck> getSimpleChecks() {
        return unmodifiableList(simpleChecks);
    }




    public boolean checkSilently(Matcher<?> matcher) {
        ExecutedCompositeCheck check = executeCheck(actualValue, matcher);
        if (!check.getStatus().isSuccessful()) {
            addDataFrom(check);
            return false;
        }
        return true;
    }

    public <U> boolean checkAndReportIfMatches(Matcher<? super U> matcher) {
        ExecutedCompositeCheck check = executeCheck(actualValue, matcher);
        if (check.getStatus().isSuccessful()) {
            addDataFrom(check);
            return true;
        }
        return false;
    }

    public <U> boolean checkAndReportIfMatches(String name, U value, Matcher<? super U> matcher) {
        return getOrAddValue(name, value).checkAndReportIfMatches(matcher);
    }

    public void reportMissingValue(String name) {
        getOrAddValue(name, null).status = MISSING;
    }

    public <U> void checkThat(String name, Object value, Matcher<?> matcher) {
        getOrAddValue(name, value).addDataFrom(executeCheck(value, matcher));
    }

    public <U> void reportValue(String name, Object value) {
        getOrAddValue(name, value);
    }


    private <U> ExecutedCompositeCheck getOrAddValue(String name, U value) {
        Map<Object, ExecutedCompositeCheck> valueToChecks = compositeChecks.get(name);
        if (valueToChecks == null) {
            valueToChecks = new LinkedHashMap<>();
            compositeChecks.put(name, valueToChecks);
        }
        ExecutedCompositeCheck check = valueToChecks.get(value);
        if (check == null) {
            check = new ExecutedCompositeCheck(value);
            valueToChecks.put(value, check);
        }
        return check;
    }

    public void addDataFrom(ExecutedCompositeCheck check) {
        if (!Objects.equals(actualValue, check.actualValue)) {
            assert false;
        }
        simpleChecks.addAll(check.simpleChecks);
        compositeChecks.putAll(check.compositeChecks);
    }


    private static <U> ExecutedCompositeCheck executeCheck(@Nullable Object value, @NotNull Matcher<?> matcher) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(value);
        if (INNER_CHECK_RESULT.get() == null) {
            ExecutedCompositeCheck result = new ExecutedCompositeCheck(value);
            result.simpleChecks.add(new ExecutedSimpleCheck(StringDescription.toString(matcher),
                    matcherResult ? null : getMismatchDescription(matcher, value)));
            return result;
        } else {
            return INNER_CHECK_RESULT.get();
        }
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    /**
     * хранит информацию о выполнении другого CompositeMatcher'а, которое было вызвано из текущего CompositeMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего CompositeMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является CompositeMatcher'ом или использует CompositeMatcher внутри
     * нельзя просто попытаться покастить matcher к CompositeMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    public static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
