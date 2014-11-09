package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.CheckResult;
import alkedr.matchers.reporting.checks.PlannedCheck;
import alkedr.matchers.reporting.checks.PlannedCheckExtractor;
import ch.lambdaj.function.convert.Converter;
import org.hamcrest.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

/**
 * Матчер, проверяющий поля, свойства и возвращаемые значения методов любого класса
 * Использовать этот класс напрямую неудобно, лучше наследоваться и добавлять нужные методы
 * TODO: пример использования
 * TODO: механизм для сбора непроверенных полей  Что делать с полями, для которых были заданы другие имена?
 * TODO: несколько матчеров на поле
 * TODO: механизм для интеграции с аннотациями GSON, Jaxb, Selenium, htmlelements и пр.
 *       (interface NameExtractor, который преобразовывает (actual, defaultName) -> name)?
 */
public class ReportingMatcher<T, This extends ReportingMatcher<T, This>> extends TypeSafeDiagnosingMatcher<T> {
    private final Collection<PlannedCheckExtractor<T, ?>> fieldCheckExtractors = new ArrayList<>();
    private final Collection<PlannedCheckExtractor<T, ?>> nonFieldCheckExtractors = new ArrayList<>();
    private CheckResult result = null;

    /**
     * хранит информацию о запуске другого ReportingMatcher'а, который быз вызван из текущего ReportingMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего матчера к отчёту текущего матчера
     * checkThat зануляет INNER_COMPOSITE_MATCHER_RESULT и вызывает matcher.matches()
     * если после этого INNER_COMPOSITE_MATCHER_RESULT не нулл, значит matcher является ReportingMatcher'ом или использует ReportingMatcher внутри
     * нельзя просто попытаться покастить matcher к ReportingMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    private static final ThreadLocal<CheckResult> INNER_COMPOSITE_MATCHER_RESULT = new ThreadLocal<>();

    /**
     * Добавляет проверку поля.
     * @return this
     */
    public This field(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        fieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }

    /**
     * Добавляет проверку не-поля.
     * В отчёте такие проверки отобразится одним блоком отдельно от полей.
     * @return this
     */
    public This value(PlannedCheckExtractor<T, ?> plannedCheckExtractor) {
        nonFieldCheckExtractors.add(plannedCheckExtractor);
        return (This)this;
    }

    /**
     * @return результат проверки или null если метод {@link org.hamcrest.Matcher#matches} не был вызван
     */
    public CheckResult getCheckResult() {
        return result;
    }


    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        result = new CheckResult();
        result.setActualValueName("object");
        result.setFields(extractAndExecuteChecks(item, fieldCheckExtractors));
        result.setNonFields(extractAndExecuteChecks(item, nonFieldCheckExtractors));
        INNER_COMPOSITE_MATCHER_RESULT.set(result);
        return result.isSuccessful();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }



    private List<CheckResult> extractAndExecuteChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        return executeChecks(extractChecks(item, checkExtractors));
    }

    private List<PlannedCheck<?>> extractChecks(T item, Iterable<PlannedCheckExtractor<T, ?>> checkExtractors) {
        List<PlannedCheck<?>> extractedChecks = new ArrayList<>();
        for (PlannedCheckExtractor<T, ?> extractor : checkExtractors) {
            extractedChecks.addAll(extractor.extractChecks(item));
        }
        return extractedChecks;
    }

    private static List<CheckResult> executeChecks(List<PlannedCheck<?>> plannedChecks) {
        return convert(plannedChecks, new Converter<PlannedCheck<?>, CheckResult>() {
            @Override
            public CheckResult convert(PlannedCheck<?> from) {
                CheckResult check = new CheckResult();
                check.setActualValueName(from.getName());
                check.setActualValueAsString(String.valueOf(from.getActualValue()));

                if (from.getMatchers() != null) {
                    check.setMatcherDescription(getDescription(from.getMatchers()));
                    INNER_COMPOSITE_MATCHER_RESULT.remove();
                    if (!from.getMatchers().matches(from.getActualValue())) {
                        check.setMismatchDescription(getMismatchDescription(from.getActualValue(), from.getMatchers()));
                    }
                    if (INNER_COMPOSITE_MATCHER_RESULT.get() != null) {
                        check.setFields(INNER_COMPOSITE_MATCHER_RESULT.get().getFields());
                        check.setNonFields(INNER_COMPOSITE_MATCHER_RESULT.get().getNonFields());
                    }
                }
                return check;
            }
        });
    }

    @NotNull
    private static String getMismatchDescription(@NotNull Object actualValue, @NotNull Matcher<?> matcher) {
        StringDescription stringDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringDescription);
        return stringDescription.toString();
    }

    @NotNull
    private static String getDescription(@NotNull SelfDescribing selfDescribing) {
        StringDescription stringDescription = new StringDescription();
        selfDescribing.describeTo(stringDescription);
        return stringDescription.toString();
    }
}
