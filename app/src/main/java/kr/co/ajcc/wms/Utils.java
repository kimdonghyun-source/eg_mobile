package kr.co.ajcc.wms;

import android.content.Context;
import android.widget.Toast;

import java.text.DecimalFormat;

public class Utils {

    public static void Log(String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.d("WMS", msg);
    }

    public static void LogLine(String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag(), msg);
    }

    private static String tag() {
        int level = 4;
        StackTraceElement trace = Thread.currentThread().getStackTrace()[level];
        String fileName = trace.getFileName();
        String classPath = trace.getClassName();
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        return "WMS# " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    public static void Toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String setComma(double number){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(number);
    }
}
