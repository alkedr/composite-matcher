package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.MISSING;

public class CheckExecutionUtils {

    public static void beginMatcherExecution() {
        INNER_CHECK_RESULT.remove();
    }

    public static void endMatcherExecution(ExecutedCompositeCheck check) {
        INNER_CHECK_RESULT.set(check);
    }


    /**
     * @param value значение, которое нужно проверить
     * @param matcher матчер, которым нужно проверять value
     * @return ExecutedCompositeCheck, в котором будет заполнено либо simpleChecks, либо compositeChecks
     */
    public static ExecutedCompositeCheck executeCheck(@Nullable Object value, @NotNull Matcher<?> matcher) {
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
     *
     * TODO: проверять, что хранящаяся здесь проверка выполнена на том значении, на котором надо
     * TODO: INNER_CHECK_RESULT - List<ExecutedCompositeCheck>
     *  (нужно для случаев типа  assertAndReportThat(actual, contains(reportingMatcher()))
     */
    public static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
