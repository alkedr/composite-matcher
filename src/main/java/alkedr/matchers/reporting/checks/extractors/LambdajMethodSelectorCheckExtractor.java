package alkedr.matchers.reporting.checks.extractors;

import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;

import java.util.List;

import static ch.lambdaj.function.argument.ArgumentsFactory.actualArgument;
import static java.util.Arrays.asList;

public class LambdajMethodSelectorCheckExtractor<T, U> implements PlannedCheckExtractor<T, U> {
    private final String fieldNameForReport;
    private final U lambdajGetterMethodSelector;
    private final Matcher<? super U> matcher;

    public LambdajMethodSelectorCheckExtractor(String fieldNameForReport, U lambdajGetterMethodSelector, Matcher<? super U> matcher) {
        this.fieldNameForReport = fieldNameForReport;
        this.lambdajGetterMethodSelector = lambdajGetterMethodSelector;
        this.matcher = matcher;
    }

    @Override
    public List<PlannedCheck<U>> extractChecks(T actual) {
        Argument<U> argument = actualArgument(lambdajGetterMethodSelector);
        return asList(new PlannedCheck<>(fieldNameForReport, argument.evaluate(actual), matcher));
    }
}
