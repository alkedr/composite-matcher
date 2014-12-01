package alkedr.matchers.reporting;

import alkedr.matchers.reporting.checkextractors.ExecutableCheckExtractor;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.any;

/**
 * Не учитывает порядок элементов по умолчанию
 * Проверяет, что элементы, заданные с пом. методов element и elements встречаются в actual в любом порядке
 * TODO: ограничение кол-ва элементов, т. к. Iterable может быть бесконечным
 */
public class UnorderedIterableMatcher<T extends Iterable<U>, U> extends ValueExtractingMatcher<T> {
    public UnorderedIterableMatcher(Class<T> tClass) {
        super(tClass);
    }


    public UnorderedIterableMatcher<T, U> elementWithIndex(Matcher<Integer> indexMatcher, Matcher<U> elementMatcher) {
        return this;
    }

    public UnorderedIterableMatcher<T, U> elementWithIndex(int index, Matcher<U> elementMatcher) {
        return this;
    }

    public UnorderedIterableMatcher<T, U> element(Matcher<U> elementMatcher) {
        return this;
    }

    public UnorderedIterableMatcher<T, U> allElements(Matcher<U> elementMatcher) {
        return elementWithIndex(any(Integer.class), elementMatcher);
    }

    public UnorderedIterableMatcher<T, U> size(Matcher<Integer> sizeMatcher) {
        return this;
    }




    @Override
    protected Collection<ExecutableCheckExtractor> getExecutableCheckExtractors(Class<?> clazz, Object actual) {
        return null;
    }
}
