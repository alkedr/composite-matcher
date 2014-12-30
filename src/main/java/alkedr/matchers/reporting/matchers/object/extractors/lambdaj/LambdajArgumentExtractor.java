package alkedr.matchers.reporting.matchers.object.extractors.lambdaj;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import ch.lambdaj.function.argument.Argument;
import org.jetbrains.annotations.Nullable;

public class LambdajArgumentExtractor<T, U> implements ValueExtractor<T> {
    private final String nameForReport;
    private final Argument<U> argument;

    public LambdajArgumentExtractor(String nameForReport, Argument<U> argument) {
        this.nameForReport = nameForReport;
        this.argument = argument;
    }

    @Override
    public ExtractedValue extractValue(@Nullable T item) {
        try {
            if (item == null) return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
            return new ExtractedValue(nameForReport, argument.evaluate(item));
        } catch (Throwable throwable) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.ERROR, throwable);
        }
    }
}
