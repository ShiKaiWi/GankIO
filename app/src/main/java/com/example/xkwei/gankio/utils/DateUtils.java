package com.example.xkwei.gankio.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xkwei on 03/01/2017.
 */

public class DateUtils {
    private static SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    public static String dateToString(Date date){
        if(null!=date)
            return mDateFormatter.format(date);
        return "";
    }
}
