package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.plan.CheckPlan;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ReportingMatcher<T> extends TypeSafeMatcher<T> {
    private ExecutedCompositeCheck lastCheckResult = null;

    /**
     * {@link alkedr.matchers.reporting.plan.CheckPlan}
     * @param actualValue значение, которое было передано в {@link org.hamcrest.Matcher#matches}
     * @return план проверок для actualValueExtractor
     */
    @NotNull
    protected abstract CheckPlan<T> checkPlanFor(@Nullable T actualValue);


    /**
     * @return подробные результаты последнего вызова {@link ReportingMatcher#matches} или null если
     * {@link ReportingMatcher#matches} для данного инстанса не вызывался
     */
    @Nullable
    public ExecutedCompositeCheck getLastCheckResult() {
        return lastCheckResult;
    }


    @Override
    protected boolean matchesSafely(T item) {
        lastCheckResult = checkPlanFor(item).executeOn(item);
        return lastCheckResult.isSuccessful();
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        // TODO:
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }
}
