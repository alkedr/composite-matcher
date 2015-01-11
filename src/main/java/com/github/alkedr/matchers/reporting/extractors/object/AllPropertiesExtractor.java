package com.github.alkedr.matchers.reporting.extractors.object;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import static java.beans.Introspector.getBeanInfo;

public class AllPropertiesExtractor<FromType> implements ValueExtractorsExtractor<FromType, Object> {
    private final Class<FromType> tClass;

    public AllPropertiesExtractor(Class<FromType> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<FromType, Object>> extractValueExtractors(@Nullable FromType item) {
        List<ValueExtractor<FromType, Object>> result = new ArrayList<>();
        try {
            for (PropertyDescriptor pd : getBeanInfo(tClass).getPropertyDescriptors()) {
                result.add(new MethodExtractor<FromType, Object>(pd.getName(), pd.getReadMethod()));
            }
        } catch (final Throwable throwable) {
            result.add(new ValueExtractor<FromType, Object>() {
                @Override
                public ExtractedValue extractValue(@Nullable FromType item) {
                    return new ExtractedValue("!<properties of " + tClass.getName() + ">!", null, ExtractedValue.Status.ERROR, throwable);
                }
            });
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    public static <FromType> AllPropertiesExtractor<FromType> allProperties(Class<FromType> tClass) {
        return new AllPropertiesExtractor<>(tClass);
    }
}
