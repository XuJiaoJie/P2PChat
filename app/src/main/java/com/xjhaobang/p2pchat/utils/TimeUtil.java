package com.xjhaobang.p2pchat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 2017/6/3.
 */

public class TimeUtil {

    public static String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

}
