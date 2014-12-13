package alkedr.matchers.reporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CompositeMatcherTest {
    private static final Object OBJECT = new Object();
    private final CompositeMatcher<Object> matcher;
    private final CompositeMatcher.ExecutedCompositeCheck expectedExecutedCompositeCheck;

    public CompositeMatcherTest(String testName, CompositeMatcher<Object> matcher,
                                CompositeMatcher.ExecutedCompositeCheck expectedExecutedCompositeCheck) {
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
                        new CompositeMatcher.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                new ArrayList<Map.Entry<String, CompositeMatcher.ExecutedCompositeCheck>>(),
                                new ArrayList<CompositeMatcher.ExecutedSimpleCheck>()
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
                        new CompositeMatcher.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher.ExecutedCompositeCheck(
                                                "1",
                                                new ArrayList<Map.Entry<String, CompositeMatcher.ExecutedCompositeCheck>>(),
                                                asList(new CompositeMatcher.ExecutedSimpleCheck("<1>", null))
                                        )
                                )),
                                new ArrayList<CompositeMatcher.ExecutedSimpleCheck>()
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
                        new CompositeMatcher.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher.ExecutedCompositeCheck(
                                                "1",
                                                new ArrayList<Map.Entry<String, CompositeMatcher.ExecutedCompositeCheck>>(),
                                                asList(new CompositeMatcher.ExecutedSimpleCheck("<2>", "was <1>"))
                                        )
                                )),
                                new ArrayList<CompositeMatcher.ExecutedSimpleCheck>()
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
                        new CompositeMatcher.ExecutedCompositeCheck(
                                OBJECT.toString(),
                                asList(new AbstractMap.SimpleEntry<>(
                                        "intField",
                                        new CompositeMatcher.ExecutedCompositeCheck(
                                                "1",
                                                new ArrayList<Map.Entry<String, CompositeMatcher.ExecutedCompositeCheck>>(),
                                                new ArrayList<CompositeMatcher.ExecutedSimpleCheck>()
                                        )
                                )),
                                new ArrayList<CompositeMatcher.ExecutedSimpleCheck>()
                        ),
                },
        });
    }

    @Test
    public void test() {
        boolean actualMatches = matcher.matches(OBJECT);
        CompositeMatcher.ExecutedCompositeCheck actualExecutedCompositeCheck = matcher.getLastCheckResult();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(actualExecutedCompositeCheck);
        String expectedJson = gson.toJson(expectedExecutedCompositeCheck);

        assertThat(actualJson, equalTo(expectedJson));
        assertThat(actualMatches, equalTo(expectedExecutedCompositeCheck.getStatus().isSuccessful()));
    }
}
