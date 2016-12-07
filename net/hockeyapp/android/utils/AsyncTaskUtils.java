package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build.VERSION;

public class AsyncTaskUtils {
    @SuppressLint({"InlinedApi"})
    public static void execute(AsyncTask<Void, ?, ?> asyncTask) {
        if (VERSION.SDK_INT <= 12) {
            asyncTask.execute(new Void[0]);
        } else {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }
}
