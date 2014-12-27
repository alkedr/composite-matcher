package alkedr.matchers.reporting.reporters;

public interface ValuesEnumerator<T> {
    void enumerateValues(T t, Functor functor);

    interface Functor {
        void call(String key, Object value);
    }
}
