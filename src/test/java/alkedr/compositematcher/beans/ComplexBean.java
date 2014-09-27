package alkedr.compositematcher.beans;

import alkedr.compositematcher.CompositeMatcher;

import static alkedr.compositematcher.beans.SimpleBean.simpleBean;
import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Author: alkedr
 * Date: 9/28/14.
 */
public class ComplexBean {
    public final SimpleBean simpleBeanField;

    public ComplexBean(SimpleBean simpleBeanField) {
        this.simpleBeanField = simpleBeanField;
    }


    public static ComplexBean complexBean(SimpleBean simpleBeanField) {
        return new ComplexBean(simpleBeanField);
    }

    public static ComplexBean complexBean(int intField, String stringField) {
        return new ComplexBean(new SimpleBean(intField, stringField));
    }


    public static CompositeMatcher<ComplexBean> complexBeanMatcher(final SimpleBean simpleBeanField) {
        return new CompositeMatcher<ComplexBean>() {
            @Override
            protected void check(ComplexBean item) {
                checkThat(item.simpleBeanField, is(simpleBeanMatcher(simpleBeanField)));
            }
        };
    }

    public static CompositeMatcher<ComplexBean> complexBeanMatcher(int intField, String stringField) {
        return complexBeanMatcher(simpleBean(intField, stringField));
    }
}
