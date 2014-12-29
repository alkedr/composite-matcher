package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;

public class FieldsCountExtractor<T> implements ValuesExtractor<T> {
    private final Class<T> tClass;
    private final String valueName;

    public FieldsCountExtractor(Class<T> tClass, String valueName) {
        this.tClass = tClass;
        this.valueName = valueName;
    }

    @Override
    public List<ExtractedValue> extractValues(Object item) {
        int actualFieldsCount = 0;
        for (Field field : tClass.getFields()) {
            if (!isStatic(field.getModifiers())) {
                actualFieldsCount++;
            }
        }
        return asList(new ExtractedValue(valueName, actualFieldsCount));
    }
}
