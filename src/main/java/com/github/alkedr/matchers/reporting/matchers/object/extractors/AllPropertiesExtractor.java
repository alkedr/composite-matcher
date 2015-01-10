package com.github.alkedr.matchers.reporting.matchers.object.extractors;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import org.jetbrains.annotations.Nullable;

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
        List<ValueExtractor<T>> result = new ArrayList<>();
        try {
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                result.add(new MethodExtractor<T>(pd.getName(), pd.getReadMethod()));
            }
        } catch (final Throwable throwable) {
            result.add(new ValueExtractor<T>() {
                @Override
                public ExtractedValue extractValue(@Nullable T item) {
                    return new ExtractedValue("!<properties of " + tClass.getName() + ">!", null, ExtractedValue.Status.ERROR, throwable);
                }
            });
        }
        return result;
    }
}
