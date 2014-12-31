package com.github.alkedr.matchers.reporting.matchers;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public interface ValueExtractorsExtractor<T> {
    List<ValueExtractor<T>> extractValueExtractors(@Nullable T item);
}
