package com.github.alkedr.matchers.reporting;

import com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.FAILED;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.PASSED;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.UNCHECKED;
import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCompositeCheck.ExtractionStatus.NORMAL;
import static org.hamcrest.Matchers.*;

/**
 * User: alkedr
 * Date: 05.02.2015
 */
public final class ReportMatchers {
    private ReportMatchers() {
    }



    public static Matcher<ReportingMatcher.ExecutedCheck> status(ReportingMatcher.ExecutedCheck.Status value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCheck, ReportingMatcher.ExecutedCheck.Status>(equalTo(value), "status", "status") {
            @Override
            protected ReportingMatcher.ExecutedCheck.Status featureValueOf(ReportingMatcher.ExecutedCheck actual) {
                return actual.getStatus();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedCheck> matchesException(Exception value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCheck, Exception>(sameInstance(value), "matchesException", "matchesException") {
            @Override
            protected Exception featureValueOf(ReportingMatcher.ExecutedCheck actual) {
                return actual.getMatchesException();
            }
        };
    }




    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> matcherDescription(String value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedSimpleCheck, String>(equalTo(value), "matcherDescription", "matcherDescription") {
            @Override
            protected String featureValueOf(ReportingMatcher.ExecutedSimpleCheck actual) {
                return actual.getMatcherDescription();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> mismatchDescription(String value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedSimpleCheck, String>(equalTo(value), "mismatchDescription", "mismatchDescription") {
            @Override
            protected String featureValueOf(ReportingMatcher.ExecutedSimpleCheck actual) {
                return actual.getMismatchDescription();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> describeToException(Exception value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedSimpleCheck, Exception>(sameInstance(value), "describeToException", "describeToException") {
            @Override
            protected Exception featureValueOf(ReportingMatcher.ExecutedSimpleCheck actual) {
                return actual.getDescribeToException();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> describeMismatchException(Exception value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedSimpleCheck, Exception>(sameInstance(value), "describeMismatchException", "describeMismatchException") {
            @Override
            protected Exception featureValueOf(ReportingMatcher.ExecutedSimpleCheck actual) {
                return actual.getDescribeMismatchException();
            }
        };
    }




    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> name(String value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, String>(equalTo(value), "name", "name") {
            @Override
            protected String featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getName();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> value(Object value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, Object>(sameInstance(value), "value", "value") {
            @Override
            protected Object featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getValue();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> extractionStatus(ExtractionStatus value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, ExtractionStatus>(equalTo(value), "extractionStatus", "extractionStatus") {
            @Override
            protected ExtractionStatus featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getExtractionStatus();
            }
        };
    }

    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> extractionException(Exception value) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, Exception>(sameInstance(value), "extractionException", "extractionException") {
            @Override
            protected Exception featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getExtractionException();
            }
        };
    }


    public static <L> Matcher<ReportingMatcher.ExecutedCompositeCheck> simpleChecks(Matcher<? super List<? extends ReportingMatcher.ExecutedSimpleCheck>> matcher) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, List<? extends ReportingMatcher.ExecutedSimpleCheck>>(matcher, "simpleChecks", "simpleChecks") {
            @Override
            protected List<? extends ReportingMatcher.ExecutedSimpleCheck> featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getSimpleChecks();
            }
        };
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> simpleChecks(Matcher<ReportingMatcher.ExecutedSimpleCheck>... matchers) {
        return simpleChecks(contains(matchers));
    }


    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> compositeChecks(Matcher<? super List<? extends ReportingMatcher.ExecutedCompositeCheck>> matcher) {
        return new FeatureMatcher<ReportingMatcher.ExecutedCompositeCheck, List<? extends ReportingMatcher.ExecutedCompositeCheck>>(matcher, "compositeChecks", "compositeChecks") {
            @Override
            protected List<? extends ReportingMatcher.ExecutedCompositeCheck> featureValueOf(ReportingMatcher.ExecutedCompositeCheck actual) {
                return actual.getCompositeChecks();
            }
        };
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> compositeChecks(Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers) {
        return compositeChecks(contains(matchers));
    }




    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> simpleCheck(String matcherDescription) {
        return simpleCheck(matcherDescription, null);
    }

    public static Matcher<ReportingMatcher.ExecutedSimpleCheck> simpleCheck(String matcherDescription, String mismatchDescription) {
        return allOf(
                matcherDescription(matcherDescription),
                mismatchDescription(mismatchDescription),
                matchesException(null),
                describeToException(null),
                describeMismatchException(null)
        );
    }



    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> emptyCompositeCheck(String name, Object value) {
        return allOf(
                name(name),
                value(value),
                status(UNCHECKED),
                extractionStatus(NORMAL),
                extractionException(null),
                matchesException(null),
                simpleChecks(empty()),
                compositeChecks(empty())
        );
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> uncheckedCompositeCheck(String name, Object value, Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers) {
        return compositeCheck(name, value, UNCHECKED, matchers);
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> passedCompositeCheck(String name, Object value, Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers) {
        return compositeCheck(name, value, PASSED, matchers);
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> failedCompositeCheck(String name, Object value, Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers) {
        return compositeCheck(name, value, FAILED, matchers);
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> compositeCheck(
            String name, Object value, ReportingMatcher.ExecutedCheck.Status status,
            Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers
    ) {
        return passedCompositeCheck(name, value, status, NORMAL, null, null, matchers);
    }

    @SafeVarargs
    public static Matcher<ReportingMatcher.ExecutedCompositeCheck> passedCompositeCheck(
            String name, Object value, ReportingMatcher.ExecutedCheck.Status status, ExtractionStatus extractionStatus,
            Exception extractionException, Exception matchesException,
            Matcher<ReportingMatcher.ExecutedCompositeCheck>... matchers
    ) {
        Collection<Matcher<? super ReportingMatcher.ExecutedCompositeCheck>> result = new ArrayList<>();
        result.add(name(name));
        result.add(value(value));
        result.add(status(status));
        result.add(extractionStatus(extractionStatus));
        result.add(extractionException(extractionException));
        result.add(matchesException(matchesException));
        Collections.addAll(result, matchers);
        return allOf(result);
    }
}
