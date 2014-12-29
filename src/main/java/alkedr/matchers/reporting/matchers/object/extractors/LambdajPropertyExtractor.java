package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;
import ch.lambdaj.function.argument.Argument;

import java.util.List;

import static java.util.Arrays.asList;

public class LambdajPropertyExtractor<T, U> implements ValuesExtractor<T> {
    private final String nameForReport;
    private final Argument<U> argument;

    public LambdajPropertyExtractor(String nameForReport, Argument<U> argument) {
        this.nameForReport = nameForReport;
        this.argument = argument;
    }

    @Override
    public List<ExtractedValue> extractValues(T item) {
        return asList(new ExtractedValue(nameForReport, argument.evaluate(item)));
    }
}
