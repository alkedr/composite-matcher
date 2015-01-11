package com.github.alkedr.matchers.reporting.extractors.object;

import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

public class FieldsExtractor<FromType> implements ValueExtractorsExtractor<FromType, Object> {
    private final Class<? super FromType> tClass;
    private final Matcher<Field> fieldMatcher;

    public FieldsExtractor(Class<? super FromType> tClass, Matcher<Field> fieldMatcher) {
        this.tClass = tClass;
        this.fieldMatcher = fieldMatcher;
    }

    @Override
    public List<ValueExtractor<FromType, Object>> extractValueExtractors(@Nullable FromType item) {
        List<ValueExtractor<FromType, Object>> result = new ArrayList<>();
        for (Field field : getAllFieldsList(tClass)) {
            if (!isStatic(field.getModifiers()) && fieldMatcher.matches(field)) {
                result.add(new FieldExtractor<FromType, Object>(field.getName(), field));
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
}
