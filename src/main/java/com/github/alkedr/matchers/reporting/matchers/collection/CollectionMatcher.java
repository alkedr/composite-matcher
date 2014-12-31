package com.github.alkedr.matchers.reporting.matchers.collection;

import com.github.alkedr.matchers.reporting.TypeSafeReportingMatcher;
import com.github.alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * User: alkedr
 * Date: 22.12.2014
 */
public class CollectionMatcher<T> extends TypeSafeReportingMatcher<Collection<T>> {

    public CollectionMatcher() {
        super(Collection.class);
    }

    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable Collection<T> item) {
        return null;
    }
}
