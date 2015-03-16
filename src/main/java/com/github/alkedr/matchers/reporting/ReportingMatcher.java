package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.reporters.PlainTextReporter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.*;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.NORMAL;
import static org.hamcrest.Matchers.isA;

public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    @NotNull private final Class<?> actualItemClass;
    private ExecutedCompositeCheck lastReport = null;

    protected ReportingMatcher(@NotNull Class<?> actualItemClass) {
        this.actualItemClass = actualItemClass;
    }


    // В этом классе потому что все ReportingMatcher'ы извлекают значения, для которых нужен UncheckedValuesExtractor
    public void uncheckedValuesExtractor(UncheckedValuesExtractor<T> extractor) {
    }

//    public void ignoreUncheckedMethod(ValueExtractor<T> valueExtractor) {
//
//    }
//
//    public void ignoreUncheckedGetter(ValueExtractor<T> valueExtractor) {
//
//    }

    public void ignoreUncheckedValue(String name) {

    }

    public interface UncheckedValuesExtractor<T> {
        void extract(@NotNull Class<?> itemClass, @Nullable T item, @NotNull ValueExtractingMatcherForExtending.UncheckedValuesAdder adder);
    }

    @NotNull
    public Class<?> getActualItemClass() {
        return actualItemClass;
    }

    @Override
    public boolean matches(Object item) {
        lastReport = getReport(item);
        REPORTING_MATCHER_FLAG.set(true);
        return lastReport.getStatus().isSuccessful();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is correct");
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (lastReport.getValue() != item) matches(item);
        description.appendText(new PlainTextReporter().report(lastReport));
    }

    public ExecutedCompositeCheck getReport(@Nullable Object item) {
        ExecutedCompositeCheckBuilder checkBuilder = CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER.get();
        boolean isRoot = false;
        if (checkBuilder == null) {
            checkBuilder = new ExecutedCompositeCheckBuilder().value(item);
            isRoot = true;
        }
        try {
            if (actualItemClass.isInstance(item)) {
                runChecks(item.getClass(), (T) item, checkBuilder);
            } else {
                checkBuilder.runMatcher(isA(actualItemClass));
                runChecks(actualItemClass, null, checkBuilder);
            }
        } finally {
            if (isRoot) {
                CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER.remove();
            }
        }
        checkBuilder.finishBuilding();
        return checkBuilder;
    }


    protected abstract void runChecks(@NotNull Class<?> itemClass, @Nullable T item, ExecutedCompositeCheckBuilder checker);


    public interface ExecutedCheck {
        @NotNull Status getStatus();
        @Nullable Exception getMatchesException();

        enum Status {
            UNCHECKED(true), // проверок не было
            PASSED(true),    // проверки были, все успешны
            FAILED(false),   // проверки были, хотя бы одна неуспешна
            BROKEN(false),   // кто-то бросил исключение
            ;

            private final boolean successful;

            Status(boolean successful) {
                this.successful = successful;
            }

            public boolean isSuccessful() {
                return successful;
            }
        }
    }

    /**
     * Хранит информацию о запуске обычного Matcher'а
     */
    public static final class ExecutedSimpleCheck implements ExecutedCheck {
        @NotNull private final String matcherDescription;
        @Nullable private final String mismatchDescription;
        @Nullable private final Exception matchesException;
        @Nullable private final Exception describeToException;
        @Nullable private final Exception describeMismatchException;

        private ExecutedSimpleCheck(@NotNull String matcherDescription, @Nullable String mismatchDescription,
                                    @Nullable Exception matchesException, @Nullable Exception describeToException,
                                    @Nullable Exception describeMismatchException) {
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
            this.matchesException = matchesException;
            this.describeToException = describeToException;
            this.describeMismatchException = describeMismatchException;
        }

        @Override
        @NotNull
        public Status getStatus() {
            if (matchesException != null || describeToException != null || describeMismatchException != null) return BROKEN;
            if (mismatchDescription != null) return FAILED;
            return PASSED;
        }

        @NotNull
        public String getMatcherDescription() {
            return matcherDescription;
        }

        @Nullable
        public String getMismatchDescription() {
            return mismatchDescription;
        }

        @Override
        @Nullable
        public Exception getMatchesException() {
            return matchesException;
        }

        @Nullable
        public Exception getDescribeToException() {
            return describeToException;
        }

        @Nullable
        public Exception getDescribeMismatchException() {
            return describeMismatchException;
        }
    }

    /**
     * Хранит информацию о запуске {@link ReportingMatcher}'а
     */
    public static class ExecutedCompositeCheck implements ExecutedCheck {
        @Nullable protected String name = null;
        @Nullable protected Object value = null;
        @NotNull protected Status status = UNCHECKED;
        @NotNull protected ExtractionStatus extractionStatus = NORMAL;
        @Nullable protected Exception extractionException = null;
        @Nullable protected Exception matchesException = null;
        @Nullable protected List<ExecutedSimpleCheck> simpleChecks = null;
        @Nullable protected List<ExecutedCompositeCheck> compositeChecks = null;

        /**
         * Экземпляры этого класса создаются через {@link ExecutedCompositeCheckBuilder}
         */
        protected ExecutedCompositeCheck() {
        }

        @Override
        @NotNull
        public Status getStatus() {
            return status;
        }

        /**
         * @return название проверяемого значения для отчёта, например название поля или метода, который мы проверяем
         * или номер элемента массива, который мы проверяем
         */
        @Nullable
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

        @NotNull
        public ExtractionStatus getExtractionStatus() {
            return extractionStatus;
        }

        @Nullable
        public Exception getExtractionException() {
            return extractionException;
        }

        @Override
        @Nullable
        public Exception getMatchesException() {
            return matchesException;
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
        public List<? extends ExecutedCompositeCheck> getCompositeChecks() {
            return compositeChecks == null ? Collections.<ExecutedCompositeCheck>emptyList() : compositeChecks;
        }

        public enum ExtractionStatus {
            NORMAL,
            MISSING,
            UNEXPECTED,
            BROKEN,
        }


        protected void finishBuilding() {
            boolean hasPassed = false;
            boolean hasFailed = false;
            boolean hasBroken = false;
            if (simpleChecks != null) {
                // TODO: trimToSize?
                for (ExecutedCheck simpleCheck : simpleChecks) {
                    if (simpleCheck.getStatus() == PASSED) hasPassed = true;
                    if (simpleCheck.getStatus() == FAILED) hasFailed = true;
                    if (simpleCheck.getStatus() == BROKEN) hasBroken = true;
                }
            }
            if (compositeChecks != null) {
                // TODO: trimToSize?
                for (ExecutedCompositeCheck compositeCheck : compositeChecks) {
                    compositeCheck.finishBuilding();
                    if (compositeCheck.getStatus() == PASSED) hasPassed = true;
                    if (compositeCheck.getStatus() == FAILED) hasFailed = true;
                    if (compositeCheck.getStatus() == BROKEN) hasBroken = true;
                }
            }
            if (hasBroken) status = BROKEN; else
            if (hasFailed) status = FAILED; else
            if (hasPassed) status = PASSED;
        }
    }

    // TODO: метод, вызываемый в самом конце, ходит по дереву проверок, вычисляет статусы, освобождает память,
    // TODO: проверяет, что у всех проверок все обязательные поля не null
    public static class ExecutedCompositeCheckBuilder extends ExecutedCompositeCheck {
        public ExecutedCompositeCheckBuilder subcheck() {
            if (compositeChecks == null) compositeChecks = new ArrayList<>();
            ExecutedCompositeCheckBuilder result = new ExecutedCompositeCheckBuilder();
            compositeChecks.add(result);
            return result;
        }

        public ExecutedCompositeCheckBuilder name(@NotNull String newName) {
            this.name = newName;
            return this;
        }

        public ExecutedCompositeCheckBuilder value(@Nullable Object newValue) {
            this.value = newValue;
            return this;
        }

        public ExecutedCompositeCheckBuilder extractionStatus(@NotNull ExtractionStatus newExtractionStatus) {
            this.extractionStatus = newExtractionStatus;
            return this;
        }

        public ExecutedCompositeCheckBuilder extractionException(@Nullable Exception newExtractionException) {
            this.extractionException = newExtractionException;
            return this;
        }


        public void runMatchers(Iterable<? extends Matcher<?>> matchers) {
            for (Matcher<?> matcher : matchers) {
                runMatcher(matcher);
            }
        }

        public void runMatchers(Matcher<?>... matchers) {
            for (Matcher<?> matcher : matchers) {
                runMatcher(matcher);
            }
        }

        /**
         * @param matchers Matcher, Collection<Matcher> или Matcher[]
         */
        public void runMatchersObject(Object matchers) {
            if (matchers instanceof Matcher) runMatcher((Matcher<?>) matchers); else
            if (matchers instanceof Collection) runMatchers((Iterable<? extends Matcher<?>>) matchers); else
            if (matchers instanceof Matcher[]) runMatchers((Matcher<?>[]) matchers); else
                throw new IllegalArgumentException("runMatchersObject: unknown matchers object " + (matchers == null ? "null" : matchers.getClass().getName()));
        }

        public boolean runMatcher(Matcher<?> matcher) {
            REPORTING_MATCHER_FLAG.remove();
            CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER.set(this);
            boolean matcherResult = matcher.matches(value);
            if (REPORTING_MATCHER_FLAG.get() == null || REPORTING_MATCHER_FLAG.get() == false) {
                if (simpleChecks == null) simpleChecks = new ArrayList<>();
                String matcherDescription = StringDescription.asString(matcher);
                String mismatchDescription = matcherResult ? null : getMismatchDescription(matcher, value);
                simpleChecks.add(new ExecutedSimpleCheck(matcherDescription, mismatchDescription, null, null, null));
            }
            return matcherResult;
        }

        private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
        }
    }


    // Все ReportingMatcher'ы устанавливают этот флаг в true в конце matches
    // Нужно чтобы ReportingMatcher мог определить является ли вложенный матчер ReportingMatcher'ом
    private static final ThreadLocal<Boolean> REPORTING_MATCHER_FLAG = new ThreadLocal<>();
    // Корневой ReportingMatcher передаёт всем вложенным матчерам свой ExecutedCompositeCheckBuilder
    private static final ThreadLocal<ExecutedCompositeCheckBuilder> CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER = new ThreadLocal<>();
}
