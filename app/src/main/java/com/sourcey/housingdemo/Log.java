package com.sourcey.housingdemo;

/**
 * Created by Biswajit on 17-04-2018.
 */

public class Log {


    public static void v(String tag, String msg) {
        PMAYLogger.getWriteLogInstance().writeLog(tag, msg);

    }

    public static void d(String tag, String msg) {
        PMAYLogger.getWriteLogInstance().writeLog(tag, msg);
    }
}
