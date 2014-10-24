package alkedr.compositematcher.tests;

import alkedr.compositematcher.beans.SimpleBean;
import ch.lambdaj.Lambda;
import org.junit.Test;

import static alkedr.compositematcher.beans.SimpleBean.simpleBean;
import static alkedr.compositematcher.beans.SimpleBean.simpleBeanMatcher;
//import static alkedr.compositematcher.matchers.forbeans.JsonBeanMatchers.*;
import static alkedr.compositematcher.utils.CheckFieldExtractionUtils.getIsSuccessful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

public class MatchesTest {
    @Test
    public void equalBeansShouldMatch() {
        assertThat(simpleBeanMatcher(1, "1").matches(simpleBean(1, "1")), is(true));
    }

    @Test
    public void notEqualBeansShouldNotMatch() {
        assertThat(simpleBeanMatcher(1, "1").matches(simpleBean(1, "2")), is(false));

//        objectWith(  // только эти поля, остальные nullValue
//                field(on.getX(), correctX()),
//                field(on.getA(), equalTo(field(on.getB()))),
//                fieldWithoutGetter("z", correctZ()),
//                fieldWithoutGetter(containsString("zzz"), correctZZZ())  // может примениться к нескольким полям
//                // отображается так: зачёркнутое actual, чеклист матчеров (зелёные галочки и красные крестики)
//        );
//
//        objectContaining( // эти и любые другие поля, остальные anything()
//        );
//
//        emptyObject();// = objectWith();
//
//
//        // паросочетание  элемент - матчер так, чтобы как можно больше заматчилось
//        arrayWith(  // только эти элементы
//                item(0, correctItem0()),
//                item(greaterThan(0), correctItem()),  // может примениться к нескольким элементам
//                item(greaterThan(5), correctItem()).count(5),  // должен примениться к 5 элементам
//                item(greaterThan(5), correctItem()).count(greaterThan(5)),  // должен примениться к >5 элементам
//                item(correctItem1()).count(5),   // индекс не важен
//                item(correctItem2()).count(greaterThan(6))
//        );
//
//        arrayContaining();  // эти элементы в этом порядке, между ними, перед ними и после них могут быть др. элементы
//
//        emptyArray();// = arrayWith();
//
//
//        mapWith(
//                entry("field1", correctField1()),
//                entry(containsString("zzz"), correctZZZ())  // может примениться к нескольким полям
//        );
//
//        mapContaining();
//
//        emptyMap();// = mapWith();


//        В отчёте фолдинг, не мешающий копированию?
    }
}
