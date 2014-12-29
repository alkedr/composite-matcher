package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class FieldsExtractor<T> implements ValuesExtractor<T> {
    private final Matcher<String> fieldNameMatcher;

    public FieldsExtractor(Matcher<String> fieldNameMatcher) {
        this.fieldNameMatcher = fieldNameMatcher;
    }

    @Override
    public List<ExtractedValue> extractValues(T item) {
        List<ExtractedValue> result = new ArrayList<>();
        for (Field field : item.getClass().getFields()) {
            field.setAccessible(true);
            if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                try {
                    result.add(new ExtractedValue(field.getName(), field.get(item)));
                } catch (IllegalAccessException ignored) {
                    result.add(new ExtractedValue(field.getName(), null, ExtractedValue.Status.MISSING));
                }
            }
        }
        return result;
    }
}
