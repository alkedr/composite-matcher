package alkedr.matchers.reporting.matchers;

import alkedr.matchers.reporting.checks.ExtractedValue;

import java.util.List;

public interface ValuesExtractor<T> {
    List<ExtractedValue> extractValues(T item);

    // Эти штуки нужны для того, чтобы одинаковые ValuesExtractor'ы объединялись и запускались только один раз
    boolean equals(Object obj);
    int hashCode();

    // эта штука нужна для случаев, когда экстрактор вернул null или бросил исключение
    String toString();
}
