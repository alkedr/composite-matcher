package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.CompositeMatcher;
import alkedr.compositematcher.beans.SimpleBean;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getActualValueName;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getIsSuccessful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class ActualValueNameFieldTest {
    @Test
    public void shouldBeCorrectWhenNameIsRecursivelyFoundInFieldsOfActualValue() {
        assertThat(getActualValueName(simpleBeanMatcher(1, "1"), new SimpleBean(1, "1")), contains("intField", "stringField"));
    }

    @Test
    public void shouldBeCorrectWhenNameIsSetUsingThreeArgumentVersionOfCheckThat() {
        assertThat(getActualValueName(new CompositeMatcher<SimpleBean>() {
            @Override
            protected void check(SimpleBean item) {
                checkThat("int field", item.intField, is(equalTo(1)));
                checkThat("string field", item.stringField, is(equalTo("1")));
            }
        }, new SimpleBean(1, "1")), contains("int field", "string field"));
    }
}
