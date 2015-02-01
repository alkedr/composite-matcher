package com.github.alkedr.matchers.reporting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ReportingMatcher.ExecutedCheck.Status.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ReportingMatcherMatchesReturnValueTest {
    @Parameterized.Parameter(0) public ReportingMatcher.ExecutedCheck.Status status;
    @Parameterized.Parameter(1) public boolean expectedMatchesReturnValue;

    @Parameterized.Parameters(name = "status={0} matches={1}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {UNCHECKED, true},
                {PASSED, true},
                {FAILED, false},
        });
    }

    @Test
    public void test() {
        ReportingMatcher.ExecutedCompositeCheck check = mock(ReportingMatcher.ExecutedCompositeCheck.class);
        when(check.getStatus()).thenReturn(status);

        ReportingMatcher<Object> reportingMatcher = (ReportingMatcher<Object>)mock(ReportingMatcher.class);
        when(reportingMatcher.getReport(any())).thenReturn(check);
        when(reportingMatcher.matches(any())).thenCallRealMethod();

        assertThat(reportingMatcher.matches(null), is(expectedMatchesReturnValue));
    }
}
