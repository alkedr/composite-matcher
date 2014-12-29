package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.asList;

public class FieldExtractor<T, U> implements ValuesExtractor<T> {
    private final String nameForReport;
    private final String nameForValueExtraction;

    public FieldExtractor(String nameForReport, String nameForValueExtraction) {
        this.nameForReport = nameForReport;
        this.nameForValueExtraction = nameForValueExtraction;
    }

    @Override
    public List<ExtractedValue> extractValues(T item) {
        try {
            Field field = item.getClass().getDeclaredField(nameForValueExtraction);  // TODO: проверить находятся ли поля родителей
            field.setAccessible(true);
            return asList(new ExtractedValue(nameForReport, field.get(item)));
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return asList(new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING));
        }
    }
}
