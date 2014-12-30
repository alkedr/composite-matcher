package alkedr.matchers.reporting.matchers.object;

import alkedr.matchers.reporting.matchers.ValueExtractingMatcher;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.fields.FieldExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.fields.FieldsWithMatchingNameExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.lambdaj.LambdajArgumentExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.methods.AllMethodsThatReturnNonVoidExtractor;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static ch.lambdaj.Lambda.argument;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class ObjectMatcher<T> extends ValueExtractingMatcher<T, ObjectMatcher<T>> {
    public ObjectMatcher(Class<T> tClass) {
        super(tClass);
    }


    public <U> PlannedCheckAdder<U> property(String nameForReport, U lambdajArgumentPlaceholder) {
        Argument<U> argument = argument(lambdajArgumentPlaceholder);
        return new PlannedCheckAdder<>(new LambdajArgumentExtractor<T, U>(nameForReport == null ? argument.getInkvokedPropertyName() : nameForReport, argument));
    }

    public <U> PlannedCheckAdder<U> property(U lambdajArgumentPlaceholder) {
        return property(null, lambdajArgumentPlaceholder);
    }


    public PlannedChecksAdder fields(Matcher<String> fieldNameMatcher) {
        return new PlannedChecksAdder(new FieldsWithMatchingNameExtractor<>(getActualItemClass(), new FeatureMatcher<Field, String>(fieldNameMatcher, "", "") {
            @Override
            protected String featureValueOf(Field actual) {
                return actual.getName();
            }
        }));
    }


    public <U> PlannedCheckAdder<U> field(String nameForReport, String nameForValueExtraction) {
        return new PlannedCheckAdder<>(new FieldExtractor<T, U>(getActualItemClass(), nameForReport, nameForValueExtraction));
    }

    public <U> PlannedCheckAdder<U> field(String nameForReportAndValueExtraction) {
        return field(nameForReportAndValueExtraction, nameForReportAndValueExtraction);
    }


    public ObjectMatcher<T> allFieldsAre(Matcher<? super Object> valueMatcher) {
        return fields(any(String.class)).are(valueMatcher);
    }

    public ObjectMatcher<T> allMethodsThatReturnNonVoidReturn(Matcher<? super Object> matcher) {
        addPlannedCheck(new AllMethodsThatReturnNonVoidExtractor<>(getActualItemClass()), asList(matcher));
        return this;
    }


    public class PlannedCheckAdder<U> {
        private final ValueExtractor<T> extractor;

        private PlannedCheckAdder(ValueExtractor<T> extractor) {
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
        private final ValueExtractorsExtractor<T> extractor;

        private PlannedChecksAdder(ValueExtractorsExtractor<T> extractor) {
            this.extractor = extractor;
        }

        public ObjectMatcher<T> are(Matcher<? super Object> matcher) {
            addPlannedCheck(extractor, asList(matcher));
            return ObjectMatcher.this;
        }
    }
}
