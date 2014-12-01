package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutableCheck;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

public abstract class CompositeMatcher<T> extends ReportingMatcher<T> {
    private final Collection<ExecutableCheck> executableChecks = new ArrayList<>();

    protected CompositeMatcher(Class<T> tClass) {
        super(tClass);
    }

    @NotNull
    @Override
    protected Collection<ExecutableCheck> getExecutableChecks(Class<?> clazz, Object actual) {
        return unmodifiableCollection(executableChecks);
    }

    protected abstract void check(@Nullable T actualValue);

    protected <U> void checkThat(String name, U value, Matcher<? super U> matcher) {
        executableChecks.add(new ExecutableCheck(name, value, asList(matcher)));
    }

    protected <U> void checkThat(U value, Matcher<? super U> matcher) {
        executableChecks.add(new ExecutableCheck("", value, asList(matcher)));  // TODO: capture name
    }

    protected <U> void addCheckResult(ExecutedSimpleCheck check) {
    }

    protected <U> void addCheckResult(ExecutedCompositeCheck check) {
    }

    protected <U> void addCheckResult(String name, ExecutedSimpleCheck check) {
    }

    protected <U> void addCheckResult(String name, ExecutedCompositeCheck check) {
    }
}
