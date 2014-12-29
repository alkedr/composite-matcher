package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static java.beans.Introspector.getBeanInfo;

public class AllPropertiesExtractor<T> implements ValuesExtractor<T> {
    private final Class<T> tClass;

    public AllPropertiesExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ExtractedValue> extractValues(Object item) {
        List<ExtractedValue> result = new ArrayList<>();
        try {
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                pd.getReadMethod().setAccessible(true);
                try {
                    result.add(new ExtractedValue(pd.getName(), pd.getReadMethod().invoke(item)));
                } catch (InvocationTargetException | IllegalAccessException ignored) {
                    result.add(new ExtractedValue(pd.getName(), null, ExtractedValue.Status.MISSING));
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
