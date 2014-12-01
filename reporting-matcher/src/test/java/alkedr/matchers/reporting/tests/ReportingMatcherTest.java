package alkedr.matchers.reporting.tests;

import alkedr.matchers.reporting.ReportingMatcher;
import alkedr.matchers.reporting.checks.CheckStatus;
import alkedr.matchers.reporting.checks.ExecutableCheck;
import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static alkedr.matchers.reporting.checks.CheckStatus.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

// TODO: тесты на матчер, бросающий исключение, BROKEN?
@RunWith(Parameterized.class)
public class ReportingMatcherTest {
    private final Object object;
    private final CheckStatus expectedStatus;
    private final List<ExecutableCheck> executableChecks;
    private final ExecutedCompositeCheck expectedExecutedCompositeCheck;

    public ReportingMatcherTest(String testName, Object object, CheckStatus expectedStatus,
                                List<ExecutableCheck> executableChecks,
                                ExecutedCompositeCheck expectedExecutedCompositeCheck) {
        this.object = object;
        this.expectedStatus = expectedStatus;
        this.executableChecks = executableChecks;
        this.expectedExecutedCompositeCheck = expectedExecutedCompositeCheck;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        Object object = new Object();

        return asList(new Object[][]{
                {
                        "0 проверок",
                        object, SKIPPED,
                        new ArrayList<ExecutableCheck>(),
                        new ExecutedCompositeCheck(object.toString()),
                },
                {
                        "1 успешная проверка",
                        object, PASSED,
                        asList(new ExecutableCheck("intField", 1, asList(equalTo(1)))),
                        new ExecutedCompositeCheck(
                                object.toString(),
                                PASSED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
                                                PASSED,
                                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                                asList(new ExecutedSimpleCheck(PASSED, "<1>", null))
                                        )
                                )),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 неуспешная проверка",
                        object, FAILED,
                        asList(new ExecutableCheck("intField", 1, asList(equalTo(2)))),
                        new ExecutedCompositeCheck(
                                object.toString(),
                                FAILED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
                                                FAILED,
                                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                                asList(new ExecutedSimpleCheck(FAILED, "<2>", "was <1>"))
                                        )
                                )),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 поле без проверок",
                        object, SKIPPED,
                        asList(new ExecutableCheck("intField", 1, new ArrayList<Matcher<?>>())),
                        new ExecutedCompositeCheck(
                                object.toString(),
                                SKIPPED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
                                                SKIPPED,
                                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                                new ArrayList<ExecutedSimpleCheck>()
                                        )
                                )),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
        });
    }

    @Test
    public void test() {
        ReportingMatcher<?> matcher = new ReportingMatcher<Object>(Object.class) {
            @Override
            protected Collection<ExecutableCheck> getExecutableChecks(Class<?> clazz, Object actual) {
                return executableChecks;
            }
        };

        boolean actualMatches = matcher.matches(object);
        ExecutedCompositeCheck actualExecutedCompositeCheck = matcher.getLastCheckResult();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(actualExecutedCompositeCheck);
        String expectedJson = gson.toJson(expectedExecutedCompositeCheck);

        assertThat(actualMatches, equalTo(expectedStatus != FAILED));
        assertThat(actualJson, equalTo(expectedJson));
    }
}
