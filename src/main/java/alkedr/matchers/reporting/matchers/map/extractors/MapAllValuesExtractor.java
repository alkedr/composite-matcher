package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapAllValuesExtractor<T, U> implements ValuesExtractor<Map<T, U>, U> {
    @Override
    public Map<String, U> extractValues(Map<T, U> item) {
        Map<String, U> result = new LinkedHashMap<>();
        for (Map.Entry<T, U> entry : item.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }
}
