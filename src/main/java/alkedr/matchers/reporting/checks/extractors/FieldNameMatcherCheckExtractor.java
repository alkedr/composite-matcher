package alkedr.matchers.reporting.checks.extractors;

import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class FieldNameMatcherCheckExtractor<T> implements PlannedCheckExtractor<T, Object> {
    private final Matcher<String> fieldNameMatcher;
    private final Matcher<? super Object> valueMatcher;

    public FieldNameMatcherCheckExtractor(Matcher<String> fieldNameMatcher, Matcher<? super Object> valueMatcher) {
        this.fieldNameMatcher = fieldNameMatcher;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public List<PlannedCheck<Object>> extractChecks(T actual) {
        List<PlannedCheck<Object>> result = new ArrayList<>();
        for (Field field : actual.getClass().getFields()) {
            field.setAccessible(true);
            if (!isStatic(field.getModifiers()) && fieldNameMatcher.matches(field.getName())) {
                try {
                    result.add(new PlannedCheck<>(field.getName(), field.get(actual), valueMatcher));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return result;
    }
}
