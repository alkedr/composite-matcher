package com.github.alkedr.matchers.reporting.object;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.github.alkedr.matchers.reporting.ReportingMatcherVerifyingUtils.*;
import static com.github.alkedr.matchers.reporting.ReportingMatchers.object;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class ObjectMatcherFieldNormalTest {
    private static final Item ITEM = new Item();

    private final String fieldName;
    private final Field field;

    public ObjectMatcherFieldNormalTest(String fieldName) throws NoSuchFieldException {
        this.fieldName = fieldName;
        field = Item.class.getDeclaredField(fieldName);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{{"publicField"}, {"privateField"}, {"publicStaticField"}});
    }


    @Test
    public void name() {
        verifyMatcher(ITEM,
                object(Item.class).field(fieldName).is(equalTo(1)),
                normalValue(fieldName, 1, matcher("<1>", null))
        );
    }

    @Test
    public void nameWithAlias() {
        verifyMatcher(ITEM,
                object(Item.class).field("blah", fieldName).is(equalTo(1)),
                normalValue("blah", 1, matcher("<1>", null))
        );
    }

    @Test
    public void reflectField() {
        verifyMatcher(ITEM,
                object(Item.class).field(field).is(equalTo(1)),
                normalValue(fieldName, 1, matcher("<1>", null))
        );
    }

    @Test
    public void reflectFieldWithAlias() {
        verifyMatcher(ITEM,
                object(Item.class).field("blah", field).is(equalTo(1)),
                normalValue("blah", 1, matcher("<1>", null))
        );
    }

    // TODO: private поле private inner класса
    // TODO: поле из суперкласса


    public static class Item {
        public final int publicField = 1;
        private final int privateField = 1;
        public static final int publicStaticField = 1;
    }
}
