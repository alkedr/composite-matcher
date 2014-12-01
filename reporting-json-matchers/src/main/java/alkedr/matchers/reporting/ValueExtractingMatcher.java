package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;
import alkedr.matchers.reporting.checks.ExecutableCheck;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ValueExtractingMatcher<T> extends ReportingMatcher<T> {
    protected ValueExtractingMatcher(Class<T> tClass) {
        super(tClass);
    }

    @Override
    protected Collection<ExecutableCheck> getExecutableChecks(Class<?> clazz, Object actual) {
        Collection<ExecutableCheck> checks = new ArrayList<>();
        for (ExecutableCheckExtractor extractor : getExecutableCheckExtractors(clazz, actual)) {
            checks.addAll(extractor.extract(clazz, actual));
        }
        return checks;
    }

    protected abstract Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual);
}
