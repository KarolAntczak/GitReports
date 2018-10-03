package com.kao.gitreports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    private static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd_MM_yy");
    private static final DateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    public static Date parseDate(String date) {
        return  Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String toShortString(Date date) {
        return SHORT_DATE_FORMAT.format(date);
    }

    public static String toCsvString(Date date) {
        return CSV_DATE_FORMAT.format(date);
    }


}


