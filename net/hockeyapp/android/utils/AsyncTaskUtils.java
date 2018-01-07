package net.hockeyapp.android.utils;

import android.os.AsyncTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class AsyncTaskUtils {
    private static Executor sCustomExecutor;

    public static void execute(AsyncTask<Void, ?, ?> asyncTask) {
        asyncTask.executeOnExecutor(sCustomExecutor != null ? sCustomExecutor : AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static <T> FutureTask<T> execute(Callable<T> callable) {
        Executor executor = sCustomExecutor != null ? sCustomExecutor : AsyncTask.THREAD_POOL_EXECUTOR;
        FutureTask<T> futureTask = new FutureTask(callable);
        executor.execute(futureTask);
        return futureTask;
    }
}
