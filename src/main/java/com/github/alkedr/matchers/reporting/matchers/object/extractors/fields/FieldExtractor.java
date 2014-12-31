package com.github.alkedr.matchers.reporting.matchers.object.extractors.fields;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;

public class FieldExtractor<T> implements ValueExtractor<T> {
    private final String nameForReport;
    private final Field field;

    public FieldExtractor(String nameForReport, Field field) {
        this.nameForReport = nameForReport;
        this.field = field;
    }

    public FieldExtractor(Class<? super T> tClass, String nameForReport, String nameForValueExtraction) {
        this(nameForReport, getField(tClass, nameForValueExtraction, true));
    }

    @Override
    public ExtractedValue extractValue(@Nullable T item) {
        try {
            field.setAccessible(true);
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, field.get(item));
        } catch (Throwable throwable) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, throwable);
        }
    }
}
