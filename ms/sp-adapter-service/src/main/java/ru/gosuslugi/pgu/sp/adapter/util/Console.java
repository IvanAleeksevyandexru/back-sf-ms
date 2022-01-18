package ru.gosuslugi.pgu.sp.adapter.util;

public interface Console {

    void log(String string);

    void log(String template, Object... objects);

}
