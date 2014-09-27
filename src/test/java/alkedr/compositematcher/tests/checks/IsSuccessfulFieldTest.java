package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.beans.ComplexBean;
import alkedr.compositematcher.beans.SimpleBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static alkedr.compositematcher.beans.ComplexBean.complexBean;
import static alkedr.compositematcher.beans.ComplexBean.complexBeanMatcher;
import static alkedr.compositematcher.beans.SimpleBean.simpleBean;
import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getIsSuccessful;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class IsSuccessfulFieldTest {
    @Test
    public void shouldBeTrueWhenChecksPass() {
        assertThat(getIsSuccessful(simpleBeanMatcher(1, "1"), simpleBean(1, "1")), contains(true, true));
    }

    @Test
    public void shouldBeFalseWhenChecksFail() {
        assertThat(getIsSuccessful(simpleBeanMatcher(1, "1"), simpleBean(2, "2")), contains(false, false));
    }


    @Test
    public void shouldBeTrueWhenInnerChecksPass() {
        assertThat(getIsSuccessful(complexBeanMatcher(1, "1"), complexBean(1, "1")), contains(true));
    }

    @Test
    public void shouldBeFalseWhenInnerChecksFail() {
        assertThat(getIsSuccessful(complexBeanMatcher(1, "1"), complexBean(2, "2")), contains(false));
    }
}
