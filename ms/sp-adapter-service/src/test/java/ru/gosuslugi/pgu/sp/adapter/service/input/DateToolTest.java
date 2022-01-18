package ru.gosuslugi.pgu.sp.adapter.service.input;

import org.apache.velocity.tools.generic.DateTool;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateToolTest {

    public static final String input_pattern = "yyyy-MM-dd";

    @Test
    public void testMonthStartWith0() throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat(input_pattern);
        Date date = format.parse("2020-11-01");
        assertEquals(Integer.valueOf(10), new DateTool().getMonth(date));
    }

    @Test
    public void testMonthFormat() throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat(input_pattern);
        Date date = format.parse("2020-01-01");
        assertEquals("01", new DateTool().format("MM", date));
        assertEquals("1", new DateTool().format("M", date));
    }
}
