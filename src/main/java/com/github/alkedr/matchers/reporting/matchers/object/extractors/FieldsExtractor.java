package com.github.alkedr.matchers.reporting.matchers.object.extractors;

import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

public class FieldsExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<? super T> tClass;
    private final Matcher<Field> fieldMatcher;

    public FieldsExtractor(Class<? super T> tClass, Matcher<Field> fieldMatcher) {
        this.tClass = tClass;
        this.fieldMatcher = fieldMatcher;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(@Nullable T item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (Field field : getAllFieldsList(tClass)) {
            if (!isStatic(field.getModifiers()) && fieldMatcher.matches(field)) {
                result.add(new FieldExtractor<T>(field.getName(), field));
            }
        }
        return result;
    }
}
