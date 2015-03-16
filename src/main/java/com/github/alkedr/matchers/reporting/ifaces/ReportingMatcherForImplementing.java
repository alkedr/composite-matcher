package com.github.alkedr.matchers.reporting.ifaces;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

// Эти интерфейсы интересны только разработчикам матчеров и генераторов отчётов
public interface ReportingMatcherForImplementing<T, U extends ReportingMatcherForImplementing<T, U>> extends Matcher<T> {
    // TODO: move to ValueExtractingMatcher
    @NotNull Class<?> getActualItemClass();
    U uncheckedExtractor(UncheckedValuesExtractor newUncheckedValuesExtractor);

    @NotNull CompositeCheck getReport(@Nullable Object item);
    Check.Status addChecksTo(@NotNull Checker storage);


    interface UncheckedValuesExtractor {
        void addUncheckedValuesTo(@NotNull CompositeCheck checker);
    }


    interface Check {
        @NotNull Status getStatus();

        enum Status {
            UNCHECKED, // проверок не было
            PASSED,    // проверки были, все успешны
            FAILED,    // проверки были, хотя бы одна неуспешна
        }
    }

    interface SimpleCheck extends Check {
        @NotNull String getMatcherDescription();
        @Nullable String getMismatchDescription();
    }

    interface CompositeCheck extends Check {
        @Nullable String getName();
        @Nullable Object getValue();
        @NotNull ExtractionStatus getExtractionStatus();
        @Nullable Exception getExtractionException();
        @NotNull Collection<? extends Check> getChecks();

        enum ExtractionStatus {
            NORMAL,
            MISSING,
            UNEXPECTED,
//            BROKEN,  // TODO: удаилть?
        }
    }


    // 1. Не объединять проверки. Пользоваться матчерами будет неудобно.
    // 2. Объединять по названию. Если у двух проверок разных значений будет одинаковое название, то мы об этом не узнаем, всё будет просто работать неправильно.
    // 3. Объединять по методу извлечения (централизованно). Список видов значений, которые мы можем извлечь, зашит в архитектуру
    //    (Это плохо, потому что нет минимального примитивного набора видов значений, на основе которых могут быть сделаны остальные, см. MapMatcher, contains + get).
    // 4. Объединять по методу извлечения (децентрализованно). Конкретные матчеры умеют объединять свои и только свои проверки. Проверки от несвязанных между собой матчеров не объединяются.
    // 5. ОБЪЕДИНЯТЬ ТОЛЬКО PlannedCheck!


    // Что будет если один матчер пометит поле как UNEXPECTED, а второй - нет?
    // "should be missing" и "should be present" отображаются как простые матчеры
    // При наведении мыши на простой матчер в отчёте появляется подсказка с description'ом матчера, который его добавил
    // Можно ещё выделить их разным цветом

    // У МАПЫ ЭТО НЕ ПРОСТО МЕТОД ГЕТ, ЭТО contains() + get()
    // Плевать на объединение разнородных матчеров (MapMatcher + ObjectMatcher)
    // Объединяться умеют только однородные матчеры
    // У каждого матчера есть свои реализации CompositeCheck, которые умеют сравниваться

    // Мы пытаемся имитировать проверку того, что два экстрактора значений одинаковы (всегда выдают одинаковый результат)

    interface Checker extends CompositeCheck {
//        void setName(String newName);  // для переименования в случае конфликта имён

        // Запускает матчер на getValue()
        // Если matcher является ReportingMatcher, то добавляет все его проверки
        // Если matcher не является ReportingMatcher, то добавляет одну SimpleCheck
        Status matcher(@NotNull Matcher<?> matcher);

        // Добавляет
        CompositeCheck subcheck(CompositeCheckAddingController addingController);

        // что делать со значениями с одинаковыми именами?
        // переименовывать! "value (1)", "value (2)" и т. д.
    }

    // Этот интерфейс реализован в конкретных матчерах для каждого вида извлекаемых значений
    interface CompositeCheckAddingController {
        // Для поиска уже существующего экземпляра CompositeCheck
        boolean isCheckerForTheSameValue(Checker check);

        // Извлекает значение, создаёт Checker, проверки не выполняет
        Checker create(Object valueToExtractFrom);

        // Запускает проверки, сохраняет результаты в checker
        void runChecks(Checker checker);
    }
}
