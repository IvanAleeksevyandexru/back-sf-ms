package ru.gosuslugi.pgu.sp.adapter.service.input;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateServiceTest {

    public static final String input_pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String input_pattern_z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Test
    public void test() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(input_pattern);
        String inputString = "2020-11-06T00:00:00.000+08:00";
        ZonedDateTime dt = ZonedDateTime.parse(inputString, formatter);
        ZoneId zone = dt.getZone();

        SimpleDateFormat format = new SimpleDateFormat(input_pattern);
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date date = format.parse(inputString);

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        outputFormat.setTimeZone(TimeZone.getTimeZone(zone));
        assertEquals("06.11.2020", outputFormat.format(date));
    }

    @Test
    public void testZoneDateTime() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(input_pattern);
        String inputString = "2020-11-06T00:00:00.000+08:00";
        ZonedDateTime dt = ZonedDateTime.parse(inputString, formatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        assertEquals("06.11.2020", outputFormatter.format(dt));

    }

    @Test
    public void testZoneDateTimeZ() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(input_pattern_z);
        String inputString = "2021-10-09T18:00:00.000Z";
        Date dt = formatter.parse(inputString);

        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd.MM.yyyy");
        assertEquals("09.10.2021", outputFormatter.format(dt));

    }

}
