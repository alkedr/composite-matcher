package alkedr.compositematcher.tests.checks;

import alkedr.compositematcher.beans.SimpleBean;
import org.hamcrest.Matchers;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getActualValue;
import static org.junit.Assert.assertThat;

public class ActualValueFieldTest {
    @Test
    public void shouldBeCorrect() {
        assertThat(getActualValue(simpleBeanMatcher(1, "1"), new SimpleBean(2, "2")), Matchers.<Object>contains(2, "2"));
    }
}
