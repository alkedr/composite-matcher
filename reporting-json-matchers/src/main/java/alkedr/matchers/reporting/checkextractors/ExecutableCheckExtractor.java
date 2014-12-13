package alkedr.matchers.reporting.checkextractors;

import alkedr.matchers.reporting.checks.ExecutableCheck;

import java.util.Collection;

public interface ExecutableCheckExtractor {
    /**
     * @param clazz класс actual'a, используется для нахождения имён полей если actual null
     * @param actual значение, из которого нужно экстрактить проверки
     * @return проверки
     */
    Collection<ExecutableCheck> extract(Class<?> clazz, Object actual);
}