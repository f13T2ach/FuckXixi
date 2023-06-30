package com.cxk.fuckxixi.handle;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ExceptionHandler.kt */
public final class ExceptionHandle implements UncaughtExceptionHandler {
    public final Activity mContext;

    public void uncaughtException(Thread thread, Throwable th) {
        Intrinsics.checkNotNullParameter(thread, "thread");
        Intrinsics.checkNotNullParameter(th, "th");
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("************ UNEXPECTED EXCEPTION ************\n\n");
        stringBuilder.append(stringWriter.toString());
        stringBuilder.append("\n************ 设备信息 ***********\n");
        stringBuilder.append("品牌: ");
        stringBuilder.append(Build.BRAND);
        String str = "\n";
        stringBuilder.append(str);
        stringBuilder.append("设备: ");
        stringBuilder.append(Build.DEVICE);
        stringBuilder.append(str);
        stringBuilder.append("型号: ");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append(str);
        stringBuilder.append("Id: ");
        stringBuilder.append(Build.ID);
        stringBuilder.append(str);
        stringBuilder.append("产品: ");
        stringBuilder.append(Build.PRODUCT);
        stringBuilder.append(str);
        stringBuilder.append("\n************ 固件 ************\n");
        stringBuilder.append("SDK: ");
        stringBuilder.append(VERSION.SDK);
        stringBuilder.append(str);
        stringBuilder.append("Release: ");
        stringBuilder.append(VERSION.RELEASE);
        stringBuilder.append(str);
        stringBuilder.append("Incremental: ");
        stringBuilder.append(VERSION.INCREMENTAL);
        stringBuilder.append(str);
        try {
            Intent intent = new Intent(this.mContext, ExceptionActivity.class);
            intent.putExtra("error", stringBuilder.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.mContext.startActivity(intent);
            Log.e("程序","[全局异常捕捉]发现异常");
            Process.killProcess(Process.myPid());
            System.exit(10);
            throw new RuntimeException("System.exThread.sleep(3000);  it returned normally, while it was supposed to halt JVM.");
        } catch (Throwable th2) {
            NoClassDefFoundError runtimeException = new NoClassDefFoundError(th2.getMessage());
        }
    }

    public ExceptionHandle(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "mContext");
        this.mContext = activity;
    }
}
