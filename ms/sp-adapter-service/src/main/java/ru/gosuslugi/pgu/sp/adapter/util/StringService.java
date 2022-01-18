package ru.gosuslugi.pgu.sp.adapter.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public class StringService {

    public String printWithDelimiter(String delimiter, Object... objects) {
        return Stream.of(objects)
            .filter(obj -> !isNull(obj))
            .map(Object::toString)
            .filter(str -> !str.isBlank())
            .collect(Collectors.joining(delimiter));
    }

    /**
     * LeftJoin - dsdjlbn cnhjre ока левые элементы присутствуют
     * @param delimiter delimiter
     * @param objects objects
     * @return String
     */
    public String printLeftJoinWithDelimiter(String delimiter, Object... objects) {
        AtomicBoolean leftExisted = new AtomicBoolean(true);
        return Stream.of(objects)
            .filter(
                obj -> {
                    leftExisted.set(leftExisted.get() && !isNull(obj));
                    return leftExisted.get();
                }
            )
            .map(Object::toString)
            .filter(
                str -> {
                    leftExisted.set(leftExisted.get() && !str.isBlank());
                    return leftExisted.get();
                }
            )
            .collect(Collectors.joining(delimiter));
    }
}
