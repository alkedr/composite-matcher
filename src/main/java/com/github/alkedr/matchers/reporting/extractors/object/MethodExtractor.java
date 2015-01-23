package com.github.alkedr.matchers.reporting.extractors.object;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public class MethodExtractor<FromType, ReturnValueType> implements ValueExtractor<FromType, ReturnValueType> {
    private final String nameForReport;
    private final Method method;

    public MethodExtractor(String nameForReport, Method method) {
        this.nameForReport = nameForReport;
        this.method = method;
    }

    @Override
    public ExtractedValue extractValue(@Nullable FromType item) {
        try {
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, method.invoke(item));
        } catch (Exception exception) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, exception);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
