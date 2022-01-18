package ru.gosuslugi.pgu.sp.adapter.util;

import com.ibm.icu.text.RuleBasedNumberFormat;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Класс с методами для перевода чисел в текст
 */
public class MoneyService {
    private static final RuleBasedNumberFormat RULE_BASED_NUMBER_FORMAT = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT);

    private static final Collection<Character> TO_ALLOWED = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ',');
    private static final Collection<Character> TO_SUPPRESS = Arrays.asList(' ', '₽');

    /**
     * Вывод значения
     */
    public String printWordRubDigitKop(String input) {
        BigDecimal normalized = normalize(input);
        final int rubs = normalized.setScale(0, RoundingMode.FLOOR).intValue();
        final int kopeks = normalized.subtract(normalized.setScale(0, RoundingMode.FLOOR)).movePointRight(2).intValue();
        return String.format("%s руб. %02d коп.", RULE_BASED_NUMBER_FORMAT.format(rubs), kopeks);
    }

    /**
     * Вывод значения без знака рубля
     */
    public String printWithoutRSymbol(String input) {
        BigDecimal normalized = normalize(input);
        final int rubs = normalized.setScale(0, RoundingMode.FLOOR).intValue();
        return input.replace("₽", "").trim() + " " + getRubWord(rubs);
    }

    /**
     * Вспомогательный метод для перевода чисел в текст
     */
    public String getRubWord(int rubs) {
        return getDeclensionOfWords(rubs, Arrays.asList("рубль", "рубля", "рублей"));
    }

    public String getDeclensionOfWords(int count, List<String> wordGroup) {
        int dozens = count % 100;
        int units = count % 10;
        if (dozens > 10  && dozens < 20) {
            return wordGroup.get(2);
        }
        if (units == 1) {
            return wordGroup.get(0);
        }
        if (units == 2 || units == 3 || units == 4) {
            return wordGroup.get(1);
        }
        return wordGroup.get(2);
    }

    /**
     * Нормализация формата суммы
     */
    private BigDecimal normalize(String input) {
        StringBuffer sb = new StringBuffer();
        for (char ch : input.toCharArray()) {
            if (TO_SUPPRESS.contains(ch)) {
                continue;
            }
            if (!TO_ALLOWED.contains(ch)) {
                throw new SpAdapterInputDataException("Некорректный формат для денег \"" + input + "\", символ = '" + ch + "'");
            }

            // To american delimiter
            if (ch == ',') {
                ch = '.';
            }
            sb.append(ch);
        }
        String result = sb.toString();
        try {
            return new BigDecimal(result);
        } catch (NumberFormatException e) {
            throw new SpAdapterInputDataException("Некорректный формат для очищенного значения денег \"" + result + "\"");
        }
    }
}
