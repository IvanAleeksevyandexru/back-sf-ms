package ru.gosuslugi.pgu.generator.util;

import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class MapUtils {


    public static Map<String, String> linkedMapOf(String... args) {
        Map<String, String> map = new LinkedHashMap<>();
        if ((args.length & 1) != 0) {
            throw new IllegalArgumentException("Incorrect arguments count, should by even");
        }
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }

    public static Map<String, Object> linkedMapOfObjects(Object... args) {
        Map<String, Object> map = new LinkedHashMap<>();
        if ((args.length & 1) != 0) {
            throw new IllegalArgumentException("Incorrect arguments count, should by even");
        }
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], args[i + 1]);
        }
        return map;
    }


}
