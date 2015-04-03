package com.github.alkedr.matchers.reporting;

import static org.mockito.Mockito.*;

public final class ReportingMatcherVerifyingUtils {
    private ReportingMatcherVerifyingUtils() {
    }


    public interface ReportingMatcherVerifier {
        void verifyListener(ReportingMatcher.CheckListener listener);
    }


    public static void verifyMatcher(Object item, ReportingMatcher<?> matcher, ReportingMatcherVerifier verifier) {
        ReportingMatcher.CheckListener checkListenerMock = mock(ReportingMatcher.CheckListener.class);
        matcher.matches(item, checkListenerMock);
        verifier.verifyListener(checkListenerMock);
        verifyNoMoreInteractions(checkListenerMock);
    }


    public static ReportingMatcherVerifier normalValue(final String name, final Object value, final ReportingMatcherVerifier... innerVerifiers) {
        return new ReportingMatcherVerifier() {
            @Override
            public void verifyListener(ReportingMatcher.CheckListener listener) {
                verify(listener).onStartValue(name, value);
                for (ReportingMatcherVerifier innerVerifier : innerVerifiers) innerVerifier.verifyListener(listener);
                verify(listener).onEndValue();
            }
        };
    }

    public static ReportingMatcherVerifier brokenValue(final String name, final Exception extractionException, final ReportingMatcherVerifier... innerVerifiers) {
        return new ReportingMatcherVerifier() {
            @Override
            public void verifyListener(ReportingMatcher.CheckListener listener) {
                verify(listener).onStartBrokenValue(name, extractionException);
                for (ReportingMatcherVerifier innerVerifier : innerVerifiers) innerVerifier.verifyListener(listener);
                verify(listener).onEndValue();
            }
        };
    }

    public static ReportingMatcherVerifier missingValue(final String name, final ReportingMatcherVerifier... innerVerifiers) {
        return new ReportingMatcherVerifier() {
            @Override
            public void verifyListener(ReportingMatcher.CheckListener listener) {
                verify(listener).onStartMissingValue(name);
                for (ReportingMatcherVerifier innerVerifier : innerVerifiers) innerVerifier.verifyListener(listener);
                verify(listener).onEndValue();
            }
        };
    }

    public static ReportingMatcherVerifier unexpectedValue(final String name, final Object value, final ReportingMatcherVerifier... innerVerifiers) {
        return new ReportingMatcherVerifier() {
            @Override
            public void verifyListener(ReportingMatcher.CheckListener listener) {
                verify(listener).onStartUnexpectedValue(name, value);
                for (ReportingMatcherVerifier innerVerifier : innerVerifiers) innerVerifier.verifyListener(listener);
                verify(listener).onEndValue();
            }
        };
    }

    public static ReportingMatcherVerifier matcher(final String matcherDescription, final String mismatchDescription) {
        return new ReportingMatcherVerifier() {
            @Override
            public void verifyListener(ReportingMatcher.CheckListener listener) {
                verify(listener).onMatcher(matcherDescription, mismatchDescription);
            }
        };
    }
}
