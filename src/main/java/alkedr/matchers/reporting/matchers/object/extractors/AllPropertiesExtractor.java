package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static java.beans.Introspector.getBeanInfo;

public class AllPropertiesExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<T> tClass;

    public AllPropertiesExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(Object item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        try {
            for (final PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                pd.getReadMethod().setAccessible(true);
                result.add(new ValueExtractor<T>() {
                    @Override
                    public ExtractedValue extractValue(T item) {
                        try {
                            return new ExtractedValue(pd.getName(), pd.getReadMethod().invoke(item));
                        } catch (InvocationTargetException | IllegalAccessException ignored) {
                            return new ExtractedValue(pd.getName(), null, ExtractedValue.Status.MISSING);
                        }
                    }
                });
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
