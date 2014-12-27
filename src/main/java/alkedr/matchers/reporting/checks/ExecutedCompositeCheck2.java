package alkedr.matchers.reporting.checks;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alkedr.matchers.reporting.checks.ExecutedCheckStatus.*;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Хранит информацию о запуске {@link alkedr.matchers.reporting.ReportingMatcher}'а
 * Экономит память как может, т. к. иногда бывает нужно проверять большие массивы/коллекции/мапы
 */
public class ExecutedCompositeCheck2 implements ExecutedCheck {
    // название проверяемого значения, например имя поля объекта или ключ в мапе
    // null у объекта, который был передан в matches()
    @Nullable private String name = null;

    // проверяемое значение
    @Nullable private final Object value;

    // результат проверок, которые были добавлены
    // обновляется при добавлении проверок
    @NotNull private ExecutedCheckStatus status = SKIPPED;

    // результаты запуска матчеров на проверяемом значении
    // изначально null чтобы не тратить память на пустой ArrayList в случае если ни одного матчера запущено не будет
    @Nullable private List<ExecutedSimpleCheck> simpleChecks = null;

    // результаты запуска матчеров на значениях, которые были извлечены из проверяемого,
    // например, если проверяемое знаяение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
    // изначально null чтобы не тратить память на пустой ArrayList в случае если ни одного матчера извлечено не будет
    // порядок сделования элементов не определён
    @Nullable private List<ExecutedCompositeCheck2> compositeChecks = null;


    // для тестов
    public ExecutedCompositeCheck2(@Nullable String name, @Nullable Object value, @NotNull ExecutedCheckStatus status,
                                   @Nullable List<ExecutedSimpleCheck> simpleChecks,
                                   @Nullable List<ExecutedCompositeCheck2> compositeChecks) {
        this.name = name;
        this.value = value;
        this.status = status;
        this.simpleChecks = simpleChecks;
        this.compositeChecks = compositeChecks;
    }

    // для статусов MISSING и UNEXPECTED
    public ExecutedCompositeCheck2(@NotNull String name, @Nullable Object value, @NotNull ExecutedCheckStatus status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }

    public ExecutedCompositeCheck2(@NotNull String name, @Nullable Object value) {
        this(name, value, SKIPPED);
    }

    public ExecutedCompositeCheck2(@Nullable Object value) {
        this.value = value;
    }


    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    @NotNull
    public ExecutedCheckStatus getStatus() {
        return status;
//        if (status != null) return status;
//        boolean hasPassedChecks = false;
//        for (ExecutedCompositeCheck check : Lambda.<ExecutedCompositeCheck>flatten(compositeChecks)) {
//            if (!check.getStatus().isSuccessful()) return FAILED;
//            if (check.getStatus() == PASSED) hasPassedChecks = true;
//        }
//        for (ExecutedCheck check : simpleChecks) {
//            if (!check.getStatus().isSuccessful()) return FAILED;
//            if (check.getStatus() == PASSED) hasPassedChecks = true;
//        }
//        return hasPassedChecks ? PASSED : SKIPPED;
    }

    @NotNull
    public List<ExecutedSimpleCheck> getSimpleChecks() {
        return simpleChecks == null ? new ArrayList<ExecutedSimpleCheck>() : unmodifiableList(simpleChecks);
    }

    @NotNull
    public List<ExecutedCompositeCheck2> getCompositeChecks() {
        return compositeChecks == null ? new ArrayList<ExecutedCompositeCheck2>() : unmodifiableList(compositeChecks);
    }


