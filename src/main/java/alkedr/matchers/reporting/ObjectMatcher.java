package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.extractors.FieldCountCheckExtractor;
import alkedr.matchers.reporting.checks.extractors.FieldNameCheckExtractor;
import alkedr.matchers.reporting.checks.extractors.FieldNameMatcherCheckExtractor;
import alkedr.matchers.reporting.checks.extractors.LambdajMethodSelectorCheckExtractor;
import ch.lambdaj.function.argument.Argument;
import org.hamcrest.Matcher;

import static ch.lambdaj.function.argument.ArgumentsFactory.actualArgument;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class ObjectMatcher<T> extends ReportingMatcher<T, ObjectMatcher<T>> {

    public <U> PropertyCheckAdder<U> property(String fieldNameForReport, U lambdajGetterMethodSelector) {
        return new PropertyCheckAdder<>(fieldNameForReport, lambdajGetterMethodSelector);
    }

    public <U> PropertyCheckAdder<U> property(U lambdajGetterMethodSelector) {
        Argument<U> argument = actualArgument(lambdajGetterMethodSelector);
        return property(argument.getInkvokedPropertyName(), lambdajGetterMethodSelector);
    }


    public FieldsCheckAdder fieldsWithoutGetters(Matcher<String> fieldNameMatcher) {
        return new FieldsCheckAdder(fieldNameMatcher);
    }


    public <U> FieldCheckAdder<U> fieldWithoutGetter(String fieldNameForReport, String fieldNameForValueExtraction) {
        return new FieldCheckAdder<U>(fieldNameForReport, fieldNameForValueExtraction);
    }

    public <U> FieldCheckAdder<U> fieldWithoutGetter(String fieldNameForValueExtraction) {
        return fieldWithoutGetter(null, fieldNameForValueExtraction);
    }


    public ObjectMatcher<T> allFieldsAre(Matcher<? super Object> valueMatcher) {
        return fieldsWithoutGetters(any(String.class)).are(valueMatcher);
    }


    public ObjectMatcher<T> fieldsCountIs(Matcher<? super Integer> valueMatcher) {
        return value(new FieldCountCheckExtractor<T>(valueMatcher));
    }

    public ObjectMatcher<T> fieldsCountIs(int value) {
        return fieldsCountIs(equalTo(value));
    }


    public class PropertyCheckAdder<U> {
        private final String fieldNameForReport;
        private final U lambdajGetterMethodSelector;

        private PropertyCheckAdder(String fieldNameForReport, U lambdajGetterMethodSelector) {
            this.fieldNameForReport = fieldNameForReport;
            this.lambdajGetterMethodSelector = lambdajGetterMethodSelector;
        }

        public ObjectMatcher<T> is(U expectedValue) {
            return is(equalTo(expectedValue));
        }

        public ObjectMatcher<T> is(Matcher<U> matcher) {
            return field(new LambdajMethodSelectorCheckExtractor<T, U>(fieldNameForReport, lambdajGetterMethodSelector, matcher));
        }
    }

    public class FieldCheckAdder<U> {
        private final String fieldNameForReport;
        private final String fieldNameForValueExtraction;

        private FieldCheckAdder(String fieldNameForReport, String fieldNameForValueExtraction) {
            this.fieldNameForReport = fieldNameForReport;
            this.fieldNameForValueExtraction = fieldNameForValueExtraction;
        }

        public ObjectMatcher<T> is(U expectedValue) {
            return is(equalTo(expectedValue));
        }

        public ObjectMatcher<T> is(Matcher<U> matcher) {
            return field(new FieldNameCheckExtractor<T, U>(fieldNameForReport, fieldNameForValueExtraction, matcher));
        }
    }

    public class FieldsCheckAdder {
        private final Matcher<String> fieldNameMatcher;

        public FieldsCheckAdder(Matcher<String> fieldNameMatcher) {
            this.fieldNameMatcher = fieldNameMatcher;
        }

        public ObjectMatcher<T> are(Matcher<? super Object> matcher) {
            return field(new FieldNameMatcherCheckExtractor<T>(fieldNameMatcher, matcher));
        }
    }
}
