package com.github.alkedr.matchers.reporting.object;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.github.alkedr.matchers.reporting.ReportingMatchers.object;
import static org.hamcrest.Matchers.containsString;

public class ObjectMatcherFieldInvalidTest {
    private static final String FIELD_NAME_THAT_DOES_NOT_EXIST = "blah";

    @Rule public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void illegalArgumentExceptionIsThrownIfFieldIsNotFound() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString(FIELD_NAME_THAT_DOES_NOT_EXIST));
        object(Object.class).field(FIELD_NAME_THAT_DOES_NOT_EXIST);
    }
}
