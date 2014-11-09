package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import alkedr.matchers.reporting.checks.PlannedCheck;
import org.hamcrest.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Базовый класс для матчеров, которые умеют строить красивые отчёты с результатами своей работы.
 * Наследники должны переопределить {@link ReportingMatcher2#getCheckPlan}.
 * Наследники могут переопределить {@link ReportingMatcher2#describeTo}.
 */
public abstract class ReportingMatcher2<T> extends TypeSafeMatcher<T> {
    private CheckResult checkResult = null;

    /**
     * {@link ReportingMatcher2.CheckPlan}
     * @param actualValue значение, которое было передано в {@link org.hamcrest.Matcher#matches}
     * @return план проверок для actualValue
     */
    @NotNull
    protected abstract CheckPlan getCheckPlan(T actualValue);


    /**
     * @return подробные результаты последнего вызова {@link ReportingMatcher2#matches} или null если
     * {@link ReportingMatcher2#matches} для данного инстанса не вызывался
     */
    @Nullable
    public CheckResult getCheckResult() {
        return checkResult;
    }


    @Override
    protected boolean matchesSafely(T item) {
        checkResult = getCheckPlan(item).execute();
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

        /**
         * Выполняет все проверки
         * @return результат проверок
         */
        public CheckResult execute() {
            List<ExecutedCheck> executedCompositeChecks = new ArrayList<>();
            for (PlannedCheck<?> plannedCheck : plannedChecks) {
                executedCompositeChecks.add(plannedCheck.execute());
            }
            return new CheckResult(executedCompositeChecks);
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


    public static class CheckResult {
        private final List<ExecutedCheck> executedCompositeChecks;

        public CheckResult(List<ExecutedCheck> executedCompositeChecks) {
            this.executedCompositeChecks = executedCompositeChecks;
        }

        public boolean isSuccessful() {
            return false;
        }
    }



    /**
     * Запланированная проверка объекта или его части.
     * Используется для указания ReportingMatcher'у что и как проверять.
     */
    public static class PlannedCheck<ActualValueType> {
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

        public ExecutedCheck execute() {
            List<ExecutedCheck> innerChecks = new ArrayList<>();

            for (Matcher<? super ActualValueType> matcher : matchers) {
                INNER_CHECK_RESULT.remove();
                boolean matcherResult = matcher.matches(actualValue);
                if (INNER_CHECK_RESULT.get() == null) {
                    innerChecks.add(new ExecutedCheck(null, null, getDescription(matcher),
                            matcherResult ? null : getMismatchDescription(matcher, actualValue)));
                } else {
                    innerChecks.add(INNER_CHECK_RESULT.get());
                }
            }

            ExecutedCheck executedCheck = new ExecutedCheck(name, String.valueOf(actualValue), innerChecks);
            INNER_CHECK_RESULT.set(executedCheck);

            return executedCheck;
        }

        private static String getDescription(SelfDescribing matcher) {
            StringDescription stringDescription = new StringDescription();
            matcher.describeTo(stringDescription);
            return stringDescription.toString();
        }

        private static <T> String getMismatchDescription(Matcher<? super T> matcher, T actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
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
        private static final ThreadLocal<ExecutedCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
    }




    public static class ExecutedCheck {
        @Nullable private String name = null;
        @Nullable private String actualValueAsString = null;
        // Либо matcherDescription и mismatchDescription null'ы, либо innerCompositeChecks пуст
        @Nullable private String matcherDescription = null;
        @Nullable private String mismatchDescription = null;
        @NotNull private List<ExecutedCheck> innerChecks = new ArrayList<>();

        public ExecutedCheck(@Nullable String name, @Nullable String actualValueAsString,
                             @Nullable String matcherDescription, @Nullable String mismatchDescription) {
            this.name = name;
            this.actualValueAsString = actualValueAsString;
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
        }

        public ExecutedCheck(@Nullable String name, @Nullable String actualValueAsString,
                             @NotNull List<ExecutedCheck> innerChecks) {
            this.name = name;
            this.actualValueAsString = actualValueAsString;
            this.innerChecks = innerChecks;
        }
    }
}
