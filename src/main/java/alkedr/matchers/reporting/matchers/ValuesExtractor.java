package alkedr.matchers.reporting.matchers;

import java.util.Map;

public interface ValuesExtractor<T, U> {
    Map<String, U> extractValues(T item);
}
