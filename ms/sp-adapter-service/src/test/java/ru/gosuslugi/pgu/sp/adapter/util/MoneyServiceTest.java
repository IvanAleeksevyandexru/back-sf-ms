package ru.gosuslugi.pgu.sp.adapter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * https://stackoverflow.com/questions/18898773/java-escape-json-string
 */
public class MoneyServiceTest {

    private static final ObjectMapper objectMapper = JsonProcessingUtil.getObjectMapper();

    @Test
    public void test() throws IOException {
//        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT);
//        System.out.println(nf.format(0));

        assertEquals("двести тридцать четыре руб. 43 коп.", new MoneyService().printWordRubDigitKop("234 ,4345 ₽"));
    }

    @Test
    public void testRubWord() throws IOException {

        assertEquals("рублей", new MoneyService().getRubWord(0));
        assertEquals("рубль", new MoneyService().getRubWord(1));
        assertEquals("рубля", new MoneyService().getRubWord(2));
        assertEquals("рубля", new MoneyService().getRubWord(3));
        assertEquals("рубля", new MoneyService().getRubWord(4));
        assertEquals("рублей", new MoneyService().getRubWord(5));
        assertEquals("рублей", new MoneyService().getRubWord(6));
        assertEquals("рублей", new MoneyService().getRubWord(7));
        assertEquals("рублей", new MoneyService().getRubWord(8));
        assertEquals("рублей", new MoneyService().getRubWord(9));
        assertEquals("рублей", new MoneyService().getRubWord(10));
        assertEquals("рублей", new MoneyService().getRubWord(11));
        assertEquals("рублей", new MoneyService().getRubWord(12));
        assertEquals("рублей", new MoneyService().getRubWord(13));
        assertEquals("рублей", new MoneyService().getRubWord(14));
        assertEquals("рублей", new MoneyService().getRubWord(15));
        assertEquals("рублей", new MoneyService().getRubWord(16));
        assertEquals("рублей", new MoneyService().getRubWord(16));
        assertEquals("рублей", new MoneyService().getRubWord(18));
        assertEquals("рублей", new MoneyService().getRubWord(19));
        assertEquals("рублей", new MoneyService().getRubWord(20));
        assertEquals("рубль", new MoneyService().getRubWord(21));
        assertEquals("рубля", new MoneyService().getRubWord(22));
        assertEquals("рубля", new MoneyService().getRubWord(23));
        assertEquals("рубля", new MoneyService().getRubWord(24));
        assertEquals("рублей", new MoneyService().getRubWord(25));
    }

    @Test
    public void testDeclensionOfWords() throws IOException {
        // velocity
        // #set($foo = ["владелец", "владельца", "владельцев"])
        List<String> group = Arrays.asList("владелец", "владельца", "владельцев");

        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(0, group));
        assertEquals("владелец", new MoneyService().getDeclensionOfWords(1, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(2, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(3, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(4, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(5, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(6, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(7, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(8, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(9, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(10, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(11, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(12, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(13, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(14, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(15, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(16, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(16, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(18, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(19, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(20, group));
        assertEquals("владелец", new MoneyService().getDeclensionOfWords(21, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(22, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(23, group));
        assertEquals("владельца", new MoneyService().getDeclensionOfWords(24, group));
        assertEquals("владельцев", new MoneyService().getDeclensionOfWords(25, group));
    }
}
