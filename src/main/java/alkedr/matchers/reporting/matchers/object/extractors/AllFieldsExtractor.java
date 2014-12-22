package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;

public class AllFieldsExtractor<T> implements ValuesExtractor<T, Object> {
    private final Class<T> tClass;

    public AllFieldsExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Map<String, Object> extractValues(T item) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Field field : tClass.getDeclaredFields()) {
            if (!isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    result.put(field.getName(), field.get(item));
                } catch (IllegalAccessException ignored) {   // TODO: report extraction errors
                }
            }
        }
        return result;
    }
}
