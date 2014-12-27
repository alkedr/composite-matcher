package alkedr.matchers.reporting.reporters.objectvisitors;

public interface ObjectVisitor {
    void onObject(String key, Object value);
}
