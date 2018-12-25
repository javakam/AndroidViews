package com.work.utils;

import java.util.Calendar;

/**
 * Created by changbao on 2018/12/23
 */
public class DateUtils {
    public static int getYearForSystem(){
       return   Calendar.getInstance().get(Calendar.YEAR);
    }
}
