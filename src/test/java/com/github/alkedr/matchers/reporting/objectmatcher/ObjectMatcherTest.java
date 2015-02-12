package com.github.alkedr.matchers.reporting.objectmatcher;

import com.github.alkedr.matchers.reporting.ObjectMatcher;
import com.github.alkedr.matchers.reporting.ValueExtractingMatcherForExtending;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static com.github.alkedr.matchers.reporting.ReportMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * User: alkedr
 * Date: 09.02.2015
 */
public class ObjectMatcherTest {

    public static class Item {
        public final Integer publicIntField = 1;

        public Integer getPublicIntField() {
            return publicIntField;
        }
    }

    private static final Item item = new Item();



    @Test
    public void field() {
        assertThat(new ObjectMatcher<>(Item.class).field("publicIntField").is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("publicIntField", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void fieldWithName() {
        assertThat(new ObjectMatcher<>(Item.class).field("blah", "publicIntField").is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("blah", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void fieldExtractor() {
        assertThat(new ObjectMatcher<>(Item.class).field("blah",
                        new ValueExtractingMatcherForExtending.ValueExtractor<Item>() {
                            @Override
                            public Object extract(@NotNull Item item) throws Exception {
                                return item.publicIntField;
                            }
                        }
                ).is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("blah", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void method() {
        assertThat(new ObjectMatcher<>(Item.class).method("getPublicIntField").is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("getPublicIntField", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void methodWithName() {
        assertThat(new ObjectMatcher<>(Item.class).method("blah", "getPublicIntField").is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("blah", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void methodExtractor() {
        assertThat(new ObjectMatcher<>(Item.class).method("blah",
                        new ValueExtractingMatcherForExtending.ValueExtractor<Item>() {
                            @Override
                            public Object extract(@NotNull Item item) throws Exception {
                                return item.getPublicIntField();
                            }
                        }
                ).is(equalTo(1)).getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("blah", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

    @Test
    public void expect() {
        ObjectMatcher<Item> matcher = new ObjectMatcher<>(Item.class);
        matcher.expect(equalTo(1)).getPublicIntField();
        assertThat(matcher.getReport(item),
                passedCompositeCheck(null, item,
                        simpleChecks(empty()),
                        compositeChecks(
                                passedCompositeCheck("getPublicIntField", item.publicIntField,
                                        simpleChecks(simpleCheck("<1>")),
                                        compositeChecks(empty())
                                )
                        )
                )
        );
    }

}
