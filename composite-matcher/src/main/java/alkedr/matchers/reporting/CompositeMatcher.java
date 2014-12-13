package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import alkedr.matchers.reporting.reporters.PlainTextReporter;
import alkedr.matchers.reporting.reporters.Reporter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class CompositeMatcher<T> extends BaseMatcher<T> {
    private ExecutedCompositeCheck lastCheckResult = null;
    private T currentActualValue = null;
    private Reporter reporter = new PlainTextReporter();
    private String mismatchDescription = null;


    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }


    protected abstract void check(@Nullable T actualValue);


    protected <U> void checkThat(@NotNull String name, @Nullable U value, @NotNull Matcher<? super U> matcher) {
        executeCheck(getOrCreateInnerCheck(name, value), value, matcher);
    }

    protected void checkThat(@NotNull String name, @Nullable Object value) {
        getOrCreateInnerCheck(name, value);
    }

    protected void checkThat(@NotNull Matcher<? super T> matcher) {
        executeCheck(lastCheckResult, currentActualValue, matcher);
    }


    private static <U> void executeCheck(@NotNull ExecutedCompositeCheck storage, @Nullable U value, @NotNull Matcher<? super U> matcher) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(value);
        if (INNER_CHECK_RESULT.get() == null) {
            storage.getSimpleChecks().add(new ExecutedSimpleCheck(matcherResult, matcher, value));
        } else {
            mergeCompositeCheckIntoAnother(storage, INNER_CHECK_RESULT.get());
        }
    }

    private static void mergeCompositeCheckIntoAnother(ExecutedCompositeCheck storage, ExecutedCompositeCheck newCheck) {
        storage.getSimpleChecks().addAll(newCheck.getSimpleChecks());
        for (Map.Entry<String, ExecutedCompositeCheck> entry : newCheck.getInnerCompositeChecks().entrySet()) {
            if (storage.getInnerCompositeChecks().containsKey(entry.getKey())) {
                mergeCompositeCheckIntoAnother(storage.getInnerCompositeChecks().get(entry.getKey()), entry.getValue());
            } else {
                storage.getInnerCompositeChecks().put(entry.getKey(), entry.getValue());
            }
        }
    }

    private <U> ExecutedCompositeCheck getOrCreateInnerCheck(String name, U value) {
        ExecutedCompositeCheck existingInnerCheck = lastCheckResult.getInnerCompositeChecks().get(name);
        if (existingInnerCheck == null) {
            ExecutedCompositeCheck newInnerCheck = new ExecutedCompositeCheck(String.valueOf(value));
            lastCheckResult.getInnerCompositeChecks().put(name, newInnerCheck);
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
        INNER_CHECK_RESULT.set(lastCheckResult);
        mismatchDescription = reporter.reportCheck(lastCheckResult);
        return lastCheckResult.getStatus().isSuccessful();
    }

    public ExecutedCompositeCheck getLastCheckResult() {
        return lastCheckResult;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (mismatchDescription != null) {
            description.appendText(mismatchDescription);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
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
