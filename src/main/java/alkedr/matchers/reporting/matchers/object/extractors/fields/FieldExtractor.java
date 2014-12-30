package alkedr.matchers.reporting.matchers.object.extractors.fields;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;

public class FieldExtractor<T, U> implements ValueExtractor<T> {
    private final Class<? super T> tClass;
    private final String nameForReport;
    private final String nameForValueExtraction;

    public FieldExtractor(Class<? super T> tClass, String nameForReport, String nameForValueExtraction) {
        this.tClass = tClass;
        this.nameForReport = nameForReport;
        this.nameForValueExtraction = nameForValueExtraction;
    }

    @Override
    public ExtractedValue extractValue(T item) {
        try {
            Field field = getField(tClass, nameForValueExtraction, true);
            field.setAccessible(true);
            return new ExtractedValue(nameForReport, field.get(item));
        } catch (IllegalAccessException ignored) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
        }
    }
}
