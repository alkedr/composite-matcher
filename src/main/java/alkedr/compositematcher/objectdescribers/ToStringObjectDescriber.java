package alkedr.compositematcher.objectdescribers;

/**
 * Author: alkedr
 * Date: 9/28/14.
 */
public class ToStringObjectDescriber implements ObjectDescriber {
    @Override
    public String describe(Object o) {
        return o.toString();
    }
}
