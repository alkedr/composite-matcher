package com.github.alkedr.matchers.reporting;

/**
 * User: alkedr
 * Date: 05.02.2015
 */
public final class ReportMatchers {
    /*private ReportMatchers() {
    }

    public static Matcher<? super SimpleCheck> simpleCheck(Matcher<Check.Status> statusMatcher, Matcher<String> matcherDescriptionMatcher, Matcher<String> mismatchDescriptionMatcher) {
        return allOf(
                new FeatureMatcher<Check, Check.Status>(statusMatcher, "status", "status") {
                    @Override
                    protected Check.Status featureValueOf(Check actual) {
                        return actual.getStatus();
                    }
                },

                new FeatureMatcher<SimpleCheck, String>(matcherDescriptionMatcher, "matcherDescription", "matcherDescription") {
                    @Override
                    protected String featureValueOf(SimpleCheck actual) {
                        return actual.getMatcherDescription();
                    }
                },

                new FeatureMatcher<SimpleCheck, String>(mismatchDescriptionMatcher, "mismatchDescription", "mismatchDescription") {
                    @Override
                    protected String featureValueOf(SimpleCheck actual) {
                        return actual.getMismatchDescription();
                    }
                }
        );
    }

    public static Matcher<? super SimpleCheck> passedSimpleCheck(String matcherDescription) {
        return simpleCheck(equalTo(PASSED), equalTo(matcherDescription), nullValue(String.class));
    }

    public static Matcher<? super SimpleCheck> failedSimpleCheck(String matcherDescription, String mismatchDescription) {
        return simpleCheck(equalTo(PASSED), equalTo(matcherDescription), equalTo(mismatchDescription));
    }


    public static Matcher<? super CompositeCheck> compositeCheck(
            Matcher<Check.Status> statusMatcher, Matcher<String> nameMatcher, Matcher<Object> valueMatcher, Matcher<ExtractionStatus> extractionStatusMatcher,
            Matcher<Exception> extractionExceptionMatcher, Matcher<? super Collection<? extends Check>> checksMatchers)
    {
        return allOf(
                new FeatureMatcher<Check, Check.Status>(statusMatcher, "status", "status") {
                    @Override
                    protected Check.Status featureValueOf(Check actual) {
                        return actual.getStatus();
                    }
                },

                new FeatureMatcher<CompositeCheck, String>(nameMatcher, "name", "name") {
                    @Override
                    protected String featureValueOf(CompositeCheck actual) {
                        return actual.getName();
                    }
                },

                new FeatureMatcher<CompositeCheck, Object>(valueMatcher, "value", "value") {
                    @Override
                    protected Object featureValueOf(CompositeCheck actual) {
                        return actual.getValue();
                    }
                },

                new FeatureMatcher<CompositeCheck, ExtractionStatus>(extractionStatusMatcher, "extractionStatus", "extractionStatus") {
                    @Override
                    protected ExtractionStatus featureValueOf(CompositeCheck actual) {
                        return actual.getExtractionStatus();
                    }
                },

                new FeatureMatcher<CompositeCheck, Exception>(extractionExceptionMatcher, "extractionException", "extractionException") {
                    @Override
                    protected Exception featureValueOf(CompositeCheck actual) {
                        return actual.getExtractionException();
                    }
                },

                new FeatureMatcher<CompositeCheck, Collection<? extends Check>>(checksMatchers, "checks", "checks") {
                    @Override
                    protected Collection<? extends Check> featureValueOf(CompositeCheck actual) {
                        return actual.getChecks();
                    }
                }
        );
    }

    @SafeVarargs
    public static Matcher<? super CompositeCheck> compositeCheck(
            Check.Status status, String name, Object value, ExtractionStatus extractionStatus,
            Matcher<Exception> extractionExceptionMatcher, Matcher<? super Check>... checksMatchers)
    {
        return compositeCheck(equalTo(status), equalTo(name), sameInstance(value), equalTo(extractionStatus), extractionExceptionMatcher, contains(asList(checksMatchers)));
    }

    public static Matcher<? super CompositeCheck> emptyCompositeCheck(String name, Object value) {
        return compositeCheck(UNCHECKED, name, value, NORMAL, nullValue(Exception.class));
    }

    @SafeVarargs
    public static Matcher<? super CompositeCheck> passedCompositeCheck(String name, Object value, Matcher<? super Check>... checksMatchers) {
        return compositeCheck(PASSED, name, value, NORMAL, nullValue(Exception.class), checksMatchers);
    }

    @SafeVarargs
    public static Matcher<? super CompositeCheck> failedCompositeCheck(String name, Object value, Matcher<? super Check>... checksMatchers) {
        return compositeCheck(FAILED, name, value, NORMAL, nullValue(Exception.class), checksMatchers);
    }

*/



//    @SafeVarargs
//    public static Matcher<CompositeCheck> compositeCheck(Check.Status status, String name, Object value, ExtractionStatus extractionStatus,
//                                                                          Matcher<Exception> extractionExceptionMatcher,
//                                                                          Matcher<Check>... checksMatchers) {
//        return allOf(
//                status(status),
//                name(name),
//                value(value),
//                extractionStatus(extractionStatus),
//                extractionException(extractionExceptionMatcher),
//                checks(checksMatchers)
//        );
//    }


//    public static Matcher<CompositeCheck> emptyCompositeCheck(String name, Object value) {
//        return allOf(
//                name(name),
//                value(value),
//                status(UNCHECKED),
//                extractionStatus(NORMAL),
//                extractionException(null)//,
////                matchesException(null),
////                simpleChecks(empty()),
////                compositeChecks(empty())
//        );
//    }
//
//    @SafeVarargs
//    public static Matcher<CompositeCheck> uncheckedCompositeCheck(String name, Object value, Matcher<CompositeCheck>... matchers) {
//        return compositeCheck(name, value, UNCHECKED, matchers);
//    }
//
//    @SafeVarargs
//    public static Matcher<CompositeCheck> passedCompositeCheck(String name, Object value, Matcher<CompositeCheck>... matchers) {
//        return compositeCheck(name, value, PASSED, matchers);
//    }
//
//    @SafeVarargs
//    public static Matcher<CompositeCheck> failedCompositeCheck(String name, Object value, Matcher<CompositeCheck>... matchers) {
//        return compositeCheck(name, value, FAILED, matchers);
//    }
//
//    @SafeVarargs
//    public static Matcher<CompositeCheck> compositeCheck(
//            String name, Object value, Check.Status status,
//            Matcher<CompositeCheck>... matchers
//    ) {
//        return passedCompositeCheck(name, value, status, NORMAL, null, null, matchers);
//    }
//
//    @SafeVarargs
//    public static Matcher<CompositeCheck> passedCompositeCheck(
//            String name, Object value, Check.Status status, ExtractionStatus extractionStatus,
//            Exception extractionException, Exception matchesException,
//            Matcher<CompositeCheck>... matchers
//    ) {
//        Collection<Matcher<? super CompositeCheck>> result = new ArrayList<>();
//        result.add(name(name));
//        result.add(value(value));
//        result.add(status(status));
//        result.add(extractionStatus(extractionStatus));
//        result.add(extractionException(extractionException));
////        result.add(matchesException(matchesException));
//        Collections.addAll(result, matchers);
//        return allOf(result);
//    }
}
