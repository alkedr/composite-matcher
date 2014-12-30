package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import ch.lambdaj.function.argument.Argument;

public class LambdajPropertyExtractor<T, U> implements ValueExtractor<T> {
    private final String nameForReport;
    private final Argument<U> argument;

    public LambdajPropertyExtractor(String nameForReport, Argument<U> argument) {
        this.nameForReport = nameForReport;
        this.argument = argument;
    }

    @Override
    public ExtractedValue extractValue(T item) {
        return new ExtractedValue(nameForReport, argument.evaluate(item));
    }
}
