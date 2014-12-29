package alkedr.matchers.reporting.checks;

public class UncheckedValuesUtils {
/*
    @NotNull
    public Iterable<ExecutedCompositeCheck> getAllCompositeChecks() {
        sortCompositeChecks();
        mergeOrRenameCompositeChecks();

        return new Iterable<ExecutedCompositeCheck>() {
            @Override
            public Iterator<ExecutedCompositeCheck> iterator() {    // TODO: throw exception if compositeChecks were modified during iteration
                return new Iterator<ExecutedCompositeCheck>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public ExecutedCompositeCheck next() {
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




    private static final Comparator<ExecutedCompositeCheck> COMPARATOR_BY_NAME = new Comparator<ExecutedCompositeCheck>() {
        @Override
        public int compare(ExecutedCompositeCheck o1, ExecutedCompositeCheck o2) {
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
*/
}
