package alkedr.matchers.reporting.reporters.objectvisitors;

import java.util.Map;

/**
 * User: alkedr
 * Date: 26.12.2014
 */
public abstract class ObjectVisitorForReporters implements ObjectVisitor {
    @Override
    public void onObject(Object object) {
    }

    public abstract void onMap(Map<Object, Object> object);

    @Override
    public void onObject(Object object) {
    }

    @Override
    public void onObject(Object object) {
    }
}
