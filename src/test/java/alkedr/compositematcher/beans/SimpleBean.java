package alkedr.compositematcher.beans;

import alkedr.compositematcher.CompositeMatcher;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Author: alkedr
 * Date: 9/28/14.
 */
public class SimpleBean {
    public final int intField;
    public final String stringField;

    public SimpleBean(int intField, String stringField) {
        this.intField = intField;
        this.stringField = stringField;
    }


    public static SimpleBean simpleBean(int intField, String stringField) {
        return new SimpleBean(intField, stringField);
    }


    public static CompositeMatcher<SimpleBean> simpleBeanMatcher(SimpleBean simpleBean) {
        return simpleBeanMatcher(simpleBean.intField, simpleBean.stringField);
    }

    public static CompositeMatcher<SimpleBean> simpleBeanMatcher(final int intField, final String stringField) {
        return new CompositeMatcher<SimpleBean>() {
            @Override
            protected void check(SimpleBean item) {
                checkThat(item.intField, is(equalTo(intField)));
                checkThat(item.stringField, is(equalTo(stringField)));
            }
        };
    }
}