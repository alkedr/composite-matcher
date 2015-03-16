package com.github.alkedr.matchers.reporting.ifaces;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.Check.Status.*;
import static com.github.alkedr.matchers.reporting.ifaces.ReportingMatcherForImplementing.CompositeCheck.ExtractionStatus.NORMAL;
import static org.hamcrest.StringDescription.asString;

public abstract class ReportingMatcherForExtending<T, U extends ReportingMatcherForExtending<T, U>>
        extends BaseMatcher<T>
        implements ReportingMatcherForImplementing<T, U>
{
    @NotNull private final Class<?> actualItemClass;
    @Nullable private UncheckedValuesExtractor uncheckedValuesExtractor = null;//DEFAULT_UNCHECKED_VALUES_EXTRACTOR;

    protected ReportingMatcherForExtending(@NotNull Class<?> actualItemClass) {
        this.actualItemClass = actualItemClass;
    }


    @NotNull
    @Override
    public Class<?> getActualItemClass() {
        return actualItemClass;
    }

    @Override
    public U uncheckedExtractor(UncheckedValuesExtractor newUncheckedValuesExtractor) {
        uncheckedValuesExtractor = newUncheckedValuesExtractor;
        return (U) this;
    }


    @Override
    public boolean matches(@Nullable Object item) {
        return getReport(item).getStatus() != FAILED;
    }


    @Override
    public void describeMismatch(@Nullable Object item, Description description) {
//        description.appendText(new PlainTextReporter().report(getReport(item)));
    }


    @NotNull
    @Override
    public CompositeCheck getReport(@Nullable Object item) {
        Checker root = new BaseChecker(null, item, NORMAL, null);
        if (uncheckedValuesExtractor != null) uncheckedValuesExtractor.addUncheckedValuesTo(root);
        addChecksTo(root);
        return root;
    }


    private static class SimpleCheckImpl implements SimpleCheck {
        @NotNull private final String matcherDescription;
        @Nullable private final String mismatchDescription;

        SimpleCheckImpl(@NotNull String matcherDescription, @Nullable String mismatchDescription) {
            this.matcherDescription = matcherDescription;
            this.mismatchDescription = mismatchDescription;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return mismatchDescription == null ? PASSED : FAILED;
        }

        @NotNull
        @Override
        public String getMatcherDescription() {
            return matcherDescription;
        }

        @Nullable
        @Override
        public String getMismatchDescription() {
            return mismatchDescription;
        }
    }


    public static class BaseChecker implements Checker {
        @NotNull private Status status = UNCHECKED;
        @Nullable private final String name;
        @Nullable private final Object value;
        @NotNull private final ExtractionStatus extractionStatus;
        @Nullable private final Exception extractionException;
        @NotNull private final Collection<Check> checks = new ArrayList<>();

        BaseChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                    @Nullable Exception extractionException) {
            this.name = name;
            this.value = value;
            this.extractionStatus = extractionStatus;
            this.extractionException = extractionException;
        }


        @Override
        public Status matcher(@NotNull Matcher<?> matcher) {
            if (matcher instanceof ReportingMatcher) {
                return ((ReportingMatcherForImplementing<?, ReportingMatcher<?>>) matcher).addChecksTo(this);
                // TODO: что вернуть?
            } else {
                if (matcher.matches(value)) {
                    checks.add(new SimpleCheckImpl(asString(matcher), null));
                    if (status == UNCHECKED) status = PASSED;
                    return PASSED;
                } else {
                    StringDescription stringMismatchDescription = new StringDescription();
                    matcher.describeMismatch(value, stringMismatchDescription);
                    checks.add(new SimpleCheckImpl(asString(matcher), stringMismatchDescription.toString()));
                    status = FAILED;
                    return FAILED;
                }
            }
        }

        @Override
        public Checker subcheck(CompositeCheckAddingController addingController) {
//        for (Node n = head; n != null; n = n.next) {
//            if (n is what we need) return n;
//            if (n.next == null) return n.next = new Node();
//        }
//        return head = new Node();
            for (Check check : checks) {
                if (check instanceof Checker && addingController.isCheckerForTheSameValue((Checker) check)) return (Checker) check;
            }
            Checker result = addingController.create(value);
            checks.add(result);
            return result;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public Object getValue() {
            return value;
        }

        @NotNull
        @Override
        public ExtractionStatus getExtractionStatus() {
            return extractionStatus;
        }

        @Nullable
        @Override
        public Exception getExtractionException() {
            return extractionException;
        }

        @NotNull
        @Override
        public Collection<? extends Check> getChecks() {
            return checks;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return status;
        }
    }


//    private static class FieldChecker extends BaseChecker {
//        final Field field;
//
//        FieldChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
//                     @Nullable Exception extractionException, Field field) {
//            super(name, value, extractionStatus, extractionException);
//            this.field = field;
//        }
//
//        @Override
//        protected boolean isField(String name, Field field) {
//            return Objects.equals(getName(), name) && Objects.equals(this.field, field);
//        }
//    }
//
//
//    private static class MethodChecker extends BaseChecker {
//        final Method method;
//        final Object[] arguments;
//
//        MethodChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
//                      @Nullable Exception extractionException, Method method, Object... arguments) {
//            super(name, value, extractionStatus, extractionException);
//            this.method = method;
//            this.arguments = arguments;
//        }
//
//        @Override
//        protected boolean isMethod(String name, Method method, Object... arguments) {
//            return Objects.equals(getName(), name) && Objects.equals(this.method, method) && Arrays.equals(this.arguments, arguments);
//        }
//    }
//
//
//    private static class ArrayElementChecker extends BaseChecker {
//        final int index;
//
//        ArrayElementChecker(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
//                                    @Nullable Exception extractionException, int index) {
//            super(name, value, extractionStatus, extractionException);
//            this.index = index;
//        }
//
//        @Override
//        protected boolean isArrayElement(String name, int index) {
//            return Objects.equals(getName(), name) && this.index == index;
//        }
//    }







//    public abstract static class AbstractCheckerImpl implements CompositeCheck {
//        @NotNull private Status status = UNCHECKED;
//        @NotNull private final Collection<Check> checks = new ArrayList<>();
//
//        public Status runMatcher(@NotNull Matcher<?> matcher) {
//            if (matcher instanceof ReportingMatcher) {
//                return ((ReportingMatcher<?>)matcher).addChecksTo(this);
//                // TODO: что вернуть?
//            } else {
//                if (matcher.matches(getValue())) {
//                    checks.add(new SimpleCheckImpl(asString(matcher), null));
//                    if (status == UNCHECKED) status = PASSED;
//                    return PASSED;
//                } else {
//                    StringDescription stringMismatchDescription = new StringDescription();
//                    matcher.describeMismatch(getValue(), stringMismatchDescription);
//                    checks.add(new SimpleCheckImpl(asString(matcher), stringMismatchDescription.toString()));
//                    status = FAILED;
//                    return FAILED;
//                }
//            }
//        }
//
////        @Override
////        public void addCheck(@NotNull Check check) {
////            checks.add(check);
////        }
//
//        @NotNull
//        @Override
//        public Collection<? extends Check> getChecks() {
//            return checks;
//        }
//
//        @NotNull
//        @Override
//        public Status getStatus() {
//            return status;
//        }
//    }
//
//
//    private static class RootCompositeCheck extends AbstractCheckerImpl {
//        @Nullable private final Object value;
//
//        RootCompositeCheck(@Nullable Object value) {
//            this.value = value;
//        }
//
//        @Nullable
//        @Override
//        public String getName() {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public Object getValue() {
//            return value;
//        }
//
//        @NotNull
//        @Override
//        public ExtractionStatus getExtractionStatus() {
//            return NORMAL;
//        }
//
//        @Nullable
//        @Override
//        public Exception getExtractionException() {
//            return null;
//        }
//    }



/*


    private static class BaseCompositeCheck2 implements Checker {
        @NotNull private Status status = UNCHECKED;
        @Nullable private final String name;
        @Nullable private final Object value;
        @NotNull private final ExtractionStatus extractionStatus;
        @Nullable private final Exception extractionException;
        @NotNull private final Collection<? extends Check> checks = new ArrayList<>();

        protected BaseCompositeCheck2(@Nullable String name, @Nullable Object value,
                                      @NotNull ExtractionStatus extractionStatus, @Nullable Exception extractionException) {
            this.name = name;
            this.value = value;
            this.extractionStatus = extractionStatus;
            this.extractionException = extractionException;
        }

        @Override
        public boolean runMatcher(@Nullable Matcher<?> matcher) {
            return false;
        }

        @Override
        public boolean matchers(@Nullable Matcher<?>... matchers) {
            return false;
        }

        @Override
        public boolean matchers(@Nullable Iterable<? extends Matcher<?>> matchers) {
            return false;
        }

        @Override
        public boolean matcherObject(@Nullable Object matchers) {
            return false;
        }

        @Override
        public Checker field(Field field) {
            return null;
        }

        @Override
        public Checker method(Method method, Object... arguments) {
            return null;
        }

        @Override
        public Checker arrayElement(int index) {
            return null;
        }

        @Override
        public Checker missingArrayElement(int index) {
            return null;
        }

        @Override
        public Checker unexpectedArrayElement(int index) {
            return null;
        }

        @Override
        public Checker listElement(int index) {
            return null;
        }

        @Override
        public Checker missingListElement(int index) {
            return null;
        }

        @Override
        public Checker unexpectedListElement(int index) {
            return null;
        }

        @Override
        public Checker valueOf(Object key) {
            return null;
        }

        @Override
        public Checker missingValueOf(Object key) {
            return null;
        }

        @Override
        public Checker unexpectedValueOf(Object key) {
            return null;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
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

        @NotNull
        @Override
        public Collection<? extends Check> getChecks() {
            return checks;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return status;
        }
    }


    private static class FieldCompositeCheck extends BaseCompositeCheck2 {
        @NotNull final Field field;

        FieldCompositeCheck(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                            @Nullable Exception extractionException, @NotNull Field field) {
            super(name == null ? field.getName() : name, value, extractionStatus, extractionException);
            this.field = field;
        }
    }

    private static class MethodCompositeCheck extends BaseCompositeCheck2 {
        @NotNull final Method method;
        @NotNull final Object[] arguments;

        MethodCompositeCheck(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                             @Nullable Exception extractionException, @NotNull Method method, @NotNull Object... arguments) {
            super(name == null ? method.getName() : name, value, extractionStatus, extractionException);
            this.method = method;
            this.arguments = arguments;
        }
    }

    private static class ArrayElementCompositeCheck extends BaseCompositeCheck2 {
        final int index;

        ArrayElementCompositeCheck(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                                   @Nullable Exception extractionException, int index) {
            super(name == null ? "[" + index + "]" : name, value, extractionStatus, extractionException);
            this.index = index;
        }
    }

    private static class ListElementCompositeCheck extends BaseCompositeCheck2 {
        final int index;

        ListElementCompositeCheck(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                                  @Nullable Exception extractionException, int index) {
            super(name == null ? "[" + index + "]" : name, value, extractionStatus, extractionException);
            this.index = index;
        }
    }

    private static class MapElementExtractor extends BaseCompositeCheck2 {
        final Object key;

        MapElementExtractor(@Nullable String name, @Nullable Object value, @NotNull ExtractionStatus extractionStatus,
                            @Nullable Exception extractionException, Object key) {
            super(name == null ? "[" + key + "]" : name, value, extractionStatus, extractionException);
            this.key = key;
        }
    }



    private abstract static class BaseCompositeCheck implements Checker {
        private Status status = PASSED;
        private final Collection<? extends Check> checks = new ArrayList<>();

        @NotNull
        @Override
        public Status getStatus() {
            return status;
        }

        @NotNull
        @Override
        public Collection<? extends Check> getChecks() {
            return checks;
        }


        @Override
        public boolean runMatcher(@Nullable Matcher<?> matcher) {
            if (matcher == null) return true;
            if (matcher instanceof ReportingMatcher) {
                ((ReportingMatcher<?>)matcher).addChecksTo(this);
                // TODO: что вернуть?
            } else {
                if (matcher.matches(getValue())) {
                    checks.add(new SimpleCheckImpl(asString(matcher), null));
                    if (status == UNCHECKED) status = PASSED;
                    return true;
                } else {
                    StringDescription stringMismatchDescription = new StringDescription();
                    matcher.describeMismatch(getValue(), stringMismatchDescription);
                    checks.add(new SimpleCheckImpl(asString(matcher), stringMismatchDescription.toString()));
                    status = FAILED;
                    return false;
                }
            }
            return false;
        }

        @Override
        public boolean matchers(@Nullable Matcher<?>... matchers) {
            if (matchers == null) return true;
            boolean result = true;
            for (Matcher<?> matcher : matchers) result &= runMatcher(matcher);
            return result;
        }

        @Override
        public boolean matchers(@Nullable Iterable<? extends Matcher<?>> matchers) {
            if (matchers == null) return true;
            boolean result = true;
            for (Matcher<?> matcher : matchers) result &= runMatcher(matcher);
            return result;
        }

        @Override
        public boolean matcherObject(@Nullable Object matchers) {
            if (matchers == null) return true;
            if (matchers instanceof Matcher) return runMatcher((Matcher<?>) matchers);
            if (matchers instanceof Collection) return matchers((Iterable<? extends Matcher<?>>) matchers);
            if (matchers instanceof Matcher[]) return matchers((Matcher<?>[]) matchers);
            throw new IllegalArgumentException("runMatchersObject: unknown matchers object " + matchers.getClass().getName());
        }

        @Override
        public Checker subcheck(CompositeCheck.ValueExtractor valueExtractor) {
            // TODO: search subchecks for subcheck with the same extractor, add if not found
            return null;
        }
    }


    private static class RootCompositeCheck extends BaseCompositeCheck {
        @Nullable private final Object value;

        RootCompositeCheck(@Nullable Object value) {
            this.value = value;
        }

        @NotNull
        @Override
        public ValueExtractor getValueExtractor() {
            return null;
        }

        @Nullable
        @Override
        public Object getValue() {
            return value;
        }

        @NotNull
        @Override
        public ExtractionStatus getExtractionStatus() {
            return NORMAL;
        }

        @Nullable
        @Override
        public Exception getExtractionException() {
            return null;
        }
    }


*/



/*


    private CheckerImpl getReport(@Nullable Object item, CheckerImpl checkBuilder) {
        if (actualItemClass.isInstance(item)) {
            runChecks(item.getClass(), (T) item, checkBuilder);
        } else {
            checkBuilder.matcher(isA(actualItemClass));
            runChecks(actualItemClass, null, checkBuilder);
        }
        checkBuilder.finishBuilding();
        return checkBuilder;
    }

    protected abstract void runChecks(@NotNull Class<?> itemClass, @Nullable T item, Checker checker);






    private static class CheckerImpl implements CompositeCheck, Checker {
        @Nullable private final String name;
        @Nullable private Object value = null;
        @NotNull private Check.Status status = UNCHECKED;
        @NotNull private ExtractionStatus extractionStatus = NORMAL;
        @Nullable private Exception extractionException = null;
        @Nullable private Exception matchesException = null;
        @Nullable private List<SimpleCheckImpl> simpleChecks = null;  // TODO: manual linked list?
        @Nullable private List<CheckerImpl> compositeChecks = null;

        CheckerImpl(@Nullable String name, Class<?> clazz) {
            this.name = name;
        }

        CheckerImpl(@Nullable Object item) {
            this.name = null;
            this.value = item;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return status;
        }

        @Nullable
        @Override
        public Exception getMatchesException() {
            return matchesException;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public Object getValue() {
            return value;
        }

        @NotNull
        @Override
        public ExtractionStatus getExtractionStatus() {
            return extractionStatus;
        }

        @Nullable
        @Override
        public Exception getExtractionException() {
            return extractionException;
        }

        @NotNull
        @Override
        public Iterable<? extends SimpleCheck> getSimpleChecks() {
            return simpleChecks == null ? Collections.<SimpleCheck>emptyList() : simpleChecks;
        }

        @NotNull
        @Override
        public Iterable<? extends CompositeCheck> getCompositeChecks() {
            return compositeChecks == null ? Collections.<CompositeCheck>emptyList() : compositeChecks;
        }


        @Override
        public boolean matcher(@Nullable Matcher<?> matcher) {
            if (matcher == null) return true;
            CHECKER_OF_OUTER_REPORTING_MATCHER.set(this);
            boolean matcherResult = matcher.matches(value);
            if (CHECKER_OF_OUTER_REPORTING_MATCHER.get() == this) {  // Reporting matcher would have called remove()
                if (simpleChecks == null) simpleChecks = new ArrayList<>();
                String matcherDescription = asString(matcher);
                String mismatchDescription = matcherResult ? null : getMismatchDescription(matcher, value);
                simpleChecks.add(new SimpleCheckImpl(matcherDescription, mismatchDescription, null, null, null));
            }
            return matcherResult;
        }

        @Override
        public boolean matchers(@Nullable Matcher<?>... matchers) {
            if (matchers == null) return true;
            boolean result = true;
            for (Matcher<?> matcher : matchers) result &= matcher(matcher);
            return result;
        }

        @Override
        public boolean matchers(@Nullable Iterable<? extends Matcher<?>> matchers) {
            if (matchers == null) return true;
            boolean result = true;
            for (Matcher<?> matcher : matchers) result &= matcher(matcher);
            return result;
        }

        @Override
        public boolean matcherObject(@Nullable Object matchers) {
            if (matchers == null) return true;
            if (matchers instanceof Matcher) return matcher((Matcher<?>) matchers);
            if (matchers instanceof Collection) return matchers((Iterable<? extends Matcher<?>>) matchers);
            if (matchers instanceof Matcher[]) return matchers((Matcher<?>[]) matchers);
            throw new IllegalArgumentException("runMatchersObject: unknown matchers object " + matchers.getClass().getName());
        }

        @Override
        public ExtractedValueAdder subcheck(@NotNull String name, Class<?> clazz) {
            CheckerImpl result = new CheckerImpl(name, clazz);
            if (compositeChecks == null) compositeChecks = new ArrayList<>();
            compositeChecks.add(result);
            return result;
        }


        @Override
        public Checker normal(@Nullable Object value) {
            this.value = value;
            this.extractionStatus = NORMAL;
            this.extractionException = null;
            return this;
        }

        @Override
        public Checker missing() {
            this.value = null;
            this.extractionStatus = MISSING;
            this.extractionException = null;
            return this;
        }

        @Override
        public Checker unexpected(@Nullable Object value) {
            this.value = value;
            this.extractionStatus = UNEXPECTED;
            this.extractionException = null;
            return this;
        }

        @Override
        public Checker broken(@Nullable Exception extractionException) {
            this.value = null;
            this.extractionStatus = BROKEN;
            this.extractionException = extractionException;
            return this;
        }



        void finishBuilding() {
            boolean hasPassed = false;
            boolean hasFailed = false;
            boolean hasBroken = false;
            if (simpleChecks != null) {
                for (Check simpleCheck : simpleChecks) {
                    if (simpleCheck.getStatus() == PASSED) hasPassed = true;
                    if (simpleCheck.getStatus() == FAILED) hasFailed = true;
                    if (simpleCheck.getStatus() == Status.BROKEN) hasBroken = true;
                }
            }
            if (compositeChecks != null) {
                for (CheckerImpl compositeCheck : compositeChecks) {
                    compositeCheck.finishBuilding();
                    if (compositeCheck.getStatus() == PASSED) hasPassed = true;
                    if (compositeCheck.getStatus() == FAILED) hasFailed = true;
                    if (compositeCheck.getStatus() == Status.BROKEN) hasBroken = true;
                }
            }
            if (hasBroken) status = Status.BROKEN; else
            if (hasFailed) status = FAILED; else
            if (hasPassed) status = PASSED;
        }

        private static String getMismatchDescription(Matcher<?> matcher, Object actualValue) {
            StringDescription stringMismatchDescription = new StringDescription();
            matcher.describeMismatch(actualValue, stringMismatchDescription);
            return stringMismatchDescription.toString();
        }
    }


    // Корневой ReportingMatcher передаёт всем вложенным матчерам свой CheckerImpl
    // Вложенные ReportingMatcher'ы очищают его чтобы корневой ReportingMatcher знал кто ReportingMatcher, а кто нет
    private static final ThreadLocal<CheckerImpl> CHECKER_OF_OUTER_REPORTING_MATCHER = new ThreadLocal<>();

    */
}
