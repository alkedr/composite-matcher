package com.github.alkedr.matchers.reporting.matchers.map.extractors;

import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapAllValuesExtractor<T, U> implements ValueExtractorsExtractor<Map<T, U>> {
    @Override
    public List<ValueExtractor<Map<T, U>>> extractValueExtractors(Map<T, U> item) {
        List<ValueExtractor<Map<T, U>>> result = new ArrayList<>();
        for (T key : item.keySet()) {
            result.add(new MapValueExtractor<T, U>(key));
        }
        return result;
    }
}
