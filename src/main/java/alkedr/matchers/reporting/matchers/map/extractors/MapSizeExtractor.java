package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;

import java.util.Map;

public class MapSizeExtractor<T, U> implements ValueExtractor<Map<T, U>> {
    private final String name;

    public MapSizeExtractor(String name) {
        this.name = name;
    }

    @Override
    public ExtractedValue extractValue(Map<T, U> item) {
        return new ExtractedValue(name, item.size());
    }
}
