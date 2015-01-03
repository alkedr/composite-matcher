package com.github.alkedr.matchers.reporting.matchers.object.extractors.methods;

import com.github.alkedr.matchers.reporting.matchers.ValueExtractor;
import com.github.alkedr.matchers.reporting.matchers.ValueExtractorsExtractor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GettersExtractor<T> implements ValueExtractorsExtractor<T> {
    private final Class<? super T> tClass;

    public GettersExtractor(Class<? super T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<T>> extractValueExtractors(@Nullable T item) {
        List<ValueExtractor<T>> result = new ArrayList<>();
        for (Method method : tClass.getMethods()) {
            if (isGoodGetter(method)) {  // TODO: construct Argument and use LambdajArgumentExtractor?
                result.add(new MethodExtractor<T>(getterNameToPropertyName(method.getName()), method));
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
