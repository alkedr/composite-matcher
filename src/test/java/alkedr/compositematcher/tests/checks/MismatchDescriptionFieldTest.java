package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.beans.SimpleBean;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getMatcherDescription;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getMismatchDescription;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Author: alkedr
 * Date: 9/28/14.
 */
public class MismatchDescriptionFieldTest {
    @Test
    public void matcherDescriptionFieldInChecksShouldBeNullWhenChecksPass() {
        assertThat(getMismatchDescription(simpleBeanMatcher(1, "1"), new SimpleBean(1, "1")), contains(nullValue(), nullValue()));
    }

    @Test
    public void matcherDescriptionFieldInChecksShouldBeCorrectWhenChecksFail() {
        assertThat(getMismatchDescription(simpleBeanMatcher(1, "1"), new SimpleBean(2, "2")), contains("was <2>", "was \"2\""));
    }
}
