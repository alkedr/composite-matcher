package alkedr.matchers.reporting.matchers.map.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapAllValuesExtractor<T, U> implements ValuesExtractor<Map<T, U>> {
    @Override
    public List<ExtractedValue> extractValues(Map<T, U> item) {
        List<ExtractedValue> result = new ArrayList<>();
        for (Map.Entry<T, U> entry : item.entrySet()) {
            result.add(new ExtractedValue(String.valueOf(entry.getKey()), entry.getValue()));
        }
        return result;
    }
}
