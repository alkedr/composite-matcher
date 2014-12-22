package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;
import ch.lambdaj.function.argument.Argument;

import java.util.LinkedHashMap;
import java.util.Map;

public class LambdajPropertyExtractor<T, U> implements ValuesExtractor<T, U> {
    private final String nameForReport;
    private final Argument<U> argument;

    public LambdajPropertyExtractor(String nameForReport, Argument<U> argument) {
        this.nameForReport = nameForReport;
        this.argument = argument;
    }

    @Override
    public Map<String, U> extractValues(T item) {
        Map<String, U> result = new LinkedHashMap<>();
        result.put(nameForReport, argument.evaluate(item));
        return result;
    }
}
