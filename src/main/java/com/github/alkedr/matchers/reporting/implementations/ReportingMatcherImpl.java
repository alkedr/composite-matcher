package com.github.alkedr.matchers.reporting.implementations;

import com.github.alkedr.matchers.reporting.ReportingMatcher;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static org.hamcrest.StringDescription.asString;

// Эти интерфейсы интересны только разработчикам матчеров и генераторов отчётов

// Потоковая генерация результатов проверок и отчётов не имеет смысла, потому что
//   - html-репортер её не поддерживает
//   - нам всё равно нужно хранить какие-то данные для объединения проверок

// В общем случае проверки не объединяются, в идеале объединение проверок вообще не должно быть в этом интерфейсе
// Некоторые матчеры умеют объединять свои проверки с проверками некоторых других матчеров

// НЕ НАДО ОБЪЕДИНЯТЬ ЗАПЛАНИРОВАННЫЕ ПРОВЕРКИ!
// Надо объединять выполненные по имени(equals) и значению(==, с учётом примитивных типов)
// Главный юзкейс для объединения запланированных проверок (мегакласс бэкенда) можно реализовать и без объединения запланированных проверок

public abstract class ReportingMatcherImpl<T, U extends ReportingMatcher<T>> extends BaseMatcher<T> implements ReportingMatcher<T> {
    private String descriptionString = "is correct";


    public U describedAs(String newDescriptionString) {
        this.descriptionString = newDescriptionString;
        return (U) this;
    }


    @Override
    public boolean matches(@Nullable Object item) {
        return matches(item, NO_OP_CHECK_LISTENER);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }

    @Override
    public void describeMismatch(@Nullable Object item, Description description) {
//        description.appendText(new PlainTextReporter().report(getReport(item)));
    }




    public static boolean normalValue(CheckListener listener, @NotNull String name, @Nullable Object value, @Nullable Object matcherObject) {
        listener.onStartValue(name, value);
        boolean result = runMatchersObject(listener, value, matcherObject);
        listener.onEndValue();
        return result;
    }

    public static boolean brokenValue(CheckListener listener, @NotNull String name, @NotNull Exception extractionException, @Nullable Object matcherObject) {
        listener.onStartBrokenValue(name, extractionException);
        boolean result = runMatchersObject(listener, null, matcherObject);
        listener.onEndValue();
        return result;
    }

    public static boolean missingValue(CheckListener listener, @NotNull String name, @Nullable Object matcherObject) {
        listener.onStartMissingValue(name);
        boolean result = runMatchersObject(listener, null, matcherObject);
        listener.onEndValue();
        return result;
    }

    public static boolean unexpectedValue(CheckListener listener, @NotNull String name, @Nullable Object value, @Nullable Object matcherObject) {
        listener.onStartUnexpectedValue(name, value);
        boolean result = runMatchersObject(listener, value, matcherObject);
        listener.onEndValue();
        return result;
    }


    private static boolean runMatchersObject(CheckListener listener, @Nullable Object value, @Nullable Object matcherObject) {
        if (matcherObject == null) return true;
        if (matcherObject instanceof Matcher) return matcher(listener, value, (Matcher<?>) matcherObject);
        if (matcherObject instanceof Collection) return matchers(listener, value, (Iterable<? extends Matcher<?>>) matcherObject);
        if (matcherObject instanceof Matcher[]) return matchers(listener, value, (Matcher<?>[]) matcherObject);
        throw new IllegalArgumentException("runMatchersObject: unknown matchers object " + matcherObject.getClass().getName());
    }

    private static boolean matchers(CheckListener listener, @Nullable Object value, @NotNull Matcher<?>... matchers) {
        boolean result = true;
        for (Matcher<?> matcher : matchers) result &= runMatchersObject(listener, value, matcher);
        return result;
    }

    private static boolean matchers(CheckListener listener, @Nullable Object value, @NotNull Iterable<? extends Matcher<?>> matchers) {
        boolean result = true;
        for (Matcher<?> matcher : matchers) result &= runMatchersObject(listener, value, matcher);
        return result;
    }

    private static boolean matcher(CheckListener listener, @Nullable Object value, @NotNull Matcher<?> matcher) {
        if (matcher instanceof ReportingMatcherImpl) {
            return ((ReportingMatcherImpl)matcher).matches(value, listener);
        } else {
            if (matcher.matches(value)) {
                listener.onMatcher(asString(matcher), null);
                return true;
            } else {
                StringDescription stringMismatchDescription = new StringDescription();
                matcher.describeMismatch(value, stringMismatchDescription);
                listener.onMatcher(asString(matcher), stringMismatchDescription.toString());
                return false;
            }
        }
    }



    private static final CheckListener NO_OP_CHECK_LISTENER = new CheckListener() {
        @Override
        public void onMatcher(@NotNull String matcherDescription, @Nullable String mismatchDescription) {
        }

        @Override
        public void onStartValue(@NotNull String name, @Nullable Object value) {
        }

        @Override
        public void onStartBrokenValue(@NotNull String name, @NotNull Exception extractionException) {
        }

        @Override
        public void onStartMissingValue(@NotNull String name) {
        }

        @Override
        public void onStartUnexpectedValue(@NotNull String name, @Nullable Object value) {
        }

        @Override
        public void onEndValue() {
        }
    };


