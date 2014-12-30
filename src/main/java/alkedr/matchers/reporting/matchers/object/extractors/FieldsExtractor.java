package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class FieldsExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Matcher<String> fieldNameMatcher;

    public FieldsExtractor(Matcher<String> fieldNameMatcher) {
        this.fieldNameMatcher = fieldNameMatcher;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(T item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (Field field : item.getClass().getFields()) {
            field.setAccessible(true);
            if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                result.add(new FieldExtractor<T, Object>(field.getName(), field.getName()));
            }
        }
        return result;
    }
}
