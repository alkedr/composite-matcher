package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.ReportingMatcher}'а
 */
public class ExecutedCompositeCheck implements ExecutedCheck {
    @NotNull private final ExtractedValue extractedValue;
    private final Status status;
    @NotNull private final List<ExecutedSimpleCheck> simpleChecks;
    @NotNull private final List<ExecutedCompositeCheck> compositeChecks;


    public ExecutedCompositeCheck(@NotNull ExtractedValue extractedValue, Status status,
                                  @NotNull List<ExecutedSimpleCheck> simpleChecks,
                                  @NotNull List<ExecutedCompositeCheck> compositeChecks) {
        this.extractedValue = extractedValue;
        this.status = status;
        this.simpleChecks = simpleChecks;
        this.compositeChecks = compositeChecks;
    }


    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * @return проверяемое значение
     */
    @NotNull
    public ExtractedValue getExtractedValue() {
        return extractedValue;
    }

    /**
     * @return результаты запуска матчеров на проверяемом значении
     */
    @NotNull
    public List<ExecutedSimpleCheck> getSimpleChecks() {
        return unmodifiableList(simpleChecks);
    }

    /**
     * @return результаты запуска матчеров на значениях, которые были извлечены из проверяемого, например,
     * если проверяемое значение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
     */
    @NotNull
    public List<ExecutedCompositeCheck> getCompositeChecks() {
        return unmodifiableList(compositeChecks);
    }
}
