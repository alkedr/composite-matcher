package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class MapSizeExtractor<T, U> implements ValuesExtractor<Map<T, U>> {
    private final String name;

    public MapSizeExtractor(String name) {
        this.name = name;
    }

    @Override
    public List<ExtractedValue> extractValues(Map<T, U> item) {
        return asList(new ExtractedValue(name, item.size()));
    }
}
