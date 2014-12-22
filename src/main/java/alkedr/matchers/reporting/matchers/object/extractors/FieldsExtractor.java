package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;

public class FieldsExtractor<T> implements ValuesExtractor<T, Object> {
    private final Matcher<String> fieldNameMatcher;

    public FieldsExtractor(Matcher<String> fieldNameMatcher) {
        this.fieldNameMatcher = fieldNameMatcher;
    }

    @Override
    public Map<String, Object> extractValues(T item) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Field field : item.getClass().getFields()) {
            field.setAccessible(true);
            if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                try {
                    result.put(field.getName(), field.get(item));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return result;
    }
}
