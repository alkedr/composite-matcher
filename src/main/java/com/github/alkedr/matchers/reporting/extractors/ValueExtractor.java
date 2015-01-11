package com.github.alkedr.matchers.reporting.extractors;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import org.jetbrains.annotations.Nullable;

/**
 * User: alkedr
 * Date: 30.12.2014
 *
 * TODO: AbstractValueExtractor that handles nulls and catches all exceptions
 *
 * TODO: TypedValueExtractor
 */
public interface ValueExtractor<T, U> {
    ExtractedValue extractValue(@Nullable T item);

    // Эти штуки нужны для того, чтобы одинаковые ValueExtractor'ы объединялись и запускались только один раз
    boolean equals(Object obj);
    int hashCode();
}
