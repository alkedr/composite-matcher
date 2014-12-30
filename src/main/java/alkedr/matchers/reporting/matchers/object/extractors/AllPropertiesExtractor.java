package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                pd.getReadMethod().setAccessible(true);
                // TODO
//                try {
//                    result.add(new ExtractedValue(pd.getName(), pd.getReadMethod().invoke(item)));
//                } catch (InvocationTargetException | IllegalAccessException ignored) {
//                    result.add(new ExtractedValue(pd.getName(), null, ExtractedValue.Status.MISSING));
//                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
