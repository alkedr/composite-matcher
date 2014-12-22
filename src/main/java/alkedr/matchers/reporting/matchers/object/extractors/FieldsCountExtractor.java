package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;

public class FieldsCountExtractor<T> implements ValuesExtractor<T, Integer> {
    private final Class<T> tClass;
    private final String valueName;

    public FieldsCountExtractor(Class<T> tClass, String valueName) {
        this.tClass = tClass;
        this.valueName = valueName;
    }

    @Override
    public Map<String, Integer> extractValues(Object item) {
        int actualFieldsCount = 0;
        for (Field field : tClass.getFields()) {
            if (!isStatic(field.getModifiers())) {
                actualFieldsCount++;
            }
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put(valueName, actualFieldsCount);
        return result;
    }
}
