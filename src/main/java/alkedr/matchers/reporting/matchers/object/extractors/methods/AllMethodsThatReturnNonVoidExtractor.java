package alkedr.matchers.reporting.matchers.object.extractors.methods;

import alkedr.matchers.reporting.checks.ExtractedValue;
import alkedr.matchers.reporting.matchers.ValueExtractor;
import alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AllMethodsThatReturnNonVoidExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<? super T> tClass;

    public AllMethodsThatReturnNonVoidExtractor(Class<? super T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(Object item) {   // FIXME: what if getter throws?
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (final Method method : item == null ? tClass.getMethods() : item.getClass().getMethods()) {
            if (isGoodGetter(method)) {
                method.setAccessible(true);
                result.add(new ValueExtractor<T>() {
                    @Override
                    public ExtractedValue extractValue(T item) {
                        try {
                            return new ExtractedValue(getterNameToPropertyName(method.getName()), item == null ? null : method.invoke(item));
                        } catch (IllegalAccessException | InvocationTargetException ignored) {
                            return new ExtractedValue(getterNameToPropertyName(method.getName()), null, ExtractedValue.Status.MISSING);
                        }
                    }
                });
            }
        }
        return result;
    }

    private static boolean isGoodGetter(Method method) {
        boolean nameIsCorrect = method.getName().startsWith("is") || method.getName().startsWith("get");
        boolean signatureIsCorrect = method.getReturnType().equals(Void.TYPE) && method.getParameterTypes().length == 0;
        boolean notGetClass = !"getClass".equals(method.getName());
        return nameIsCorrect && signatureIsCorrect && notGetClass;
    }

    private static String getterNameToPropertyName(String methodName) {
        if (methodName.length() > 3 && methodName.startsWith("get")) {
            return methodName.substring(3, 4).toLowerCase() + (methodName.length() > 4 ? methodName.substring(4) : "");
        } else if (methodName.length() > 2 && methodName.startsWith("is")) {
            return methodName.substring(2, 3).toLowerCase() + (methodName.length() > 3 ? methodName.substring(3) : "");
        } else {
            return methodName;
        }
    }
}
