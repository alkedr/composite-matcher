package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class AllFieldsExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<T> tClass;

    public AllFieldsExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(T item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (Field field : tClass.getDeclaredFields()) {
            if (!isStatic(field.getModifiers())) {
                field.setAccessible(true);
                result.add(new FieldExtractor<T, Object>(field.getName(), field.getName()));
            }
        }
        return result;
    }
}
