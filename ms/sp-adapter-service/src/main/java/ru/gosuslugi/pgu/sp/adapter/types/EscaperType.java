package ru.gosuslugi.pgu.sp.adapter.types;

import net.minidev.json.JSONValue;
import org.apache.commons.text.StringEscapeUtils;

import java.util.function.Function;
import java.util.regex.Pattern;

public class EscaperType {
    private static final Pattern TABS = Pattern.compile("\t+");

    /** XML escaping */
    public static final EscaperType XML = new EscaperType(StringEscapeUtils::escapeXml11);

    /** JSON escaping */
    public static final EscaperType PDF = new EscaperType(str -> {
        String value = JSONValue.escape(str);
        value = value.replace("\n", "\\n").replace("\r", "\\r");
        value = TABS.matcher(value).replaceAll(" ");
        return value;
    });

    /** JSON escaping */
    public static final EscaperType PDF_ADD = new EscaperType(StringEscapeUtils::escapeXml11);


    private final Function<String, String> escaper;

    private EscaperType(Function<String, String> escaper) {
        this.escaper = escaper;
    }

    public Function<String, String> getEscaper() {
        return escaper;
    }
}
