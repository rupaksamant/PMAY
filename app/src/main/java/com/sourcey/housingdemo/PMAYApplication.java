package com.sourcey.housingdemo;

import android.app.Application;

import java.io.PrintWriter;
import java.io.StringWriter;


public class PMAYApplication extends Application {

    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
//            Log.e("TestApplication", "Uncaught exception is: ", ex);
            // log it & phone home.\
            System.out.println("rupak thread = [" + thread + "], ex = [" + ex + "]");
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            Log.d("PMAY","PMAYApplication  : "+ errors.toString());
            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
