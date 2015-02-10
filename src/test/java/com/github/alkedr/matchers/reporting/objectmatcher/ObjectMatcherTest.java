package com.github.alkedr.matchers.reporting.objectmatcher;

import com.github.alkedr.matchers.reporting.ObjectMatcher;
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

        public int getPublicIntField() {
            return publicIntField;
        }
    }

    private static final Item item = new Item();



    @Test
    public void fieldName() {
        assertThat(new ObjectMatcher<Item>().field("publicIntField").is(equalTo(1)).getReport(item),
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
    public void fieldName2() {
        assertThat(new ObjectMatcher<Item>().field("blah", "publicIntField").is(equalTo(1)).getReport(item),
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

}
