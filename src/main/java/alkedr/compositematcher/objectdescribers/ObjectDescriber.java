package alkedr.compositematcher.objectdescribers;

public interface ObjectDescriber {
    // Возвращает описание объекта (получает его из toString, аннотаций и т. д.)
    // Может вернуть null, тогда будет использовано название поля
    String describe(Object o);
}
