package com.github.alkedr.matchers.reporting.objectmatcher;

import com.github.alkedr.matchers.reporting.ifaces.ObjectMatcher;
import com.github.alkedr.matchers.reporting.ifaces.ObjectMatcherForExtending;
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

    private static final Item ITEM = new Item();



    private static void check(ObjectMatcher<Item> matcher, String valueName) {
        assertThat(matcher.getReport(ITEM),
                passedCompositeCheck(null, ITEM,
                        passedCompositeCheck(valueName, ITEM.publicIntField,
                                passedSimpleCheck("<1>")
                        )
                )
        );
    }


    @Test
    public void field() {
        check(new ObjectMatcherForExtending<>(Item.class, "").field("publicIntField").is(equalTo(1)), "publicIntField");
    }

    @Test
    public void fieldWithName() {
        check(new ObjectMatcher<>(Item.class).field("blah", "publicIntField").is(equalTo(1)), "blah");
    }

//    @Test
//    public void fieldExtractor() {
//        check(new ObjectMatcherForExtending<>(Item.class, "").field("blah",
//                new ValueExtractingMatcherForExtending.ValueExtractor<Item>() {
//                    @Override
//                    public Object extract(@NotNull Item item) {
//                        return item.publicIntField;
//                    }
//                }
//        ).is(equalTo(1)), "blah");
//    }

    // TODO: field(Field), field(name, Field)
    // TODO: несуществующее поле
    // TODO: private поле
    // TODO: private поле private inner класса
    // TODO: static поле



    @Test
    public void method() {
        check(new ObjectMatcher<>(Item.class).method("getPublicIntField").is(equalTo(1)), "getPublicIntField()");
    }

    @Test
    public void methodWithName() {
        check(new ObjectMatcher<>(Item.class).method("blah", "getPublicIntField").is(equalTo(1)), "blah");
    }

    @Test
    public void methodExtractor() {
        check(new ObjectMatcher<>(Item.class).method(
                new ValueExtractingMatcherForExtending.ValueExtractor<Item>() {
                    @Override
                    public Object extract(@NotNull Item item) {
                        return item.getPublicIntField();
                    }
                }
        ).is(equalTo(1)), "getPublicIntField()");
    }

    @Test
    public void methodExtractorWithName() {
        check(new ObjectMatcher<>(Item.class).method("blah",
                new ValueExtractingMatcherForExtending.ValueExtractor<Item>() {
                    @Override
                    public Object extract(@NotNull Item item) {
                        return item.getPublicIntField();
                    }
                }
        ).is(equalTo(1)), "blah");
    }

    // TODO: method(Method), method(name, Method)
    // TODO: несуществующий метод
    // TODO: private метод
    // TODO: private метод private inner класса
    // TODO: static метод



    @Test
    public void expect() {
        ObjectMatcher<Item> matcher = new ObjectMatcher<>(Item.class);
        matcher.expect(equalTo(1)).getPublicIntField();
        check(matcher, "getPublicIntField");
    }

    // TODO: тесты на некорректное использование expect()
    // TODO: примитиный тип

    // TODO: property()
    // TODO: property(null)
    // TODO: property(пропертя от другого класса)

}
