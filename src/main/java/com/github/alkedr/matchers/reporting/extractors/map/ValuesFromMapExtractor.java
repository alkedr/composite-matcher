package com.github.alkedr.matchers.reporting.extractors.map;

import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValuesFromMapExtractor<Key, Value> implements ValueExtractorsExtractor<Map<Key, ? super Value>, Value> {
    @Nullable private final Matcher<Key> keyMatcher;

    public ValuesFromMapExtractor(@Nullable Matcher<Key> keyMatcher) {
        this.keyMatcher = keyMatcher;
    }

    @Override
    public List<ValueExtractor<Map<Key, ? super Value>, Value>> extractValueExtractors(@Nullable Map<Key, ? super Value> item) {
        List<ValueExtractor<Map<Key, ? super Value>, Value>> result = new ArrayList<>();
        for (Key key : item.keySet()) {
            if (keyMatcher == null || keyMatcher.matches(key)) {
                result.add(ValueFromMapExtractor.<Key, Value>valueOfKey(key));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    public static <Key, Value> ValuesFromMapExtractor<Key, Value> allMapValues() {
        return mapValuesForKeysThatAre(null);
    }

    public static <Key, Value> ValuesFromMapExtractor<Key, Value> mapValuesForKeysThatAre(@Nullable Matcher<Key> keyMatcher) {
        return new ValuesFromMapExtractor<>(keyMatcher);
    }
}
