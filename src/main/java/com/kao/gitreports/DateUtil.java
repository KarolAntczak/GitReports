package com.kao.gitreports;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date parseDate(String date) {
        return  Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
