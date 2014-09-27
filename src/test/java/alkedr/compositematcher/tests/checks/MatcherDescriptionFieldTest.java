package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.beans.SimpleBean;
import org.hamcrest.Matchers;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getActualValue;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getMatcherDescription;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class MatcherDescriptionFieldTest {
    @Test
    public void matcherDescriptionFieldInChecksShouldBeCorrect() {
        assertThat(getMatcherDescription(simpleBeanMatcher(1, "1"), new SimpleBean(2, "2")), contains("is <1>", "is \"1\""));
    }
}
