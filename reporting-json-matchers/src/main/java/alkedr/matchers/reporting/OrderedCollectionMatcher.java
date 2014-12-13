package alkedr.matchers.reporting;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.any;

/**
 * Учитывает порядок элементов
 * Проверяет, что элементы, заданные с пом. методов element и elements встречаются в actual в том порядке, в котором они были заданы
 *
 * С пом. fluent API любому элементу можно задать сколько угодно матчеров
 */
public class OrderedCollectionMatcher<T extends Collection<U>, U> extends CompositeMatcher<T> {
    private final Collection<ElementGroup<U>> elementGroups = new ArrayList<>();

    public OrderedCollectionMatcher<T, U> firstElementIs(Matcher<U> elementMatcher) {
        elementGroups.add(new ElementGroup<U>(elementMatcher) {
            @Override
            protected boolean shouldCheckElement(Collection<U> actual, int elementIndex) {
                return elementIndex == 0;
            }
        });
        return this;
    }

    public OrderedCollectionMatcher<T, U> lastElementIs(Matcher<U> elementMatcher) {
        elementGroups.add(new ElementGroup<U>(elementMatcher) {
            @Override
            protected boolean shouldCheckElement(Collection<U> actual, int elementIndex) {
                return elementIndex == actual.size()-1;
            }
        });
        return this;
    }

    public OrderedCollectionMatcher<T, U> allElementsAre(Matcher<U> elementMatcher) {
        elementGroups.add(new ElementGroup<U>(elementMatcher) {
            @Override
            protected boolean shouldCheckElement(Collection<U> actual, int elementIndex) {
                return true;
            }
        });
        return this;
    }

    public GroupWithMultipleElementsBuilder first(Matcher<U> elementMatcher) {
        return new GroupWithMultipleElementsBuilder();
    }


    public static class GroupWithMultipleElementsBuilder<T extends Collection<U>, U> {
//        public OrderedCollectionMatcher<T, U> elementsAre(Matcher<U> elementMatcher) {
//
//        }
    }




    public OrderedCollectionMatcher<T, U> elements(int count, Matcher<U> elementMatcher) {
//        elementGroups.add(new ElementGroup<>(count, elementMatcher));
        return this;
    }

    public OrderedCollectionMatcher<T, U> elements(Matcher<U> elementMatcher) {
        return this;
//        return elements(any(Integer.class), elementMatcher);
    }

    public OrderedCollectionMatcher<T, U> element(Matcher<U> elementMatcher) {
        return elements(1, elementMatcher);
    }




    @Override
    protected void check(@Nullable T actualValue) {

    }

//    .firstElementIs(matcher);
//    .first(x).elementsAre(matcher);
//    .element(x).is(matcher);
//    .elementsFrom(x).to(y).are(matcher);
//    .lastElementIs(matcher);
//    .last(x).elementsAre(matcher);
//
//    .elementsWithIndex(indexMatcher).are(matcher);
//    .elementsThatAre(elementMatcher1).alsoAre(elementMatcher2);
//    .allElementsAre(matcher);
//    .sizeIs(matcher);
    // TODO: не забыть про ситуации типа .first(5).elementsAre(matcher);, но элементов меньше чем 5


    private abstract static class ElementGroup<U> {
        private final Matcher<U> matcher;

        private ElementGroup(Matcher<U> matcher) {
            this.matcher = matcher;
        }

        protected abstract boolean shouldCheckElement(Collection<U> actual, int elementIndex);
    }
}
