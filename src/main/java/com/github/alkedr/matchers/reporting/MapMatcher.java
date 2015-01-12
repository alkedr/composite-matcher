package com.github.alkedr.matchers.reporting;

import java.util.Map;

/**
 * User: alkedr
 * Date: 12.01.2015
 */
public class MapMatcher<T, U> extends ValueExtractingMatcherImpl<Map<T, U>, MapMatcher<T, U>> {
    public MapMatcher() {
        super(Map.class);
    }
}
