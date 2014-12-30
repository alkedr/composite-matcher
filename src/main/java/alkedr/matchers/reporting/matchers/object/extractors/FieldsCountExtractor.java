package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isStatic;

public class FieldsCountExtractor<T> implements ValueExtractor<T> {
    private final Class<? super T> tClass;
    private final String valueName;

    public FieldsCountExtractor(Class<? super T> tClass, String valueName) {
        this.tClass = tClass;
        this.valueName = valueName;
    }

    @Override
    public ExtractedValue extractValue(Object item) {
        int actualFieldsCount = 0;
        for (Field field : tClass.getFields()) {
            if (!isStatic(field.getModifiers())) {
                actualFieldsCount++;
            }
        }
        return new ExtractedValue(valueName, actualFieldsCount);
    }
}
