package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.hamcrest.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Базовый класс для матчеров, которые умеют строить красивые отчёты с результатами своей работы.
 * Наследники должны переопределить {@link ReportingMatcher#checkPlanFor}.
 * Наследники могут переопределить {@link ReportingMatcher#describeTo}.
 */
public abstract class ReportingMatcher<T> extends TypeSafeMatcher<T> {
    private ExecutedCompositeCheck checkResult = null;

    /**
     * {@link ReportingMatcher.CheckPlan}
     * @param actualValue значение, которое было передано в {@link org.hamcrest.Matcher#matches}
     * @return план проверок для actualValue
     */
    @NotNull
    protected abstract CheckPlan checkPlanFor(T actualValue);


    /**
     * @return подробные результаты последнего вызова {@link ReportingMatcher#matches} или null если
     * {@link ReportingMatcher#matches} для данного инстанса не вызывался
     */
    @Nullable
    public ExecutedCompositeCheck getCheckResult() {
        return checkResult;
    }


    @Override
    protected boolean matchesSafely(T item) {
        checkResult = execute(checkPlanFor(item));
        return checkResult.isSuccessful();
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        // TODO:
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }



    private static ExecutedCompositeCheck execute(CheckPlan checkPlan) {
        List<ExecutedCompositeCheck> executedCompositeChecks = new ArrayList<>();
        for (PlannedCheck<?> plannedCheck : checkPlan.getPlannedChecks()) {
            List<ExecutedSimpleCheck> innerSimpleChecks = new ArrayList<>();
            for (Matcher<?> matcher : plannedCheck.getMatchers()) {
                INNER_CHECK_RESULT.remove();
                boolean matcherResult = matcher.matches(plannedCheck.getActualValue());
                if (INNER_CHECK_RESULT.get() == null) {
                    innerSimpleChecks.add(new ExecutedSimpleCheck(
                            getDescription(matcher),
                            matcherResult ? null : getMismatchDescription(matcher, plannedCheck.getActualValue())
                    ));
                } else {
                    executedCompositeChecks.add(new ExecutedCompositeCheck(
                            plannedCheck.getName(), String.valueOf(plannedCheck.getActualValue()),
                            INNER_CHECK_RESULT.get().getInnerSimpleChecks(), INNER_CHECK_RESULT.get().getInnerCompositeChecks()
                    ));
                }
            }
            if (!innerSimpleChecks.isEmpty()) {
                executedCompositeChecks.add(new ExecutedCompositeCheck(
                        plannedCheck.getName(), String.valueOf(plannedCheck.getActualValue()),
                        innerSimpleChecks, new ArrayList<ExecutedCompositeCheck>()
                ));
            }
        }
        ExecutedCompositeCheck executedCompositeCheck = new ExecutedCompositeCheck(
                null, null,
                new ArrayList<ExecutedSimpleCheck>(), executedCompositeChecks
        );
        INNER_CHECK_RESULT.set(executedCompositeCheck);
        return executedCompositeCheck;
    }


    private static String getDescription(SelfDescribing matcher) {
        StringDescription stringDescription = new StringDescription();
        matcher.describeTo(stringDescription);
        return stringDescription.toString();
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    /**
     * хранит информацию о запуске другого ReportingMatcher'а, который быз вызван из текущего ReportingMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего матчера к отчёту текущего матчера
     * checkThat зануляет INNER_CHECK_RESULT и вызывает matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();



    public static class CheckPlan {
        private final Collection<PlannedCheck<?>> plannedChecks = new ArrayList<>();

        /**
         * Добавляет значение без проверок в список.
         * Значения без проверок отображаются в отчёте.
         * Это может быть полезно для контроля за тем, какие поля проверяются, а какие нет.
         * Если для данного значения уже были добавлены проверки, то этот метод ничего не изменит.
         * @param name описание значения, часть ключа, сравнивается по .equals()
         * @param value значение, часть ключа, сравнивается по ==
         * @return this
         */
        public <U> CheckPlan addCheck(String name, U value) {
            getOrAddCheckForValue(name, value);
            return this;
        }

        /**
         * Добавляет проверку значения в список.
         * Если для данного значения уже были добавлены проверки, то они сгруппируются.
         * @param name описание значения, часть ключа, сравнивается по .equals()
         * @param value значение, часть ключа, сравнивается по ==
         * @param matcher матчер
         * @return this
         */
        public <U> CheckPlan addCheck(String name, U value, Matcher<? super U> matcher) {
            getOrAddCheckForValue(name, value).addMatcher(matcher);
            return this;
        }

        public Collection<PlannedCheck<?>> getPlannedChecks() {
            return plannedChecks;
        }

        private <U> PlannedCheck<U> getOrAddCheckForValue(String name, U value) {
            for (PlannedCheck<?> plannedCheck : plannedChecks) {
                if ((plannedCheck.getActualValue() == value) && plannedCheck.getName().equals(name)) {  // '==' не случайно
                    return (PlannedCheck<U>)plannedCheck;
                }
            }
            PlannedCheck<U> newPlannedCheck = new PlannedCheck<>(name, value, new ArrayList<Matcher<? super U>>());
            plannedChecks.add(newPlannedCheck);
            return newPlannedCheck;
        }
    }



    /**
     * Запланированная проверка объекта или его части.
     * Используется для указания ReportingMatcher'у что и как проверять.
     */
    private static class PlannedCheck<ActualValueType> {
        @NotNull private final String name;
        @NotNull private final ActualValueType actualValue;
        @NotNull private final List<Matcher<? super ActualValueType>> matchers;


        /**
         * @param name название поля
         * @param actualValue значение поля
         * @param matchers матчеры, если пустой список, то поле отобразится в отчёте как непроверенное
         */
        private PlannedCheck(@NotNull String name, @NotNull ActualValueType actualValue,
                             @NotNull List<? extends Matcher<? super ActualValueType>> matchers) {
            this.name = name;
            this.actualValue = actualValue;
            this.matchers = new ArrayList<>(matchers);
        }

        public void addMatcher(Matcher<? super ActualValueType> matcher) {
            matchers.add(matcher);
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
    }

}
