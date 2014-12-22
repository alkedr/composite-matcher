package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSizeExtractor<T, U> implements ValuesExtractor<Map<T, U>, Integer> {
    private final String name;

    public MapSizeExtractor(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Integer> extractValues(Map<T, U> item) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put(name, item.size());
        return result;
    }
}
