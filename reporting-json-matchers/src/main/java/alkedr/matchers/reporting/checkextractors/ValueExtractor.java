package alkedr.matchers.reporting.checkextractors;

public interface ValueExtractor {
    Object extract(Class<?> clazz, Object actual);
}
