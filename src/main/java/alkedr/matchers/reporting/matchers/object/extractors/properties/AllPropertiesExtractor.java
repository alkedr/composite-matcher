package alkedr.matchers.reporting.matchers.object.extractors.properties;

import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.methods.MethodExtractor;
import org.jetbrains.annotations.Nullable;

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
    public List<ValueExtractor<T>> extractValueExtractors(@Nullable T item) {
        try {
            List<ValueExtractor<T>> result = new ArrayList<>();
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                result.add(new MethodExtractor<T>(pd.getName(), pd.getReadMethod()));
            }
            return result;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
