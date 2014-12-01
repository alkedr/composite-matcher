package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.*;

public class MapMatcher<T> extends ValueExtractingMatcher<T> {
    private final Collection<ExecutableCheckExtractor> executableCheckExtractors = new ArrayList<>();

    public MapMatcher(Class<T> tClass) {
        super(tClass);
    }

    @Override
    protected Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual) {
        return unmodifiableCollection(executableCheckExtractors);
    }

}
