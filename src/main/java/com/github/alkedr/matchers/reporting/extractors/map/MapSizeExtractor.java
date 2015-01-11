package com.github.alkedr.matchers.reporting.extractors.map;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapSizeExtractor implements ValueExtractor<Map<Object, Object>, Integer> {
    @NotNull private final String name;

    public MapSizeExtractor(@NotNull String name) {
        this.name = name;
    }

    @Override
    public ExtractedValue extractValue(Map<Object, Object> item) {
        return new ExtractedValue(name, item.size());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    public static MapSizeExtractor mapSize() {
        return mapSize("|size|");
    }

    public static MapSizeExtractor mapSize(@NotNull String name) {
        return new MapSizeExtractor(name);
    }
}
