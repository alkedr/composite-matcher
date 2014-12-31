package com.github.alkedr.matchers.reporting.matchers.map.extractors;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;

import java.util.Map;

public class MapValueExtractor<T, U> implements ValueExtractor<Map<T, U>> {
    private final T key;

    public MapValueExtractor(T key) {
        this.key = key;
    }

    @Override
    public ExtractedValue extractValue(Map<T, U> item) {
        return item.containsKey(key) ? new ExtractedValue(String.valueOf(key), item.get(key)) : new ExtractedValue(String.valueOf(key), null, ExtractedValue.Status.MISSING);
    }
}