    @NotNull
    public Iterable<ExecutedCompositeCheck2> getAllCompositeChecks() {
        sortCompositeChecks();
        mergeOrRenameCompositeChecks();

        return new Iterable<ExecutedCompositeCheck2>() {
            @Override
            public Iterator<ExecutedCompositeCheck2> iterator() {    // TODO: throw exception if compositeChecks were modified during iteration
                return new Iterator<ExecutedCompositeCheck2>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public ExecutedCompositeCheck2 next() {
                        // возвращаем очередной элемент compositeChecks
                        // если они закончились, то возвращаем очередной элемент extractedValues, пропуская элементы, которые содержались в compositeChecks
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }




    private static final Comparator<ExecutedCompositeCheck2> COMPARATOR_BY_NAME = new Comparator<ExecutedCompositeCheck2>() {
        @Override
        public int compare(ExecutedCompositeCheck2 o1, ExecutedCompositeCheck2 o2) {
            if (o1.getName() == null && o2.getName() == null) return 0;
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            return o1.getName().compareTo(o2.getName());
        }
    };

    private void sortCompositeChecks() {
        if (compositeChecks == null) return;
        sort(compositeChecks, COMPARATOR_BY_NAME);
    }

    private void mergeOrRenameCompositeChecks() {
        // TODO: merge checks with same name, rename checks with same name and value
    }




    public boolean checkSilently(Matcher<?> matcher) {
        ExecutedCompositeCheck check = executeCheck(value, matcher);
        if (!check.getStatus().isSuccessful()) {
            addDataFrom(check);
            return false;
        }
        return true;
    }

    public <U> boolean checkAndReportIfMatches(Matcher<? super U> matcher) {
        ExecutedCompositeCheck check = executeCheck(value, matcher);
        if (check.getStatus().isSuccessful()) {
            addDataFrom(check);
            return true;
        }
        return false;
    }

    public <U> boolean checkAndReportIfMatches(String name, U value, Matcher<? super U> matcher) {
        return getOrAddValue(name, value).checkAndReportIfMatches(matcher);
    }

    public void reportMissingValue(String name) {
        getOrAddValue(name, null).status = MISSING;
    }

    public <U> void checkThat(String name, Object value, Matcher<?> matcher) {
        getOrAddValue(name, value).addDataFrom(executeCheck(value, matcher));
    }

    public <U> void reportValue(String name, Object value) {
        getOrAddValue(name, value);
    }


    private <U> ExecutedCompositeCheck getOrAddValue(String name, U value) {
        Map<Object, ExecutedCompositeCheck> valueToChecks = compositeChecks.get(name);
        if (valueToChecks == null) {
            valueToChecks = new LinkedHashMap<>();
            compositeChecks.put(name, valueToChecks);
        }
        ExecutedCompositeCheck check = valueToChecks.get(value);
        if (check == null) {
            check = new ExecutedCompositeCheck(value);
            valueToChecks.put(value, check);
        }
        return check;
    }

    public void addDataFrom(ExecutedCompositeCheck check) {
        if (!Objects.equals(value, check.actualValue)) {
            assert false;
        }
        simpleChecks.addAll(check.simpleChecks);
        compositeChecks.putAll(check.compositeChecks);
    }


    private static <U> ExecutedCompositeCheck executeCheck(@Nullable Object value, @NotNull Matcher<?> matcher) {
        INNER_CHECK_RESULT.remove();
        boolean matcherResult = matcher.matches(value);
        if (INNER_CHECK_RESULT.get() == null) {
            ExecutedCompositeCheck result = new ExecutedCompositeCheck(value);
            result.simpleChecks.add(new ExecutedSimpleCheck(StringDescription.toString(matcher),
                    matcherResult ? null : getMismatchDescription(matcher, value)));
            return result;
        } else {
            return INNER_CHECK_RESULT.get();
        }
    }

    private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
        StringDescription stringMismatchDescription = new StringDescription();
        matcher.describeMismatch(actualValue, stringMismatchDescription);
        return stringMismatchDescription.toString();
    }

    /**
     * хранит информацию о выполнении другого CompositeMatcher'а, которое было вызвано из текущего CompositeMatcher'а
     * нужно для того, чтобы присоединить отчёт о проверках внутреннего CompositeMatcher'а к отчёту
     * зануляем INNER_CHECK_RESULT и вызываем matcher.matches()
     * если после этого INNER_CHECK_RESULT не нулл, значит matcher является CompositeMatcher'ом или использует CompositeMatcher внутри
     * нельзя просто попытаться покастить matcher к CompositeMatcher'у, т. к. бывают обёртки для матчеров (напр. describedAs())
     */
    public static final ThreadLocal<ExecutedCompositeCheck> INNER_CHECK_RESULT = new ThreadLocal<>();

}
