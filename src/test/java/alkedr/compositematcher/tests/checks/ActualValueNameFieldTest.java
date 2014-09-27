package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.beans.SimpleBean;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getActualValueName;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getIsSuccessful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ActualValueNameFieldTest {
    @Test
    public void actualValueNameFieldInChecksShouldBeCorrect() {
        assertThat(getActualValueName(simpleBeanMatcher(1, "1"), new SimpleBean(1, "1")), contains("intField", "stringField"));
    }
}
