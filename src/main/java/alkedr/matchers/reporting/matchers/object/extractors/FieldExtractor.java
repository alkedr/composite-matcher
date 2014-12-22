package alkedr.matchers.reporting.matchers.object.extractors;

import alkedr.matchers.reporting.matchers.ValuesExtractor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldExtractor<T, U> implements ValuesExtractor<T, U> {
    private final String nameForReport;
    private final String nameForValueExtraction;

    public FieldExtractor(String nameForReport, String nameForValueExtraction) {
        this.nameForReport = nameForReport;
        this.nameForValueExtraction = nameForValueExtraction;
    }

    @Override
    public Map<String, U> extractValues(T item) {
        Map<String, U> result = new HashMap<>();
        try {
            Field field = item.getClass().getDeclaredField(nameForValueExtraction);  // TODO: проверить находятся ли поля родителей
            field.setAccessible(true);
            result.put(nameForReport, (U)field.get(item));  // TODO: safe cast
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // FIXME: В отчёте должно отобразиться, что поле не найдено
            throw new RuntimeException(e);
        }
        return result;
    }
}
