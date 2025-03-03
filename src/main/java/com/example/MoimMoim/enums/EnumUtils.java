package com.example.MoimMoim.enums;

import java.lang.reflect.Method;
import java.util.Arrays;

public class EnumUtils {
    // return type은 Enum을 상속받은 것만, 매개변수는, enumClass와, label
    public static <T extends Enum<T>> T fromLabel(Class<T> enumClass, String label) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> {
                    try {
                        Method method;
                        try {
                            method = enumClass.getMethod("getLabel");
                        } catch (NoSuchMethodException ex) {
                            method = enumClass.getMethod("getDescription");
                        }
                        return method.invoke(e).equals(label);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException(
                                "Enum must have either 'getLabel' or 'getDescription' method: " + enumClass.getSimpleName()
                        );
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown label: " + label));
    }
}
