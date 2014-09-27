package alkedr.compositematcher.tests;

import alkedr.compositematcher.beans.SimpleBean;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBean;
import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getIsSuccessful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

public class MatchesTest {
    @Test
    public void equalBeansShouldMatch() {
        assertThat(simpleBeanMatcher(1, "1").matches(simpleBean(1, "1")), is(true));
    }

    @Test
    public void notEqualBeansShouldNotMatch() {
        assertThat(simpleBeanMatcher(1, "1").matches(simpleBean(1, "2")), is(false));
    }
}
