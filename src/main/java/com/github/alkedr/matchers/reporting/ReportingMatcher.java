package com.github.alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ReportingMatcher<T> extends Matcher<T> {
    ExecutedCompositeCheck getReport(@Nullable Object item);


    interface ExecutedCheck {
        @NotNull Status getStatus();

        enum Status {
            UNCHECKED,
            PASSED,
            FAILED,
            ;
        }
    }

    /**
     * Хранит информацию о запуске обычного Matcher'а
     */
    interface ExecutedSimpleCheck extends ExecutedCheck {
        @NotNull String getMatcherDescription();
        @Nullable String getMismatchDescription();
    }

    /**
     * Хранит информацию о запуске {@link ReportingMatcher}'а
     */
    interface ExecutedCompositeCheck extends ExecutedCheck {
        @Nullable String getName();
        @Nullable Object getValue();
        @NotNull ExtractionStatus getExtractionStatus();
        @Nullable Exception getExtractionException();

        /**
         * @return результаты запуска матчеров на проверяемом значении
         */
        @NotNull List<ExecutedSimpleCheck> getSimpleChecks();

        /**
         * @return результаты запуска матчеров на значениях, которые были извлечены из проверяемого, например,
         * если проверяемое значение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
         */
        @NotNull List<ExecutedCompositeCheck> getCompositeChecks();
    }


    enum ExtractionStatus {
        NORMAL,
        MISSING,
        UNEXPECTED,
        ERROR,
        ;
    }
}
