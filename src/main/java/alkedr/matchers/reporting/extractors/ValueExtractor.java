package alkedr.matchers.reporting.extractors;

import alkedr.matchers.reporting.checks.ExecutableCheck;

import java.util.Collection;

public interface ValueExtractor {
    Object extract(Class<?> clazz, Object actual);
}
