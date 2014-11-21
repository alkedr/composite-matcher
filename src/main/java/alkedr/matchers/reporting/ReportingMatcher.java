package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutableCheck;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: null'ы! Если actualValue null, то мы хотим отобразить в отчёте всё, что мы ожидали увидеть, всё дерево!
public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    private ExecutedCompositeCheck lastCheckResult = null;

    /**
     * @param actualValue значение, которое было передано в {@link org.hamcrest.Matcher#matches}
     * @return проверки для actualValueExtractor
     */
    @NotNull
    protected abstract Collection<ExecutableCheck> getChecksFor(@Nullable Object actualValue);


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
        lastCheckResult = executePlannedChecks(item, mergePlannedChecksWithSameNamesPreservingOrder(getChecksFor(item)));
        INNER_CHECK_RESULT.set(lastCheckResult);
        return lastCheckResult.isSuccessful();
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
        for (Matcher<?> matcher : executableCheck.getMatchers()) {
            INNER_CHECK_RESULT.remove();
            boolean matcherResult = matcher.matches(executableCheck.getValue());
            if (INNER_CHECK_RESULT.get() == null) {
                // TODO: create CompositeCheck with name executableCheck.getName() if not exists and add simple check there
                result.addSimpleCheck(executableCheck.getName(), String.valueOf(executableCheck.getValue()), new ExecutedSimpleCheck(matcherResult, matcher, executableCheck.getValue()));
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
