package alkedr.matchers.reporting.extractors;

import alkedr.matchers.reporting.checks.ExecutableCheck;

import java.util.ArrayList;
import java.util.Collection;

public final class ExtractorsUtils {
    private ExtractorsUtils() {}

    public static <T> Collection<ExecutableCheck> extractChecks(Iterable<ExecutableCheckExtractor> extractors, Class<?> clazz, Object actual) {
        Collection<ExecutableCheck> checks = new ArrayList<>();
        for (ExecutableCheckExtractor extractor : extractors) {
            checks.addAll(extractor.extract(clazz, actual));
        }
        return checks;
    }
}
