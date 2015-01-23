package com.github.alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Хранит информацию о запуске {@link com.github.alkedr.matchers.reporting.ReportingMatcher}'а
 */
public class ExecutedCompositeCheck implements ExecutedCheck {
    @Nullable private final String name;
    @Nullable private final Object value;
    @NotNull private final ExtractedValue.Status extractionStatus;
    @Nullable private final Exception extractionException;  // TODO: embed in status?

    @NotNull private final Status status;
    @Nullable private final List<ExecutedSimpleCheck> simpleChecks;
    @Nullable private final List<ExecutedCompositeCheck> compositeChecks;


    public ExecutedCompositeCheck(@NotNull ExtractedValue extractedValue, @NotNull Status status,
                                  @Nullable List<ExecutedSimpleCheck> simpleChecks,
                                  @Nullable List<ExecutedCompositeCheck> compositeChecks) {
        this.name = extractedValue.getName();
        this.value = extractedValue.getValue();
        this.extractionStatus = extractedValue.getStatus();
        this.extractionException = extractedValue.getException();
        this.status = status;
        this.simpleChecks = simpleChecks;
        this.compositeChecks = compositeChecks;
    }


    @Override
    @NotNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @NotNull
    public ExtractedValue.Status getExtractionStatus() {
        return extractionStatus;
    }

    @Nullable
    public Exception getExtractionException() {
        return extractionException;
    }

    /**
     * @return результаты запуска матчеров на проверяемом значении
     */
    @NotNull
    public List<ExecutedSimpleCheck> getSimpleChecks() {
        return simpleChecks == null ? Collections.<ExecutedSimpleCheck>emptyList() : simpleChecks;
    }

    /**
     * @return результаты запуска матчеров на значениях, которые были извлечены из проверяемого, например,
     * если проверяемое значение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
     */
    @NotNull
    public List<ExecutedCompositeCheck> getCompositeChecks() {
        return compositeChecks == null ? Collections.<ExecutedCompositeCheck>emptyList() : compositeChecks;
    }
}
