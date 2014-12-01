package alkedr.matchers.reporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static alkedr.matchers.reporting.CompositeMatcher2.CheckStatus.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CompositeMatcherTest {
    private static final Object OBJECT = new Object();
    private final CompositeMatcher2<Object> matcher;
    private final CompositeMatcher2.ExecutedCompositeCheck expectedExecutedCompositeCheck;

    public CompositeMatcherTest(String testName, CompositeMatcher2<Object> matcher,
                                CompositeMatcher2.ExecutedCompositeCheck expectedExecutedCompositeCheck) {
        this.matcher = matcher;
        this.expectedExecutedCompositeCheck = expectedExecutedCompositeCheck;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {
                        "0 проверок",
                        new CompositeMatcher2<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                            }
                        },
                        new CompositeMatcher2.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                SKIPPED,
                                new ArrayList<Map.Entry<String, CompositeMatcher2.ExecutedCompositeCheck>>(),
                                new ArrayList<CompositeMatcher2.ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 успешная проверка поля",
                        new CompositeMatcher2<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                checkThat("intField", 1, equalTo(1));
                            }
                        },
                        new CompositeMatcher2.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                PASSED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher2.ExecutedCompositeCheck(
                                                "1",
                                                PASSED,
                                                new ArrayList<Map.Entry<String, CompositeMatcher2.ExecutedCompositeCheck>>(),
                                                asList(new CompositeMatcher2.ExecutedSimpleCheck(PASSED, "<1>", null))
                                        )
                                )),
                                new ArrayList<CompositeMatcher2.ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 неуспешная проверка поля",
                        new CompositeMatcher2<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                checkThat("intField", 1, equalTo(2));
                            }
                        },
                        new CompositeMatcher2.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                FAILED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher2.ExecutedCompositeCheck(
                                                "1",
                                                FAILED,
                                                new ArrayList<Map.Entry<String, CompositeMatcher2.ExecutedCompositeCheck>>(),
                                                asList(new CompositeMatcher2.ExecutedSimpleCheck(FAILED, "<2>", "was <1>"))
                                        )
                                )),
                                new ArrayList<CompositeMatcher2.ExecutedSimpleCheck>()
                        ),
                },
                {
                        "1 поле без проверок",
                        new CompositeMatcher2<Object>() {
                            @Override
                            protected void check(@Nullable Object actualValue) {
                                ensureFieldExists("intField", 1);
                            }
                        },
                        new CompositeMatcher2.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                SKIPPED,
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher2.ExecutedCompositeCheck(
                                                "1",
                                                SKIPPED,
                                                new ArrayList<Map.Entry<String, CompositeMatcher2.ExecutedCompositeCheck>>(),
                                                new ArrayList<CompositeMatcher2.ExecutedSimpleCheck>()
                                        )
                                )),
                                new ArrayList<CompositeMatcher2.ExecutedSimpleCheck>()
                        ),
                },
        });
    }

    @Test
    public void test() {
        boolean actualMatches = matcher.matches(OBJECT);
        CompositeMatcher2.ExecutedCompositeCheck actualExecutedCompositeCheck = matcher.getLastCheckResult();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(actualExecutedCompositeCheck);
        String expectedJson = gson.toJson(expectedExecutedCompositeCheck);

        assertThat(actualJson, equalTo(expectedJson));
        assertThat(actualMatches, equalTo(expectedExecutedCompositeCheck.getStatus().isSuccessful()));
    }
}
