package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class MapValueExtractor<T, U> implements ValuesExtractor<Map<T, U>> {
    private final T key;

    public MapValueExtractor(T key) {
        this.key = key;
    }

    @Override
    public List<ExtractedValue> extractValues(Map<T, U> item) {
        return item.containsKey(key) ? asList(new ExtractedValue(String.valueOf(key), item.get(key))) : asList(new ExtractedValue(String.valueOf(key), null, ExtractedValue.Status.MISSING));
    }
}
