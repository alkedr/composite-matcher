package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Запланированная проверка объекта или его части.
 * Используется для указания ReportingMatcher'у что и как проверять.
 */
public class PlannedCheck<ActualValueType> {
    @NotNull private final String name;
    @NotNull private final ActualValueType actualValue;
    @NotNull private final List<Matcher<? super ActualValueType>> matchers;


    /**
     * @param name название поля
     * @param actualValue значение поля
     * @param matchers матчеры, если пустой список, то поле отобразится в отчёте как непроверенное
     */
    public PlannedCheck(@NotNull String name, @NotNull ActualValueType actualValue,
                        @NotNull List<? extends Matcher<? super ActualValueType>> matchers) {
        this.name = name;
        this.actualValue = actualValue;
        this.matchers = new ArrayList<>(matchers);
    }

    public void addMatcher(Matcher<? super ActualValueType> matcher) {
        matchers.add(matcher);
    }

    public ExecutedCompositeCheck execute() {
        List<ExecutedCompositeCheck> innerCompositeChecks = new ArrayList<>();
        List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();

        for (Matcher<? super ActualValueType> matcher : matchers) {
            INNER_CHECK_RESULT.remove();
            boolean matcherResult = matcher.matches(actualValue);
            if (INNER_CHECK_RESULT.get() == null) {
                ExecutedSimpleCheck executedSimpleCheck = new ExecutedSimpleCheck();
                StringDescription stringDescription = new StringDescription();
                matcher.describeTo(stringDescription);
                executedSimpleCheck.setMatcherDescription(stringDescription.toString());
                if (!matcherResult) {
                    StringDescription stringMismatchDescription = new StringDescription();
                    matcher.describeMismatch(actualValue, stringMismatchDescription);
                    executedSimpleCheck.setMismatchDescription(stringMismatchDescription.toString());
                }
            } else {
                innerCompositeChecks.add(INNER_CHECK_RESULT.get());
            }
        }

        ExecutedCompositeCheck executedCompositeCheck = new ExecutedCompositeCheck();
        executedCompositeCheck.setActualValueName(name);
        executedCompositeCheck.setActualValueAsString(String.valueOf(actualValue));
        executedCompositeCheck.setInnerCompositeChecks(innerCompositeChecks);  // TODO: merge composite checks?
        executedCompositeCheck.setInnerSimpleChecks(innerSimpleChecks);

        INNER_CHECK_RESULT.set(executedCompositeCheck);

        return executedCompositeCheck;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public ActualValueType getActualValue() {
        return actualValue;
    }

    @NotNull
    public List<Matcher<? super ActualValueType>> getMatchers() {
        return unmodifiableList(matchers);
    }


    /**
     * хранит информацию о запуске другого ReportingMatcher'а, который быз вызван из текущего ReportingMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего матчера к отчёту текущего матчера
     * checkThat зануляет INNER_CHECK_RESULT и вызывает matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
