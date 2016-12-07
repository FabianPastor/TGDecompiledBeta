package com.googlecode.mp4parser.util;

import java.util.Date;

public class DateHelper {
    public static Date convert(long secondsSince) {
        return new Date((secondsSince - NUM) * 1000);
    }

    public static long convert(Date date) {
        return (date.getTime() / 1000) + NUM;
    }
}
