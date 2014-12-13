package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableCollection;
import static org.hamcrest.Matchers.any;

/**
 * Учитывает порядок элементов
 */
public class IterableMatcher<T extends Iterable<U>, U> extends ValueExtractingMatcher<T> {
    private final Collection<ExecutableCheckExtractor> executableCheckExtractors = new ArrayList<>();

    public IterableMatcher(Class<T> tClass) {
        super(tClass);
    }


    public IterableMatcher<T, U> elementWithIndex(Matcher<Integer> indexMatcher/*, Matcher<U> elementMatcher*/) {
        return this;
    }

    public IterableMatcher<T, U> elementWithIndex(int index/*, Matcher<U> elementMatcher*/) {
        return this;
    }

    public IterableMatcher<T, U> element(Matcher<U> elementMatcher) {
        return this;
    }

//    public IterableMatcher<T, U> allElements(Matcher<T> elementMatcher) {
//        return elementWithIndex(any(Integer.class)).is(elementMatcher);
//    }

    public IterableMatcher<T, U> size(Matcher<Integer> sizeMatcher) {
        return this;
    }


    @Override
    protected Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual) {
        return unmodifiableCollection(executableCheckExtractors);
    }
}
