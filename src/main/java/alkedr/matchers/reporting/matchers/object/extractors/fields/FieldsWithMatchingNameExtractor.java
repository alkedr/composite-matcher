package alkedr.matchers.reporting.matchers.object.extractors.fields;

import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

public class FieldsWithMatchingNameExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<? super T> tClass;
    private final Matcher<Field> fieldMatcher;

    public FieldsWithMatchingNameExtractor(Class<? super T> tClass, Matcher<Field> fieldMatcher) {
        this.tClass = tClass;
        this.fieldMatcher = fieldMatcher;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(T item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (Field field : getAllFieldsList(tClass)) {
            if (!isStatic(field.getModifiers()) && fieldMatcher.matches(field)) {
                result.add(new FieldExtractor<>(tClass, field.getName(), field.getName()));
            }
        }
        return result;
    }
}
