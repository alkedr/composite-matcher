package com.github.alkedr.matchers.reporting.extractors.map;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

public class ValueFromMapExtractor<Key, Value> implements ValueExtractor<Map<Key, ? super Value>, Value> {
    private final String name;
    private final Key key;

    public ValueFromMapExtractor(String name, Key key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public ExtractedValue extractValue(Map<Key, ? super Value> item) {
        return item.containsKey(key)
                ? new ExtractedValue(name, item.get(key))
                : new ExtractedValue(name, null, ExtractedValue.Status.MISSING);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    public static <Key, Value> ValueFromMapExtractor<Key, Value> valueOfKey(Key key) {
        return new ValueFromMapExtractor<>(String.valueOf(key), key);
    }

    public static <Key, Value> ValueFromMapExtractor<Key, Value> valueOfKey(String name, Key key) {
        return new ValueFromMapExtractor<>(name, key);
    }


    public static <Key, Value> ValueFromMapExtractor<Key, Value> valueOfKey(Key key, Class<Value> valueClass) {
        return new ValueFromMapExtractor<>(String.valueOf(key), key);
    }

    public static <Key, Value> ValueFromMapExtractor<Key, Value> valueOfKey(String name, Key key, Class<Value> valueClass) {
        return new ValueFromMapExtractor<>(name, key);
    }
}
