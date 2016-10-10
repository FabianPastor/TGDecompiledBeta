package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build.VERSION;

public class AsyncTaskUtils
{
  @SuppressLint({"InlinedApi"})
  public static void execute(AsyncTask<Void, ?, ?> paramAsyncTask)
  {
    if (Build.VERSION.SDK_INT <= 12)
    {
      paramAsyncTask.execute(new Void[0]);
      return;
    }
    paramAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/AsyncTaskUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */