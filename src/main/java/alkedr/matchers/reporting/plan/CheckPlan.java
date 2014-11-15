package alkedr.matchers.reporting.plan;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * T - ActualValue, U - ExtractedValue
 *
 * name - ключ, если у двух значений одно имя, то считаем, что это одно и то же значение, и экстракторы возвращают одно и то же, берём первый экстрактор
 *
 * У проверяемого объекта есть значения (поля, методы и пр.)
 * У значения есть название и экстрактор
 * одно и то же значение можно извлечь разными способами, например через геттер и через поле
 * У экстрактора есть список матчеров
 *
 * addValue добавляет значение без проверок в список.
 * Значения без проверок отображаются в отчёте.
 * Это может быть полезно для контроля за тем, какие поля проверяются, а какие нет.
 * Если для данного значения уже были добавлены проверки, то этот метод ничего не изменит.
 * Название значения используется как ключ, разные значения должны иметь разные названия,
 * одно и то же значение должно всегда иметь одно и то же название
 *
 * Если название значения не указано, то оно вычислится из extractor'а
 * Если вычислить не получится, то бросится исключение
 *
 * Нельзя сразу строить мапу название -> проверки, т. к. не знаем все названия заранее
 * Если неизвестные названия null, то они попадут в одну ячейку и нарушится порядок следования
 *
 * PlannedCheck'и с одинаковыми названиями и конвертерами объединаются
 * (конвертеры сравниваются по equals())
 *
 *
 * Объединение проверок в группы по именам нужно для отображения в отчёте
 * Объединение проверок по экстракторам нужно для сокращения кол-ва вызовов экстракторов
 */
public class CheckPlan<T> {
    private final Collection<PlannedCheck<T, ?>> plannedChecks = new LinkedList<>();

    /**
     * Добавляет проверку
     * @param extractor экстрактор проверяемого значения
     * @param newMatchers матчеры, которыми нужно проверять
     * @param <U> тип проверяемого значения
     * @return this
     */
    public <U> CheckPlan<T> add(NamedValueExtractor<T, U> extractor, Collection<Matcher<? super U>> newMatchers) {
        plannedChecks.add(new PlannedCheck<>(extractor, (Collection<Matcher<? super Object>>) newMatchers));
        return this;
    }


    /**
     * Выполняет проверки
     * @param item значение, которое нужно проверить
     * @return дерево выполненых проверок
     */
    public ExecutedCompositeCheck executeOn(@Nullable T item) {
        callSetItemForEveryExtractor(item);
        mergePlannedChecksWithSameNames();
        ExecutedCompositeCheck result = executePlannedChecks(item);
        INNER_CHECK_RESULT.set(result);
        return result;
    }


    private void callSetItemForEveryExtractor(@Nullable T item) {
        for (PlannedCheck<T, ?> plannedCheck : plannedChecks) {
            plannedCheck.extractor.setItem(item);
        }
    }

    private void mergePlannedChecksWithSameNames() {
        Map<String, PlannedCheck<T, ?>> index = new HashMap<>();
        Iterator<PlannedCheck<T, ?>> it = plannedChecks.iterator();
        while (it.hasNext()) {
            PlannedCheck<T, ?> plannedCheck = it.next();
            PlannedCheck<T, ?> plannedCheckInIndex = index.get(plannedCheck.extractor.getName());
            if (plannedCheckInIndex == null) {
                index.put(plannedCheck.extractor.getName(), plannedCheck);
            } else {
                plannedCheckInIndex.addMatchersFrom(plannedCheck);
                it.remove();
            }
        }
    }

    private ExecutedCompositeCheck executePlannedChecks(@Nullable T item) {
        ExecutedCompositeCheck result = new ExecutedCompositeCheck(String.valueOf(item));
        for (PlannedCheck<T, ?> plannedCheck : plannedChecks) {
            plannedCheck.executeAndStoreResultInto(result);
        }
        return result;
    }

    /**
     * хранит информацию о выполнении другого CheckPlan'а, которое было вызвано из текущего CheckPlan'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего ReportingMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();



    private static class PlannedCheck<T, U> {
        private final NamedValueExtractor<T, U> extractor;
        private final Collection<Matcher<? super Object>> matchers;

        private PlannedCheck(NamedValueExtractor<T, U> extractor, Collection<Matcher<? super Object>> matchers) {
            this.extractor = extractor;
            this.matchers = new ArrayList<>(matchers);
        }

        private void addMatchersFrom(PlannedCheck<T, ?> other) {
            matchers.addAll(other.matchers);
        }

        private void executeAndStoreResultInto(ExecutedCompositeCheck result) {
            for (Matcher<?> matcher : matchers) {
                INNER_CHECK_RESULT.remove();
                boolean matcherResult = matcher.matches(extractor.getValue());
                if (INNER_CHECK_RESULT.get() == null) {
                    result.addSimpleCheck(new ExecutedSimpleCheck(matcherResult, matcher, extractor.getValue()));
                } else {
                    result.addCompositeCheck(extractor.getName(), INNER_CHECK_RESULT.get());
                }
            }
        }
    }
}
