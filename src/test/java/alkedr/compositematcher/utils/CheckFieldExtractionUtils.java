package alkedr.compositematcher.utils;

import alkedr.compositematcher.CompositeMatcher;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public final class CheckFieldExtractionUtils {

    public static <T> List<Boolean> getIsSuccessful(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).isSuccessful());
    }

    public static <T> List<String> getActualValueName(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).getActualValueName());
    }

    public static <T> List<?> getActualValue(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).getActualValue());
    }

    public static <T> List<String> getMatcherDescription(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).getMatcherDescription());
    }

    public static <T> List<String> getMismatchDescription(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).getMismatchDescription());
    }

    public static <T> List<List> getInnerChecks(CompositeMatcher<T> matcher, T simpleBean) {
        return getChecksField(matcher, simpleBean, on(CompositeMatcher.Check.class).getInnerChecks());
    }

    private static <T, U> List<U> getChecksField(CompositeMatcher<T> matcher, T simpleBean, U field) {
        matcher.matches(simpleBean);
        return extract(matcher.getChecks(), field);
    }

}
