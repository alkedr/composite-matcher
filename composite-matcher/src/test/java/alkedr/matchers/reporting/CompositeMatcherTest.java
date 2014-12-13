package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checks.ExecutedCompositeCheck;
import alkedr.matchers.reporting.checks.ExecutedSimpleCheck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CompositeMatcherTest {
    private static final Object OBJECT = new Object();
    private final CompositeMatcher<Object> matcher;
    private final ExecutedCompositeCheck expectedExecutedCompositeCheck;

    public CompositeMatcherTest(String testName, CompositeMatcher<Object> matcher,
                                ExecutedCompositeCheck expectedExecutedCompositeCheck) {
        this.matcher = matcher;
        this.expectedExecutedCompositeCheck = expectedExecutedCompositeCheck;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {
                        "0 проверок",
                        new CompositeMatcher<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                            }
                        },
                        new ExecutedCompositeCheck(
                                OBJECT.toString(),
                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 успешная проверка поля",
                        new CompositeMatcher<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                checkThat("intField", 1, equalTo(1));
                            }
                        },
                        new ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
                                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                                asList(new ExecutedSimpleCheck("<1>", null))
                                        )
                                )),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 неуспешная проверка поля",
                        new CompositeMatcher<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                checkThat("intField", 1, equalTo(2));
                            }
                        },
                        new ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
                                                new ArrayList<Map.Entry<String, ExecutedCompositeCheck>>(),
                                                asList(new ExecutedSimpleCheck("<2>", "was <1>"))
                                        )
                                )),
                                new ArrayList<ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 поле без проверок",
                        new CompositeMatcher<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                checkThat("intField", 1);
                            }
                        },
                        new ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new ExecutedCompositeCheck(
                                                "1",
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
        boolean actualMatches = matcher.matches(OBJECT);
        ExecutedCompositeCheck actualExecutedCompositeCheck = matcher.getLastCheckResult();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(actualExecutedCompositeCheck);
        String expectedJson = gson.toJson(expectedExecutedCompositeCheck);

        assertThat(actualJson, equalTo(expectedJson));
        assertThat(actualMatches, equalTo(expectedExecutedCompositeCheck.getStatus().isSuccessful()));
    }
}
