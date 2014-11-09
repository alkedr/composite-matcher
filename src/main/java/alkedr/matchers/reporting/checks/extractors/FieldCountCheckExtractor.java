package alkedr.matchers.reporting.checks.extractors;

import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.select;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.asList;

public class FieldCountCheckExtractor<T> implements PlannedCheckExtractor<T, Integer> {
    @NotNull private final Matcher<? super Integer> valueMatcher;

    public FieldCountCheckExtractor(@NotNull Matcher<? super Integer> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    public List<PlannedCheck<Integer>> extractChecks(T actual) {
        return asList(new PlannedCheck<>("fields count", getNonStaticFieldsCount(actual), valueMatcher));
    }

    private int getNonStaticFieldsCount(T actual) {
        int result = 0;
        for (Field field : actual.getClass().getFields()) {
            if (!isStatic(field.getModifiers())) {
                result++;
            }
        }
        return result;
    }
}
