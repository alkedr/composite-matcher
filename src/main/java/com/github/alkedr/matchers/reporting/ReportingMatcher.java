package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.reporters.PlainTextReporter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.*;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExtractionStatus.NORMAL;
import static org.hamcrest.Matchers.isA;

public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    @NotNull private final Class<? super T> tClass;
    private ExecutedCompositeCheck lastReport = null;

    protected ReportingMatcher() {
        this.tClass = findClassOfT(getClass());
    }

    protected ReportingMatcher(@NotNull Class<? super T> tClass) {
        this.tClass = tClass;
    }


    public Class<? super T> getActualItemClass() {
        return tClass;
    }


    @Override
    public boolean matches(Object item) {
        REPORTING_MATCHER_FLAG.set(true);
        lastReport = getReport(item);
        return lastReport.getStatus() != FAILED;
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
        if (checkBuilder == null) checkBuilder = new ExecutedCompositeCheckImpl().value(item);
        if (tClass.isInstance(item)) {
            runChecks((T) item, checkBuilder);
        } else {
            checkBuilder.runMatcher(isA(tClass));
            runChecks(null, checkBuilder);
        }
        return checkBuilder;
    }


    public abstract void runChecks(@Nullable T item, ExecutedCompositeCheckBuilder checker);

    public interface ExecutedCheck {
        @NotNull Status getStatus();

        enum Status {
            UNCHECKED,
            PASSED,
            FAILED,
        }
    }

    /**
     * Хранит информацию о запуске обычного Matcher'а
     */
    public interface ExecutedSimpleCheck extends ExecutedCheck {
        @NotNull String getMatcherDescription();
        @Nullable String getMismatchDescription();
    }

    /**
     * Хранит информацию о запуске {@link ReportingMatcher}'а
     */
    public interface ExecutedCompositeCheck extends ExecutedCheck {
        @Nullable String getName();
        @Nullable Object getValue();
        @NotNull ExtractionStatus getExtractionStatus();
        @Nullable Exception getExtractionException();

        /**
         * @return результаты запуска матчеров на проверяемом значении
         */
        @NotNull
        List<? extends ExecutedSimpleCheck> getSimpleChecks();

        /**
         * @return результаты запуска матчеров на значениях, которые были извлечены из проверяемого, например,
         * если проверяемое значение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
         */
        @NotNull
        List<? extends ExecutedCompositeCheck> getCompositeChecks();
    }

    public enum ExtractionStatus {  // TODO: оставить только NORMAL и ERROR?
        NORMAL,
        MISSING,
        UNEXPECTED,
        ERROR,
        ;
    }

    public interface ExecutedCompositeCheckBuilder extends ExecutedCompositeCheck {
        ExecutedCompositeCheckBuilder name(String newName);
        ExecutedCompositeCheckBuilder value(Object newValue);
        ExecutedCompositeCheckBuilder extractionStatus(ExtractionStatus extractionStatus);
        ExecutedCompositeCheckBuilder extractionException(Exception newException);
        boolean runMatcher(Matcher<?> matcher);
        void runMatchers(Collection<? extends Matcher<?>> matchers);
        void runMatchers(Matcher<?>... matchers);
        ExecutedCompositeCheckBuilder subcheck();
    }



    private static class ExecutedSimpleCheckImpl implements ExecutedSimpleCheck {
        @NotNull private final String matcherDescription;
        @Nullable private final String mismatchDescription;

        private ExecutedSimpleCheckImpl(@NotNull String matcherDescription, @Nullable String mismatchDescription) {
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
        }

        @Override
        @NotNull
        public Status getStatus() {
            return mismatchDescription == null ? PASSED : FAILED;
        }

        @Override
        @NotNull
        public String getMatcherDescription() {
            return matcherDescription;
        }

        @Override
        @Nullable
        public String getMismatchDescription() {
            return mismatchDescription;
        }
    }


    // TODO: метод, вызываемый в самом конце, ходит по дереву проверок, вычисляет статусы, освобождает память,
    // TODO: проверяет, что у всех проверок все обязательные поля не null
    private static final class ExecutedCompositeCheckImpl implements ExecutedCompositeCheckBuilder {
        @Nullable private String name = null;
        @Nullable private Object value = null;
        @NotNull private ExtractionStatus extractionStatus = NORMAL;
        @Nullable private Exception extractionException = null;  // TODO: embed in status?
        @NotNull private Status status = UNCHECKED;
        @Nullable private List<ExecutedSimpleCheckImpl> simpleChecks = null;
        @Nullable private List<ExecutedCompositeCheckImpl> compositeChecks = null;

        @Override
        @NotNull
        public Status getStatus() {
            return status;
        }

        @Override
        @Nullable
        public String getName() {
            return name;
        }

        @Override
        @Nullable
        public Object getValue() {
            return value;
        }

        @Override
        @NotNull
        public ExtractionStatus getExtractionStatus() {
            return extractionStatus;
        }

        @Override
        @Nullable
        public Exception getExtractionException() {
            return extractionException;
        }

        @Override
        @NotNull
        public List<? extends ExecutedSimpleCheck> getSimpleChecks() {
            return simpleChecks == null ? Collections.<ExecutedSimpleCheck>emptyList() : simpleChecks;
        }

        @Override
        @NotNull
        public List<? extends ExecutedCompositeCheck> getCompositeChecks() {
            return compositeChecks == null ? Collections.<ExecutedCompositeCheck>emptyList() : compositeChecks;
        }


        @Override
        public void runMatchers(Collection<? extends Matcher<?>> matchers) {
            for (Matcher<?> matcher : matchers) {
                runMatcher(matcher);
            }
        }

        @Override
        public void runMatchers(Matcher<?>... matchers) {
            for (Matcher<?> matcher : matchers) {
                runMatcher(matcher);
            }
        }

        @Override
        public ExecutedCompositeCheckBuilder subcheck() {
            if (compositeChecks == null) compositeChecks = new ArrayList<>();
            ExecutedCompositeCheckImpl result = new ExecutedCompositeCheckImpl();
            compositeChecks.add(result);
            return result;
        }

        @Override
        public ExecutedCompositeCheckBuilder name(String newName) {
            this.name = newName;
            return this;
        }

        @Override
        public ExecutedCompositeCheckBuilder value(Object newValue) {
            this.value = newValue;
            return this;
        }

        @Override
        public ExecutedCompositeCheckBuilder extractionStatus(ExtractionStatus extractionStatus) {
            this.extractionStatus = extractionStatus;
            return this;
        }

        @Override
        public ExecutedCompositeCheckBuilder extractionException(Exception newException) {
            this.extractionException = newException;
            return this;
        }

        @Override
        public boolean runMatcher(Matcher<?> matcher) {
            REPORTING_MATCHER_FLAG.remove();
            CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER.set(this);
            boolean matcherResult = matcher.matches(value);
            if (REPORTING_MATCHER_FLAG.get() == null || REPORTING_MATCHER_FLAG.get() == false) {
                if (simpleChecks == null) simpleChecks = new ArrayList<>();
                String matcherDescription = StringDescription.asString(matcher);
                String mismatchDescription = matcherResult ? null : getMismatchDescription(matcher, value);
                simpleChecks.add(new ExecutedSimpleCheckImpl(matcherDescription, mismatchDescription));
            }
            return matcherResult;
        }

        private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
        }
    }

    private static <T> Class<T> findClassOfT(Class<?> thisClass) {
        for (Class<?> clazz = thisClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if ("runChecks".equals(method.getName()) && method.getParameterTypes().length == 2 && !method.isSynthetic()) {
                    return (Class<T>) method.getParameterTypes()[0];
                }
            }
        }
        throw new Error("Cannot determine class of T from runChecks method");
    }

    private static final ThreadLocal<Boolean> REPORTING_MATCHER_FLAG = new ThreadLocal<>();
    private static final ThreadLocal<ExecutedCompositeCheckImpl> CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER = new ThreadLocal<>();
}
