package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutableCheck;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static alkedr.matchers.reporting.checks.CheckStatus.FAILED;

public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    private final Class<T> tClass;
    private ExecutedCompositeCheck lastCheckResult = null;

    protected ReportingMatcher(Class<T> tClass) {
        this.tClass = tClass;
    }


    protected abstract Collection<ExecutableCheck> getExecutableChecks(Class<?> clazz, Object actual);


    /**
     * @return подробные результаты последнего вызова {@link ReportingMatcher#matches} или null если
     * {@link ReportingMatcher#matches} не вызывался
     */
    @Nullable
    public ExecutedCompositeCheck getLastCheckResult() {
        return lastCheckResult;
    }


    @Override
    public boolean matches(Object item) {
        lastCheckResult = executePlannedChecks(item, mergePlannedChecksWithSameNamesPreservingOrder(getExecutableChecks(tClass, item)));
        INNER_CHECK_RESULT.set(lastCheckResult);
        return lastCheckResult.getStatus() != FAILED;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        // TODO:
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }


    private static Iterable<ExecutableCheck> mergePlannedChecksWithSameNamesPreservingOrder(Iterable<ExecutableCheck> executableChecks) {
        Map<String, ExecutableCheck> index = new HashMap<>();
        Collection<ExecutableCheck> result = new ArrayList<>();
        for (ExecutableCheck check : executableChecks) {
            ExecutableCheck checkInIndex = index.get(check.getName());
            if (checkInIndex == null) {
                index.put(check.getName(), check);
                result.add(check);
            } else {
                checkInIndex.addMatchersFrom(check);
            }
        }
        return result;
    }

    private static <T> ExecutedCompositeCheck executePlannedChecks(@Nullable T item, Iterable<ExecutableCheck> plannedChecks) {
        ExecutedCompositeCheck result = new ExecutedCompositeCheck(String.valueOf(item));
        for (ExecutableCheck executableCheck : plannedChecks) {
            executePlannedCheck(executableCheck, result);
        }
        return result;
    }

    private static void executePlannedCheck(ExecutableCheck executableCheck, ExecutedCompositeCheck result) {
        if (executableCheck.getMatchers().isEmpty()) {
            result.addCompositeCheck(executableCheck.getName(), new ExecutedCompositeCheck(String.valueOf(executableCheck.getValue())));
            return;
        }
        for (Matcher<?> matcher : executableCheck.getMatchers()) {
            INNER_CHECK_RESULT.remove();
            boolean matcherResult = matcher.matches(executableCheck.getValue());
            if (INNER_CHECK_RESULT.get() == null) {
                result.addSimpleCheck(executableCheck.getName(), String.valueOf(executableCheck.getValue()),
                        new ExecutedSimpleCheck(matcherResult, matcher, executableCheck.getValue()));
            } else {
                result.addCompositeCheck(executableCheck.getName(), INNER_CHECK_RESULT.get());
            }
        }
    }


    /**
     * хранит информацию о выполнении другого ReportingMatcher'а, которое было вызвано из текущего ReportingMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего ReportingMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();
}
