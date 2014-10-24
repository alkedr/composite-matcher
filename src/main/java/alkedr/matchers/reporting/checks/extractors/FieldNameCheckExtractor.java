package alkedr.matchers.reporting.checks.extractors;

import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.asList;

@SuppressWarnings("unchecked")
public class FieldNameCheckExtractor<T, U> implements PlannedCheckExtractor<T, U> {
    @NotNull private final String fieldNameForReport;
    @NotNull private final String fieldNameForValueExtraction;
    @NotNull private final Matcher<? super U> valueMatcher;

    public FieldNameCheckExtractor(@Nullable String fieldNameForReport, @NotNull String fieldNameForValueExtraction,
                                   @NotNull Matcher<? super U> valueMatcher) {
        this.fieldNameForReport = fieldNameForReport == null ? fieldNameForValueExtraction : fieldNameForReport;
        this.fieldNameForValueExtraction = fieldNameForValueExtraction;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public List<PlannedCheck<U>> extractChecks(T actual) {
        try {
            Field field = actual.getClass().getField(fieldNameForValueExtraction);
            field.setAccessible(true);
            return asList(new PlannedCheck<>(fieldNameForReport, (U)field.get(actual), valueMatcher));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return asList(); // FIXME: В отчёте должно отобразиться, что поле не найдено
        }
    }
}
