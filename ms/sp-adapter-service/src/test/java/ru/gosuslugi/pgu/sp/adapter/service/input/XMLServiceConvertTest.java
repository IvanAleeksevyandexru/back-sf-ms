package ru.gosuslugi.pgu.sp.adapter.service.input;


import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Oбход по файлам .vm и модификация CDATA секций: добавление разэкранирования
 */
public class XMLServiceConvertTest {

    public static final String CDATA = "(\\<\\!\\[CDATA\\[)(.+)(\\]\\]\\>)";
    public static final String ESCAPED_VARIABLE = "\\$[A-Za-z]+[0-9A-Za-z_]*((\\.[A-Za-z]+[0-9A-Za-z_]*)|(\\[[0-9]*\\])|(\\[\\'[A-Za-z]+[0-9A-Za-z_]*\\'\\]))*";
    public static final Function<String, String> VARIABLE_FUNCTION = var -> "$xmlService.unescape(" + var + ")";

    @Ignore
    @Test
    public void test() throws IOException {
        File root = new File("src/main/resources");
        convert(root);
        System.out.println("Ok!");
    }

    private void convert(File file) throws IOException {
        if (file.isDirectory()) {
            for(File item : file.listFiles()) {
                convert(item);
            }
        } else {
            convertFile(file);
        }
    }

    private void convertFile(File file) throws IOException {
        if (file.getName().endsWith(".vm")) {

            String str = read(file);
            String output = updatedString(
                str,
                CDATA,
                2,
                cdata -> {
                    String result;

                    // Ignore this cases
                    if (
                        cdata.indexOf("$") == -1
                        || Pattern.matches("\\$dateTool\\.format\\(.*\\)", cdata)
                        || Pattern.matches("01\\..*", cdata)
                        || Pattern.matches(".*substring\\(.*", cdata)
                    ) {
                        result = cdata;
                    } else {
                        result = updatedString(
                            cdata,
                            ESCAPED_VARIABLE,
                            0,
                            VARIABLE_FUNCTION
                        );
                    }

                    // Notification by changes
                    if (!cdata.equals(result)) {
                        System.out.println(cdata + "<=>" + result);
                    }
                    return result;
                }
            );
            if (!str.equals(output)) {
                save(file, output);
                System.out.println(file.getAbsolutePath() + " saved");
            }
        }
    }

    private String updatedString(String str, String regexTemplate, int idGroup, Function<String,String> function) {
        StringBuffer result = new StringBuffer();

        int endChar = 0;
        Iterable<MatchResult> iterable = allMatches(Pattern.compile(regexTemplate), str);
        for(MatchResult matchResult : iterable) {
            int start = matchResult.start(idGroup);
            if (start > endChar) {
                result.append(str.substring(endChar, start));
                endChar = start;
            }
            result.append(function.apply(matchResult.group(idGroup)));
            endChar += matchResult.group(idGroup).length();
        }
        result.append(str.substring(endChar));
        return result.toString();
    }

    private String read(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return IOUtils.toString(reader);
            }
        }
    }

    private void save(File file, String str) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            try (Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                writer.append(str);
            }
        }
    }

    public static Iterable<MatchResult> allMatches(
        final Pattern p,
        final CharSequence input
    ) {
        return new Iterable<MatchResult>() {
            public Iterator<MatchResult> iterator() {
                return new Iterator<MatchResult>() {
                    // Use a matcher internally.
                    final Matcher matcher = p.matcher(input);
                    // Keep a match around that supports any interleaving of hasNext/next calls.
                    MatchResult pending;

                    public boolean hasNext() {
                        // Lazily fill pending, and avoid calling find() multiple times if the
                        // clients call hasNext() repeatedly before sampling via next().
                        if (pending == null && matcher.find()) {
                            pending = matcher.toMatchResult();
                        }
                        return pending != null;
                    }

                    public MatchResult next() {
                        // Fill pending if necessary (as when clients call next() without
                        // checking hasNext()), throw if not possible.
                        if (!hasNext()) { throw new NoSuchElementException(); }
                        // Consume pending so next call to hasNext() does a find().
                        MatchResult next = pending;
                        pending = null;
                        return next;
                    }

                    /** Required to satisfy the interface, but unsupported. */
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }
}