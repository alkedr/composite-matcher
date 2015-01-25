package com.github.alkedr.matchers.reporting;

import java.util.Collection;

public interface CollectionMatcherForImplementing<T, U extends CollectionMatcherForImplementing<T, U>>
        extends ValueExtractingMatcherForImplementing<Collection<T>, U> {
}
