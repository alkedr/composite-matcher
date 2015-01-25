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
import static org.hamcrest.Matchers.isA;

/**
 * Override runChecks.
 */
public abstract class ReportingMatcher<T> extends BaseMatcher<T> {
    @NotNull private final Class<? super T> tClass;
    private ExecutedCompositeCheck lastReport = null;

    protected ReportingMatcher() {
        this(Object.class);
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
        if (checkBuilder == null) checkBuilder = new ExecutedCompositeCheckImpl(null, item, ExtractionStatus.NORMAL, null);
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
            ;
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
        @NotNull List<ExecutedSimpleCheck> getSimpleChecks();

        /**
         * @return результаты запуска матчеров на значениях, которые были извлечены из проверяемого, например,
         * если проверяемое значение объект, то это могут быть поля объекта, если массив, то элементы, и т. д.
         */
        @NotNull List<ExecutedCompositeCheck> getCompositeChecks();
    }


    public enum ExtractionStatus {
        NORMAL,
        MISSING,
        UNEXPECTED,
        ERROR,
        ;
    }

    public interface ExecutedCompositeCheckBuilder extends ExecutedCompositeCheck {
        boolean runMatcher(Matcher<?> matcher);
        void runMatchers(Collection<? extends Matcher<?>> matchers);
        void addDataFrom(ExecutedCompositeCheck check);
        void addSimpleCheck(ExecutedSimpleCheck simpleCheck);
        void addCompositeCheck(ExecutedCompositeCheck compositeCheck);
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


    private static final class ExecutedCompositeCheckImpl implements ExecutedCompositeCheckBuilder {
        @Nullable private final String name;
        @Nullable private final Object value;
        @NotNull private final ExtractionStatus extractionStatus;
        @Nullable private final Exception extractionException;  // TODO: embed in status?
        @NotNull private Status status = UNCHECKED;
        @Nullable private List<ExecutedSimpleCheck> simpleChecks = null;
        @Nullable private List<ExecutedCompositeCheck> compositeChecks = null;

        private ExecutedCompositeCheckImpl(@Nullable String name, @Nullable Object value,
                                           @NotNull ExtractionStatus extractionStatus,
                                           @Nullable Exception extractionException) {
            this.name = name;
            this.value = value;
            this.extractionStatus = extractionStatus;
            this.extractionException = extractionException;
        }

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
        public List<ExecutedSimpleCheck> getSimpleChecks() {
            return simpleChecks == null ? Collections.<ExecutedSimpleCheck>emptyList() : simpleChecks;
        }

        @Override
        @NotNull
        public List<ExecutedCompositeCheck> getCompositeChecks() {
            return compositeChecks == null ? Collections.<ExecutedCompositeCheck>emptyList() : compositeChecks;
        }


        @Override
        public void runMatchers(Collection<? extends Matcher<?>> matchers) {
            for (Matcher<?> matcher : matchers) {
                runMatcher(matcher);
            }
        }

        @Override
        public boolean runMatcher(Matcher<?> matcher) {
            REPORTING_MATCHER_FLAG.remove();
            CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER.set(this);
            boolean matcherResult = matcher.matches(value);
            if (REPORTING_MATCHER_FLAG.get() == null || REPORTING_MATCHER_FLAG.get() == false) {
                addSimpleCheck(new ExecutedSimpleCheckImpl(StringDescription.toString(matcher),
                        matcherResult ? null : getMismatchDescription(matcher, value)));
            }
            return matcherResult;
        }

        @Override
        public void addDataFrom(ExecutedCompositeCheck check) {
            for (ExecutedSimpleCheck simpleCheck : check.getSimpleChecks()) addSimpleCheck(simpleCheck);
            for (ExecutedCompositeCheck compositeCheck : check.getCompositeChecks()) addCompositeCheck(compositeCheck);
        }

        @Override
        public void addSimpleCheck(ExecutedSimpleCheck simpleCheck) {
            if (simpleChecks == null) simpleChecks = new ArrayList<>();
            simpleChecks.add(simpleCheck);
            updateStatusAfterAddingCheckWithStatus(simpleCheck.getStatus());
        }

        @Override
        public void addCompositeCheck(ExecutedCompositeCheck compositeCheck) {
            if (compositeChecks == null) compositeChecks = new ArrayList<>();
            compositeChecks.add(compositeCheck);
            updateStatusAfterAddingCheckWithStatus(compositeCheck.getStatus());
        }

        private void updateStatusAfterAddingCheckWithStatus(Status newCheckStatus) {
            if (status == UNCHECKED || newCheckStatus == FAILED) status = newCheckStatus;
        }

        private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
        }
    }

    private static final ThreadLocal<Boolean> REPORTING_MATCHER_FLAG = new ThreadLocal<>();
    private static final ThreadLocal<ExecutedCompositeCheckBuilder> CHECK_BUILDER_OF_OUTER_REPORTING_MATCHER = new ThreadLocal<>();
}
