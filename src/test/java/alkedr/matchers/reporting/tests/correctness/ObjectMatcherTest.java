package alkedr.matchers.reporting.tests.correctness;

import alkedr.matchers.reporting.ObjectMatcher;
import ch.lambdaj.Lambda;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ObjectMatcherTest {

    private final ObjectWithPropertiesOfDifferentTypes on = Lambda.on(ObjectWithPropertiesOfDifferentTypes.class);
    private final Matcher<ObjectWithPropertiesOfDifferentTypes> matcher =
            new ObjectMatcher<ObjectWithPropertiesOfDifferentTypes>()
                    .property("booleanPropertyWithFancyName", on.getBooleanProperty(), is(true))
                    .property(on.getIntProperty(), is(1))
                    .property("longPropertyWithFancyName", on.getLongProperty(), 2L)
                    .property(on.getStringProperty(), "3")
            ;


    @Test
    public void matchingWithCorrectFieldsShouldWorkCorrectly() {
        assertThat(matcher.matches(new ObjectWithPropertiesOfDifferentTypes(true, 1, 2L, "3")), is(true));
    }

    @Test
    public void matchingWithIncorrectBooleanFieldShouldWorkCorrectly() {
        assertThat(matcher.matches(new ObjectWithPropertiesOfDifferentTypes(false, 1, 2L, "3")), is(false));
    }

    @Test
    public void matchingWithIncorrectIntFieldShouldWorkCorrectly() {
        assertThat(matcher.matches(new ObjectWithPropertiesOfDifferentTypes(true, 2, 2L, "3")), is(false));
    }

    @Test
    public void matchingWithIncorrectLongFieldShouldWorkCorrectly() {
        assertThat(matcher.matches(new ObjectWithPropertiesOfDifferentTypes(true, 1, 3L, "3")), is(false));
    }

    @Test
    public void matchingWithIncorrectStringFieldShouldWorkCorrectly() {
        assertThat(matcher.matches(new ObjectWithPropertiesOfDifferentTypes(true, 1, 2L, "4")), is(false));
    }


    private static class ObjectWithPropertiesOfDifferentTypes {
        private final boolean booleanProperty;
        private final int intProperty;
        private final Long longProperty;
        private final String stringProperty;

        private ObjectWithPropertiesOfDifferentTypes(boolean booleanProperty, int intProperty, Long longProperty, String stringProperty) {
            this.booleanProperty = booleanProperty;
            this.intProperty = intProperty;
            this.longProperty = longProperty;
            this.stringProperty = stringProperty;
        }

        public boolean getBooleanProperty() {
            return booleanProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public Long getLongProperty() {
            return longProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }
    }
}
