package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.beans.Introspector.getBeanInfo;

public class AllPropertiesExtractor<T> implements ValuesExtractor<T, Object> {
    private final Class<T> tClass;

    public AllPropertiesExtractor(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Map<String, Object> extractValues(Object item) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                pd.getReadMethod().setAccessible(true);
                try {
                    result.put(pd.getName(), pd.getReadMethod().invoke(item));
                } catch (InvocationTargetException | IllegalAccessException ignored) {   // TODO: report extraction errors
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
