package alkedr.matchers.reporting.matchers;

import java.util.List;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public interface ValueExtractorsExtractor<T> {
    List<ValueExtractor<T>> extractValueExtractors(T item);
}
