package alkedr.matchers.reporting;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.CompositeMatcher2.CheckStatus.*;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public abstract class CompositeMatcher2<T> extends BaseMatcher<T> {
    private ExecutedCompositeCheck lastCheckResult = null;
    private T currentActualValue = null;


    protected abstract void check(@Nullable T actualValue);


    protected <U> void checkThat(@NotNull String name, @Nullable U value, @NotNull Matcher<? super U> matcher) {
        lastCheckResult.addCheckResult(name, value, executeCheck(value, matcher));
    }

    protected void checkThat(@NotNull Matcher<? super T> matcher) {
        lastCheckResult.addCheckResult(executeCheck(currentActualValue, matcher));
    }

    protected static <U> ExecutedCheck executeCheck(@Nullable U value, @NotNull Matcher<? super U> matcher) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(value);
        return (INNER_CHECK_RESULT.get() == null) ? new ExecutedSimpleCheck(matcherResult, matcher, value) : INNER_CHECK_RESULT.get();
    }

    protected <U> CompositeMatcher2<T> ensureFieldExists(String name, U value) {
        lastCheckResult.getOrCreateCompositeCheckForField(name, value);
        return this;
    }


    @Override
    public boolean matches(Object item) {
        lastCheckResult = new ExecutedCompositeCheck(String.valueOf(item));
        currentActualValue = (T)item; // FIXME: safe cast
        check(currentActualValue);
        currentActualValue = null;
        INNER_CHECK_RESULT.set(lastCheckResult);
        return lastCheckResult.getStatus().isSuccessful();
    }

    public ExecutedCompositeCheck getLastCheckResult() {
        return lastCheckResult;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        // TODO:
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }




    public enum CheckStatus {
        PASSED(true),
        FAILED(false),
        SKIPPED(true),
        ;

        private final boolean isSuccessful;

        CheckStatus(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    public interface ExecutedCheck {
        CheckStatus getStatus();
        void storeInto(@NotNull ExecutedCompositeCheck check);
    }

    /**
     * Хранит информацию о запуске {@link alkedr.matchers.reporting.CompositeMatcher2}'а
     */
    public static class ExecutedCompositeCheck implements ExecutedCheck {
        @NotNull private final String actualValueAsString;

        // статус меняется по мере добавления проверок методами addSimpleCheck и addCompositeCheck
        @NotNull private CheckStatus status = SKIPPED;

        // сюда попадают матчеры, применяемые к полям, если несколько метчеров для одного поля, то мёржатся
        @NotNull private final Map<String, ExecutedCompositeCheck> innerCompositeChecks = new LinkedHashMap<>();

        // сюда попадают матчеры, применяемые ко всему actual
        @NotNull private final List<ExecutedSimpleCheck> simpleChecks = new ArrayList<>();


        public ExecutedCompositeCheck(@NotNull String actualValueAsString, @NotNull CheckStatus status,
                                      @NotNull Iterable<? extends Map.Entry<String, ExecutedCompositeCheck>> innerCompositeChecks,
                                      @NotNull Collection<ExecutedSimpleCheck> simpleChecks) {
            this.actualValueAsString = actualValueAsString;
            this.status = status;
            for (Map.Entry<String, ExecutedCompositeCheck> entry : innerCompositeChecks) {
                this.innerCompositeChecks.put(entry.getKey(), entry.getValue());
            }
            this.simpleChecks.addAll(simpleChecks);
        }

        public ExecutedCompositeCheck(@NotNull String actualValueAsString) {
            this(actualValueAsString, SKIPPED, new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(), new ArrayList<ExecutedSimpleCheck>());
        }


        @Override
        public void storeInto(@NotNull ExecutedCompositeCheck check) {
            if ((check.status == SKIPPED) || ((check.status == PASSED) && (status == FAILED))) {
                check.status = status;
            }
//            if (!check.getActualValueAsString().equals(actualValueAsString)) {
//                throw new RuntimeException("");
//            }
            check.simpleChecks.addAll(simpleChecks);
            for (Map.Entry<String, ExecutedCompositeCheck> entry : innerCompositeChecks.entrySet()) {
                if (check.innerCompositeChecks.containsKey(entry.getKey())) {
                    check.innerCompositeChecks.get(entry.getKey()).addCheckResult(entry.getValue());
                } else {
                    check.innerCompositeChecks.put(entry.getKey(), entry.getValue());
                }
            }
        }


        @NotNull
        public String getActualValueAsString() {
            return actualValueAsString;
        }

        @Override
        @NotNull
        public CheckStatus getStatus() {
            return status;
        }

        @NotNull
        public Map<String, ExecutedCompositeCheck> getInnerCompositeChecks() {
            return unmodifiableMap(innerCompositeChecks);
        }

        @NotNull
        public List<ExecutedSimpleCheck> getSimpleChecks() {
            return unmodifiableList(simpleChecks);
        }


        public <U> void addCheckResult(String name, @Nullable U value, ExecutedCheck executedCheck) {
            executedCheck.storeInto(getOrCreateCompositeCheckForField(name, value));
            if ((status == SKIPPED) || ((status == PASSED) && (executedCheck.getStatus() == FAILED))) {
                status = executedCheck.getStatus();
            }
        }

        public void addCheckResult(ExecutedCheck executedCheck) {
            executedCheck.storeInto(this);
        }

        private <U> ExecutedCompositeCheck getOrCreateCompositeCheckForField(String name, U value) {
            ExecutedCompositeCheck executedCompositeCheck = new ExecutedCompositeCheck(String.valueOf(value));
            innerCompositeChecks.put(name, executedCompositeCheck);
            return executedCompositeCheck;
        }
    }

    /**
     * Хранит информацию о запуске обычного Matcher'а
     */
    public static class ExecutedSimpleCheck implements ExecutedCheck {
        @NotNull private final CheckStatus status;
        @Nullable private final String matcherDescription;
        @Nullable private final String mismatchDescription;

        public ExecutedSimpleCheck(boolean matches, Matcher<?> matcher, Object actual) {
            matcherDescription = StringDescription.toString(matcher);
            mismatchDescription = matches ? null : getMismatchDescription(matcher, actual);
            status = matches ? PASSED : FAILED;
        }

        public ExecutedSimpleCheck(@NotNull CheckStatus status, @Nullable String matcherDescription, @Nullable String mismatchDescription) {
            this.status = status;
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
        }


        @Override
        public void storeInto(@NotNull ExecutedCompositeCheck check) {
            if ((check.status == SKIPPED) || ((check.status == PASSED) && (status == FAILED))) {
                check.status = status;
            }
            check.simpleChecks.add(this);
        }


        @Override
        @NotNull
        public CheckStatus getStatus() {
            return status;
        }

        @Nullable
        public String getMatcherDescription() {
            return matcherDescription;
        }

        @Nullable
        public String getMismatchDescription() {
            return mismatchDescription;
        }


        private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
        }
    }


    /**
     * хранит информацию о выполнении другого CompositeMatcher'а, которое было вызвано из текущего CompositeMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего CompositeMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является CompositeMatcher'ом или использует CompositeMatcher внутри
     * нельзя просто попытаться покастить matcher к CompositeMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
