package alkedr.matchers.reporting.matchers.object;

import alkedr.matchers.reporting.checks.CheckExecutor;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractingMatcher;
import alkedr.matchers.reporting.matchers.ValuesExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.*;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static ch.lambdaj.Lambda.argument;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;

public class ObjectMatcher<T> extends ValueExtractingMatcher<T> {
    private final Class<T> tClass;


    public ObjectMatcher(Class<T> tClass) {
        this.tClass = tClass;
    }


    public <U> PlannedCheckAdder<U> property(String nameForReport, U lambdajArgumentPlaceholder) {
        Argument<U> argument = argument(lambdajArgumentPlaceholder);
        return new PlannedCheckAdder<>(new LambdajPropertyExtractor<T, U>(nameForReport == null ? argument.getInkvokedPropertyName() : nameForReport, argument));
    }

    public <U> PlannedCheckAdder<U> property(U lambdajArgumentPlaceholder) {
        return property(null, lambdajArgumentPlaceholder);
    }


    public PlannedChecksAdder fields(Matcher<String> fieldNameMatcher) {
        return new PlannedChecksAdder(new FieldsExtractor<T>(fieldNameMatcher));
    }


    public <U> PlannedCheckAdder<U> field(String nameForReport, String nameForValueExtraction) {
        return new PlannedCheckAdder<>(new FieldExtractor<T, U>(nameForReport, nameForValueExtraction));
    }

    public <U> PlannedCheckAdder<U> field(String nameForReportAndValueExtraction) {
        return field(nameForReportAndValueExtraction, nameForReportAndValueExtraction);
    }


    public ObjectMatcher<T> allFieldsAre(Matcher<? super Object> valueMatcher) {
        return fields(any(String.class)).are(valueMatcher);
    }

    private ObjectMatcher<T> allMethodsThatReturnNonVoidReturn(Matcher<? super Object> matcher) {
        addPlannedCheck(new AllMethodsThatReturnNonVoidExtractor<>(tClass), asList(matcher));
        return this;
    }


    public ObjectMatcher<T> fieldsCountIs(Matcher<? super Integer> valueMatcher) {
        addPlannedCheck(new FieldsCountExtractor<T>(tClass, "<fields count>"), asList(valueMatcher));
        return this;
    }

    public ObjectMatcher<T> fieldsCountIs(int value) {
        return fieldsCountIs(equalTo(value));
    }



    public class PlannedCheckAdder<U> {
        private final ValuesExtractor<T> extractor;

        private PlannedCheckAdder(ValuesExtractor<T> extractor) {
            this.extractor = extractor;
        }

        public ObjectMatcher<T> isUnchecked() {
            addPlannedCheck(extractor, new ArrayList<Matcher<? super U>>());
            return ObjectMatcher.this;
        }

        public ObjectMatcher<T> isEqualTo(U expectedValue) {
            return is(equalTo(expectedValue));
        }

        public ObjectMatcher<T> isEqualToIfNotNull(U expectedValue) {
            return expectedValue == null ? isUnchecked() : isEqualTo(expectedValue);
        }

        public ObjectMatcher<T> is(Matcher<? super U> matcher) {
            addPlannedCheck(extractor, asList(matcher));
            return ObjectMatcher.this;
        }

        public ObjectMatcher<T> isIf(boolean condition, Matcher<? super U> matcher) {
            return condition ? is(matcher) : isUnchecked();
        }
    }

    public class PlannedChecksAdder {
        private final ValuesExtractor<T> extractor;

        private PlannedChecksAdder(ValuesExtractor<T> extractor) {
            this.extractor = extractor;
        }

        public ObjectMatcher<T> are(Matcher<? super Object> matcher) {
            addPlannedCheck(extractor, asList(matcher));
            return ObjectMatcher.this;
        }
    }


    @Override
    public ExecutedCompositeCheck getReportSafely(@Nullable T item) {
        CheckExecutor<T> executor = new CheckExecutor<>(new ExtractedValue("", item));
        executor.checkAndReportIfDoesntMatch(isA(tClass));
        executor.addDataFrom(super.getReportSafely(item));
        return executor.buildCompositeCheck();
    }


    public static <T> ObjectMatcher<T> object(Class<T> tClass) {
        return new ObjectMatcher<>(tClass);
    }

    public static <T> ObjectMatcher<T> beanWithFields(Class<T> tClass) {
        // TODO: универсальный матчер для непроверенных полей, который знает про коллекции, мапы и пр.
        // TODO: он должен будет как-то поддерживвть blacklisting полей и методов на случай  Object getThis() { return this; } ?
        ObjectMatcher<Object> recursiveFieldsMatcher = object(Object.class);
        recursiveFieldsMatcher.allFieldsAre(recursiveFieldsMatcher);
        return object(tClass).allFieldsAre(recursiveFieldsMatcher);
    }

    // TODO: beanWithPrivateFields

    public static <T> ObjectMatcher<T> beanWithGetters(Class<T> tClass) {
        ObjectMatcher<Object> recursiveGettersMatcher = object(Object.class);
        recursiveGettersMatcher.allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
        return object(tClass).allMethodsThatReturnNonVoidReturn(recursiveGettersMatcher);
    }

    // TODO: beanWithPrivateGetters?
}
