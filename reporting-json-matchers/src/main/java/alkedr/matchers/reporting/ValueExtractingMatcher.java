package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;
import alkedr.matchers.reporting.checks.ExecutableCheck;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ValueExtractingMatcher<T> extends CompositeMatcher<T> {
    @Override
    protected void check(@Nullable T actualValue) {
        for (ExecutableCheckExtractor extractor : getExecutableCheckExtractors(null, actualValue)) {
            checks.addAll(extractor.extract(clazz, actual));
        }
    }

    @Override
    protected Collection<ExecutableCheck> getExecutableChecks(Class<?> clazz, Object actual) {
        Collection<ExecutableCheck> checks = new ArrayList<>();
        return checks;
    }

    protected abstract Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual);
}
