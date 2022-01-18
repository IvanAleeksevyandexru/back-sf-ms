package ru.gosuslugi.pgu.sp.adapter.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Slf4j
public class DateService {

    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    public static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter FORMAT_WITHOUT_TIMEZONE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter OUTPUT_FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy 'в' HH:mm");
    public static final DateTimeFormatter OUTPUT_FORMATTER_WITH_TIME_DAY_AND_MONTH = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'г. в' HH:mm", new Locale("ru"));

    private static final Map<String, String> MONTH_MAP = new HashMap<>();
    static {
        MONTH_MAP.put("01", "января");
        MONTH_MAP.put("02", "февраля");
        MONTH_MAP.put("03", "марта");
        MONTH_MAP.put("04", "апреля");
        MONTH_MAP.put("05", "мая");
        MONTH_MAP.put("06", "июня");
        MONTH_MAP.put("07", "июля");
        MONTH_MAP.put("08", "августа");
        MONTH_MAP.put("09", "сентября");
        MONTH_MAP.put("10", "октября");
        MONTH_MAP.put("11", "ноября");
        MONTH_MAP.put("12", "декабря");
    }

    public String format(String date) {
        return OUTPUT_FORMATTER.format(ZonedDateTime.parse(date, INPUT_FORMATTER));
    }
    public String format(String template, String date) {
        return DateTimeFormatter.ofPattern(template).format(ZonedDateTime.parse(date, INPUT_FORMATTER));
    }
    public String formatZ(String date) {
        return OUTPUT_FORMATTER.format(ZonedDateTime.parse(date, ISO_DATE_TIME));
    }
    public String formatYet(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date dt = formatter.parse(date);

        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd.MM.yyyy");
        return outputFormatter.format(dt);
    }
    public String formatDateAndTime(String date) {
        return OUTPUT_FORMATTER_WITH_TIME.format(ZonedDateTime.parse(date, ISO_DATE_TIME));
    }

    public String formatDateAndTimeWithMonthAndDayNames(String date) {
        return StringUtils.capitalize(OUTPUT_FORMATTER_WITH_TIME_DAY_AND_MONTH.format(LocalDateTime.parse(date, FORMAT_WITHOUT_TIMEZONE)));
    }

    /**
     * Метод получения дня из даты
     */
    public String getDay(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = formatter.parse(date);
        return new SimpleDateFormat("dd").format(dt);
    }

    /**
     * Метод получения месяца из даты
     */
    public String getMonth(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = formatter.parse(date);
        String monthNum = new SimpleDateFormat("MM").format(dt);
        return MONTH_MAP.get(monthNum);
    }

    /**
     * Метод получения года из даты
     */
    public String getYear(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = formatter.parse(date);
        return new SimpleDateFormat("yyyy").format(dt);
    }

    /**
     * Преобразование даты из "обратной" последовательности yyyy-MM-dd в обычную dd-MM-yyyy
     */
    public String convertToRussianDate (String date) throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = formatter.parse(date);
            return new SimpleDateFormat("dd.MM.yyyy").format(dt);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Не распарсилась дата \"" + date + "\" в формате \"yyyy-MM-dd\"", e);
            }
            return date;
        }
    }

    /**
     * Преобразование даты из формата yyyy-MM-dd'T'HH:mm:ss в упрощенный dd.MM.yyyy
     */
    public String formatBGA(String date) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX");
        Date dt = formatter.parse(date);
        return new SimpleDateFormat("dd.MM.yyyy").format(dt);
    }

    /**
     * Преобразование даты в формат, используемый в электронных подписях
     */
    public String formatForElectronicSigns(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX");
        Date dt = formatter.parse(date);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(dt);
    }

    public String formatForRegistrationDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = formatter.parse(date);
        return new SimpleDateFormat("dd.MM.yyyy").format(dt);
    }

    /**
     * Метод позволяющий найти разницу лет
     */
    public Integer getDeltaYears(String year) {
        return LocalDate.now().getYear() - Integer.parseInt(year);
    }

}
