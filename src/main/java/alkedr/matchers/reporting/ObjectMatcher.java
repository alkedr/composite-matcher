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
    public <U> ObjectMatcher<T> property(String fieldNameForReport, U lambdajGetterMethodSelector, Matcher<? super U> matcher) {
        return field(new LambdajMethodSelectorCheckExtractor<T, U>(fieldNameForReport, lambdajGetterMethodSelector, matcher));
    }

    public <U> ObjectMatcher<T> property(U lambdajGetterMethodSelector, Matcher<? super U> matcher) {
        Argument<U> argument = actualArgument(lambdajGetterMethodSelector);
        return property(argument.getInkvokedPropertyName(), lambdajGetterMethodSelector, matcher);
    }

    public <U2, U1 extends U2> ObjectMatcher<T> property(String fieldNameForReport, U1 lambdajGetterMethodSelector, U2 expectedValue) {
        return property(fieldNameForReport, lambdajGetterMethodSelector, equalTo(expectedValue));
    }

    public <U2, U1 extends U2> ObjectMatcher<T> property(U1 lambdajGetterMethodSelector, U2 expectedValue) {
        return property(lambdajGetterMethodSelector, equalTo(expectedValue));
    }


    public ObjectMatcher<T> fieldsWithoutGetters(Matcher<String> fieldNameMatcher, Matcher<? super Object> valueMatcher) {
        return field(new FieldNameMatcherCheckExtractor<T>(fieldNameMatcher, valueMatcher));
    }


    public <U> ObjectMatcher<T> fieldWithoutGetter(String fieldNameForReport, String fieldNameForValueExtraction,
                                                   Matcher<? super U> valueMatcher) {
        return field(new FieldNameCheckExtractor<T, U>(fieldNameForReport, fieldNameForValueExtraction, valueMatcher));
    }

    public <U> ObjectMatcher<T> fieldWithoutGetter(String fieldNameForReport, String fieldNameForValueExtraction,
                                                      U expectedValue) {
        return fieldWithoutGetter(fieldNameForReport, fieldNameForValueExtraction, equalTo(expectedValue));
    }

    public <U> ObjectMatcher<T> fieldWithoutGetter(String fieldNameForValueExtraction, Matcher<? super U> valueMatcher) {
        return fieldWithoutGetter(null, fieldNameForValueExtraction, valueMatcher);
    }

    public <U> ObjectMatcher<T> fieldWithoutGetter(String fieldNameForValueExtraction, U expectedValue) {
        return fieldWithoutGetter(null, fieldNameForValueExtraction, equalTo(expectedValue));
    }


    public ObjectMatcher<T> allFields(Matcher<? super Object> valueMatcher) {
        return fieldsWithoutGetters(any(String.class), valueMatcher);
    }


    public ObjectMatcher<T> fieldsCount(Matcher<? super Integer> valueMatcher) {
        return value(new FieldCountCheckExtractor<T>(valueMatcher));
    }

    public ObjectMatcher<T> fieldsCount(int value) {
        return fieldsCount(equalTo(value));
    }
}
