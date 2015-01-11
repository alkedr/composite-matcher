package com.github.alkedr.matchers.reporting.extractors.object;

import com.github.alkedr.matchers.reporting.extractors.ValueExtractor;
import com.github.alkedr.matchers.reporting.extractors.ValueExtractorsExtractor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GettersExtractor<FromType> implements ValueExtractorsExtractor<FromType, Object> {
    private final Class<? super FromType> tClass;

    public GettersExtractor(Class<? super FromType> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<ValueExtractor<FromType, Object>> extractValueExtractors(@Nullable FromType item) {
        List<ValueExtractor<FromType, Object>> result = new ArrayList<>();
        for (Method method : tClass.getMethods()) {
            if (isGoodGetter(method)) {  // TODO: construct Argument and use LambdajArgumentExtractor?
                result.add(new MethodExtractor<FromType, Object>(getterNameToPropertyName(method.getName()), method));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
