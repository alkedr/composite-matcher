package alkedr.matchers.reporting.checks;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;

import static java.util.Collections.sort;

public class UncheckedValuesUtils {

    @NotNull
    public Iterable<ExecutedCompositeCheck2> getAllCompositeChecks() {
        sortCompositeChecks();
        mergeOrRenameCompositeChecks();

        return new Iterable<ExecutedCompositeCheck2>() {
            @Override
            public Iterator<ExecutedCompositeCheck2> iterator() {    // TODO: throw exception if compositeChecks were modified during iteration
                return new Iterator<ExecutedCompositeCheck2>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public ExecutedCompositeCheck2 next() {
                        // возвращаем очередной элемент compositeChecks
                        // если они закончились, то возвращаем очередной элемент extractedValues, пропуская элементы, которые содержались в compositeChecks
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }




    private static final Comparator<ExecutedCompositeCheck2> COMPARATOR_BY_NAME = new Comparator<ExecutedCompositeCheck2>() {
        @Override
        public int compare(ExecutedCompositeCheck2 o1, ExecutedCompositeCheck2 o2) {
            if (o1.getName() == null && o2.getName() == null) return 0;
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            return o1.getName().compareTo(o2.getName());
        }
    };

    private void sortCompositeChecks() {
        if (compositeChecks == null) return;
        sort(compositeChecks, COMPARATOR_BY_NAME);
    }

    private void mergeOrRenameCompositeChecks() {
        // TODO: merge checks with same name, rename checks with same name and value
    }

}
