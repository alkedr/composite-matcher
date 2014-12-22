package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapValueExtractor<T, U> implements ValuesExtractor<Map<T, U>, U> {
    private final T key;

    public MapValueExtractor(T key) {
        this.key = key;
    }

    @Override
    public Map<String, U> extractValues(Map<T, U> item) {
        if (!item.containsKey(key)) {
            // TODO
        }
        Map<String, U> result = new LinkedHashMap<>();
        result.put(String.valueOf(key), item.get(key));
        return result;
    }
}
