package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;

import java.lang.reflect.Field;

public class FieldExtractor<T, U> implements ValueExtractor<T> {
    private final String nameForReport;
    private final String nameForValueExtraction;

    public FieldExtractor(String nameForReport, String nameForValueExtraction) {
        this.nameForReport = nameForReport;
        this.nameForValueExtraction = nameForValueExtraction;
    }

    @Override
    public ExtractedValue extractValue(T item) {
        try {
            Field field = item.getClass().getDeclaredField(nameForValueExtraction);  // TODO: проверить находятся ли поля родителей
            field.setAccessible(true);
            return new ExtractedValue(nameForReport, field.get(item));
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return new ExtractedValue(nameForReport, null, ExtractedValue.Status.MISSING);
        }
    }
}
