package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.ReportingMatcher}'а
 * Экономит память как может, т. к. иногда бывает нужно проверять большие массивы/коллекции/мапы
 */
public class ExecutedCompositeCheck2 implements ExecutedCheck {
    @NotNull private final String name;
    @Nullable private final Object value;
    @NotNull private final ExtractedValueStatus valueStatus;
    private boolean isSuccessful;
    @NotNull private final List<ExecutedSimpleCheck> simpleChecks = new ArrayList<>();
    @NotNull private final List<ExecutedCompositeCheck2> compositeChecks = new ArrayList<>();


    // для тестов
    public ExecutedCompositeCheck2(@NotNull String name, @Nullable Object value, boolean isSuccessful,
                                   @NotNull ExtractedValueStatus valueStatus, @NotNull Collection<ExecutedSimpleCheck> simpleChecks,
                                   @NotNull Collection<ExecutedCompositeCheck2> compositeChecks) {
        this.name = name;
        this.value = value;
        this.isSuccessful = isSuccessful;
        this.valueStatus = valueStatus;
        this.simpleChecks.addAll(simpleChecks);
        this.compositeChecks.addAll(compositeChecks);
    }


    /**
     * @return название проверяемого значения, например имя поля объекта или ключ в мапе.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return проверяемое значение
     */
    @Nullable
    public Object getValue() {
        return value;
    }

    /**
     * @return результат извлечения значения
     */
    @NotNull
    public ExtractedValueStatus getValueStatus() {
        return valueStatus;
    }

    /**
     * @return true если все проверки успешны, false если хотя бы одна неуспешна
     */
    @Override
    public boolean isSuccessful() {
        return isSuccessful;
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
    public List<ExecutedCompositeCheck2> getCompositeChecks() {
        return unmodifiableList(compositeChecks);
    }


    // TODO: отдельный Builder, ExecutedCompositeCheck2 immutable с package-private конструктором?
    public void addCheck(ExecutedSimpleCheck check) {
        simpleChecks.add(check);
        isSuccessful &= check.isSuccessful();
    }

    public void addCheck(ExecutedCompositeCheck2 check) {
        compositeChecks.add(check);
        isSuccessful &= check.isSuccessful();
    }
}