    // TODO: в ObjectMatcher
//    U addUncheckedFields();
//    U addUncheckedMethods();



//    PlannedChecks getPlannedChecks(Object item);
//
//    // PlannedChecks - список проверок, которые мы собираемся выполнить
//    // Нельзя просто List<...> потому что иногда можно сэкономить время и пямять храня все запланированные проверки
//    // в одной структуре, например матчер для мапы может хранить запланированные проверки в мапе ключ => проверки
//    // НЕ РАБОТАЕТ ДЛЯ IterableMatcher
//    interface PlannedChecks {
//        // Возвращает true если ещё остались незапущенные проверки
//        boolean hasChecksLeft();
//
//        // Выполняет очередную проверку, результаты добавляет в storage, возвращает нечто, что будет передано в runAndRemoveAllChecksForTheSameValue другим PlannedChecks'ам
//        Object runRemoveAndReturnNextCheck(CompositeCheckInProgress storage);
//
//        // Выполняет все проверки для значения check, результаты добавляет в storage
//        void runAndRemoveAllChecksForTheSameValue(Object check, CompositeCheckInProgress storage);
//    }
//
//    // Можно плюнуть на эффективность и использовать линейный поиск и создавать отдельные объекты для каждой проверки
//    // Тогда нужно иметь способ отключать объединение проверок одних и тех же значений CompositeMatcher?   TODO: удостовериться, что CompositeMatcher можно будет запускать рекурсивно
//
//
//    interface Check {
//        @NotNull Status getStatus();
//
//        enum Status {
//            UNCHECKED, // проверок не было
//            PASSED,    // проверки были, все успешны
//            FAILED,    // проверки были, хотя бы одна неуспешна
//        }
//    }
//
//    interface SimpleCheck extends Check {
//        @NotNull String getMatcherDescription();
//        @Nullable String getMismatchDescription();
//    }
//
//    interface CompositeCheck extends Check {
//        @Nullable String getName();
//        @Nullable Object getValue();
//        @NotNull ExtractionStatus getExtractionStatus();
//        @Nullable Exception getExtractionException();
//        @NotNull Collection<? extends Check> getChecks();
//
//        enum ExtractionStatus {
//            NORMAL,
//            MISSING,
//            UNEXPECTED,
////            BROKEN,  // TODO: удалить?
//        }
//    }
//
//    interface CompositeCheckInProgress extends CompositeCheck {
//        void addCheck(Check check);
//    }


    // TODO: Reporting matchers create instances of PlannedChecks
    // TODO: PlannedChecks могут объединяться между собой


    // Если проверки объединяются только в пределах матчера, то как объединять unchecked? И вещи типа "предпоследний элемент массива"

    // "предпоследний элемент массива" объединяется только с другим "предпоследним элементом массива"?

    // 1. UncheckedValuesExtractor'ы знают про вещи типа "элемент мапы", "элемент массива" и пр. (прописано в UncheckedValuesListener)
    // 2!. unchecked значения не объединяются с матчерами, матчер всегда побеждает, мы должны только уметь
    //    обнаруживать ситуацию, когда unchecked и значение, извлечённое матчером, - одно и тоже
    // 3. Плюнуть на глубокий поиск unchecked значений, сделать фабрики матчеров или методы у матчеров, которые будут
    //    добавлять unchecked




//    @NotNull CompositeCheck getReport(@Nullable Object item);
//    Check.Status addChecksTo(@NotNull Checker storage);


//    interface UncheckedValuesExtractor {
//        void addUncheckedValuesTo(@NotNull CompositeCheck checker);
//    }




//    interface PlannedCheck {
//
//    }
//
//
//    // (Matchers + value) => CompositeCheck
//    //
//    // (Matcher + value) => {PlannedChecks | SimpleCheckImpl}
//    // (List<{PlannedChecks | SimpleCheckImpl}> + value) => CompositeCheck
//
//
//
//
//    interface ExecutableCheck {
//
//    }
//
//
//    interface ExecutableSimpleCheck extends ExecutableCheck, SimpleCheck {
//        boolean isExecuted();
//        void execute(Object value);
//    }
//
//    interface ExecutableCompositeCheck extends ExecutableCheck, CompositeCheck {
//        boolean hasExecutableChecksLeft();
//        ExecutableCheck executeRemoveAndReturnNextExecutableCheck();
//        void executeAndRemoveAllChecksForTheSameValue(ExecutableCompositeCheck plannedCheck);
//    }



//    ExecutableCompositeCheck getExecutableCompositeCheck(Object value);


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

//    interface Checker extends CompositeCheck {
////        void setName(String newName);  // для переименования в случае конфликта имён
//
//        // Запускает матчер на getValue()
//        // Если matcher является ReportingMatcher, то добавляет все его проверки
//        // Если matcher не является ReportingMatcher, то добавляет одну SimpleCheck
//        Status matcher(@NotNull Matcher<?> matcher);
//
//        // Добавляет
//        CompositeCheck subcheck(CompositeCheckAddingController addingController);
//
//        // что делать со значениями с одинаковыми именами?
//        // переименовывать! "value (1)", "value (2)" и т. д.
//    }
//
//    // Этот интерфейс реализован в конкретных матчерах для каждого вида извлекаемых значений
//    interface CompositeCheckAddingController {
//        // Для поиска уже существующего экземпляра CompositeCheck
//        boolean isCheckerForTheSameValue(com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.Checker check);
//
//        // Извлекает значение, создаёт Checker, проверки не выполняет
//        com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.Checker create(Object valueToExtractFrom);
//
//        // Запускает проверки, сохраняет результаты в checker
//        void runChecks(com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.Checker checker);
//    }
}
