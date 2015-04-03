package com.github.alkedr.matchers.reporting.map;

import com.github.alkedr.matchers.reporting.ReportingMatcher;

public class MapMatcherTest {



    public static void checkMyBean(MyBean myBean, ReportingMatcher.CheckListener checker) {
        assertAndReportThat(myBean, BeanMatchers::correctBean);


        object(checker, myBean)
                .field()
                .method()
                ;
    }

    public static Matcher<>

}
