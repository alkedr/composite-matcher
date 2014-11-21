package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutableCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

public abstract class CompositeMatcher<T> extends ReportingMatcher<T> {
    private final Collection<ExecutableCheck> executableChecks = new ArrayList<>();

    @NotNull
    @Override
    protected Collection<ExecutableCheck> getChecksFor(@Nullable Object actualValue) {
        return unmodifiableCollection(executableChecks);
    }

    protected abstract void check(@Nullable T actualValue);

    protected <U> void checkThat(String name, U value, Matcher<? super U> matcher) {
        executableChecks.add(new ExecutableCheck(name, value, asList(matcher)));
    }

    protected <U> void checkThat(U value, Matcher<? super U> matcher) {
        executableChecks.add(new ExecutableCheck("", value, asList(matcher)));  // TODO: capture name
    }
}
