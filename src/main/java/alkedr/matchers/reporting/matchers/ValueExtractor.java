package alkedr.matchers.reporting.matchers;

import alkedr.matchers.reporting.checks.ExtractedValue;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public interface ValueExtractor<T> {
    ExtractedValue extractValue(T item);

    // Эти штуки нужны для того, чтобы одинаковые ValueExtractor'ы объединялись и запускались только один раз
    boolean equals(Object obj);
    int hashCode();
}
