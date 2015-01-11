package com.github.alkedr.matchers.reporting.extractors.object;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.argument.Argument;
import com.github.alkedr.matchers.reporting.checks.ExtractedValue;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

// TODO: support chains  ( on(X.class).getA().getB().getC() )
public class LambdajArgumentExtractor<FromType, ReturnValueType> implements ValueExtractor<FromType, ReturnValueType> {
    private final String nameForReport;
    private final Argument<ReturnValueType> argument;

    public LambdajArgumentExtractor(String nameForReport, Argument<ReturnValueType> argument) {
        this.nameForReport = nameForReport;
        this.argument = argument;
    }

    @Override
    public ExtractedValue extractValue(@Nullable FromType item) {
        try {
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, argument.evaluate(item));
        } catch (Throwable throwable) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, throwable);
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


    public static <T, U> LambdajArgumentExtractor<T, U> resultOf(U arg) {
        return resultOf(Lambda.argument(arg));
    }

    public static <T, U> LambdajArgumentExtractor<T, U> resultOf(String nameForReport, U arg) {
        return resultOf(nameForReport, Lambda.argument(arg));
    }

    public static <T, U> LambdajArgumentExtractor<T, U> resultOf(Argument<U> argument) {
        return resultOf(argument.getInkvokedPropertyName(), argument);
    }

    public static <T, U> LambdajArgumentExtractor<T, U> resultOf(String nameForReport, Argument<U> argument) {
        return new LambdajArgumentExtractor<>(nameForReport, argument);
    }
}
