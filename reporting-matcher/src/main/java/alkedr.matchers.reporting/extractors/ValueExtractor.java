package alkedr.matchers.reporting.extractors;

import alkedr.matchers.reporting.checks.ExecutableCheck;

public interface ValueExtractor {
    Object extract(Class<?> clazz, Object actual);
}
