package com.github.alkedr.matchers.reporting.extractors.object;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;

public class FieldExtractor<FromType, FieldType> implements ValueExtractor<FromType, FieldType> {
    private final String nameForReport;
    private final Field field;

    public FieldExtractor(String nameForReport, Field field) {
        this.nameForReport = nameForReport;
        this.field = field;
    }

    public FieldExtractor(Class<? super FromType> tClass, String nameForReport, String nameForValueExtraction) {
        this(nameForReport, getField(tClass, nameForValueExtraction, true));
    }

    @Override
    public ExtractedValue extractValue(@Nullable FromType item) {
        try {
            field.setAccessible(true);
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, field.get(item));
        } catch (Throwable throwable) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, throwable);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    public static <FromType, FieldType> FieldExtractor<FromType, FieldType> field(Class<? super FromType> tClass, String nameForReportAndValueExtraction) {
        return field(tClass, nameForReportAndValueExtraction, nameForReportAndValueExtraction);
    }

    private static <FromType, FieldType> FieldExtractor<FromType, FieldType> field(Class<? super FromType> tClass, String nameForReport, String nameForValueExtraction) {
        return new FieldExtractor<>(tClass, nameForReport, nameForValueExtraction);
    }
}
