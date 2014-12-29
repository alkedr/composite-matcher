package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class AllFieldsExtractor<T> implements ValuesExtractor<T> {
    private final Class<T> tClass;

    public AllFieldsExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ExtractedValue> extractValues(T item) {
        List<ExtractedValue> result = new ArrayList<>();
        for (Field field : tClass.getDeclaredFields()) {
            if (!isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    result.add(new ExtractedValue(field.getName(), field.get(item)));
                } catch (IllegalAccessException ignored) {   // TODO: report extraction errors
                    result.add(new ExtractedValue(field.getName(), null, ExtractedValue.Status.MISSING));
                }
            }
        }
        return result;
    }
}
