package alkedr.matchers.reporting.matchers.object;

import alkedr.matchers.reporting.matchers.ValueExtractingMatcher;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import alkedr.matchers.reporting.matchers.object.extractors.*;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;

import java.util.ArrayList;

import static ch.lambdaj.Lambda.argument;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class ObjectMatcher<T> extends ValueExtractingMatcher<T> {
    public ObjectMatcher(Class<T> tClass) {
        super(tClass);
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

    public ObjectMatcher<T> allMethodsThatReturnNonVoidReturn(Matcher<? super Object> matcher) {
        addPlannedCheck(new AllMethodsThatReturnNonVoidExtractor<>(getActualItemClass()), asList(matcher));
        return this;
    }


    public ObjectMatcher<T> fieldsCountIs(Matcher<? super Integer> valueMatcher) {
        addPlannedCheck(new FieldsCountExtractor<T>(getActualItemClass(), "<fields count>"), asList(valueMatcher));
        return this;
    }

    public ObjectMatcher<T> fieldsCountIs(int value) {
        return fieldsCountIs(equalTo(value));
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
