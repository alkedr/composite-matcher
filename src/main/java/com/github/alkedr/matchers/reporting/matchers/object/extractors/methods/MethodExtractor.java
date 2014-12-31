package com.github.alkedr.matchers.reporting.matchers.object.extractors.methods;

import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * User: alkedr
 * Date: 30.12.2014
 */
public class MethodExtractor<T> implements ValueExtractor<T> {
    private final String nameForReport;
    private final Method method;

    public MethodExtractor(String nameForReport, Method method) {
        this.nameForReport = nameForReport;
        this.method = method;
    }

    @Override
    public ExtractedValue extractValue(@Nullable T item) {
        try {
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, method.invoke(item));
        } catch (Throwable throwable) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, throwable);
        }
    }
}
