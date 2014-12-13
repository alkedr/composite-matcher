package alkedr.matchers.reporting;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.CompositeMatcher.ExecutedCheckStatus.*;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public abstract class CompositeMatcher<T> extends BaseMatcher<T> {
    private ExecutedCompositeCheck lastCheckResult = null;
    private T currentActualValue = null;


    protected abstract void check(@Nullable T actualValue);


    protected <U> void checkThat(@NotNull String name, @Nullable U value, @NotNull Matcher<? super U> matcher) {
        executeCheck(getOrCreateInnerCheck(name, value), value, matcher);
    }

    protected <U> CompositeMatcher<T> checkThat(@NotNull String name, @Nullable U value) {
        getOrCreateInnerCheck(name, value);
        return this;
    }

    protected void checkThat(@NotNull Matcher<? super T> matcher) {
        executeCheck(lastCheckResult, currentActualValue, matcher);
    }


    private static <U> void executeCheck(@NotNull ExecutedCompositeCheck storage, @Nullable U value, @NotNull Matcher<? super U> matcher) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(value);
        if (INNER_CHECK_RESULT.get() == null) {
            storage.simpleChecks.add(new ExecutedSimpleCheck(matcherResult, matcher, value));
        } else {
            mergeCompositeCheckIntoAnother(storage, INNER_CHECK_RESULT.get());
        }
    }

    private static void mergeCompositeCheckIntoAnother(ExecutedCompositeCheck storage, ExecutedCompositeCheck newCheck) {
        storage.simpleChecks.addAll(newCheck.simpleChecks);
        for (Map.Entry<String, ExecutedCompositeCheck> entry : newCheck.innerCompositeChecks.entrySet()) {
            if (storage.innerCompositeChecks.containsKey(entry.getKey())) {
                mergeCompositeCheckIntoAnother(storage.innerCompositeChecks.get(entry.getKey()), entry.getValue());
            } else {
                storage.innerCompositeChecks.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private <U> ExecutedCompositeCheck getOrCreateInnerCheck(String name, U value) {
        ExecutedCompositeCheck existingInnerCheck = lastCheckResult.innerCompositeChecks.get(name);
        if (existingInnerCheck == null) {
            ExecutedCompositeCheck newInnerCheck = new ExecutedCompositeCheck(String.valueOf(value));
            lastCheckResult.innerCompositeChecks.put(name, newInnerCheck);
            return newInnerCheck;
        } else {
            return existingInnerCheck;
        }
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




    public enum ExecutedCheckStatus {
        PASSED(true),
        FAILED(false),
        SKIPPED(true),
        ;

        private final boolean isSuccessful;

        ExecutedCheckStatus(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    public interface ExecutedCheck {
        ExecutedCheckStatus getStatus();
    }

    /**
     * Хранит информацию о запуске {@link CompositeMatcher}'а
     */
    public static class ExecutedCompositeCheck implements ExecutedCheck {
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
            boolean hasPassed = false;
            for (ExecutedCompositeCheck check : innerCompositeChecks.values()) {
                if (check.getStatus() == FAILED) return FAILED;
                if (check.getStatus() == PASSED) hasPassed = true;
            }
            for (ExecutedSimpleCheck check : simpleChecks) {
                if (check.getStatus() == FAILED) return FAILED;
                if (check.getStatus() == PASSED) hasPassed = true;
            }
            return hasPassed ? PASSED : SKIPPED;
        }

        @NotNull
        public Map<String, ExecutedCompositeCheck> getInnerCompositeChecks() {
            return unmodifiableMap(innerCompositeChecks);
        }

        @NotNull
        public List<ExecutedSimpleCheck> getSimpleChecks() {
            return unmodifiableList(simpleChecks);
        }
    }

    /**
     * Хранит информацию о запуске обычного Matcher'а
     */
    public static class ExecutedSimpleCheck implements ExecutedCheck {
        @Nullable private final String matcherDescription;
        @Nullable private final String mismatchDescription;

        public ExecutedSimpleCheck(boolean matches, Matcher<?> matcher, Object actual) {
            matcherDescription = StringDescription.toString(matcher);
            mismatchDescription = matches ? null : getMismatchDescription(matcher, actual);
        }

        public ExecutedSimpleCheck(@Nullable String matcherDescription, @Nullable String mismatchDescription) {
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
        }


        @Override
        @NotNull
        public ExecutedCheckStatus getStatus() {
            return mismatchDescription == null ? PASSED : FAILED;
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
