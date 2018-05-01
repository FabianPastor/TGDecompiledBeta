package net.hockeyapp.android.utils;

import android.os.AsyncTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class AsyncTaskUtils
{
  private static Executor sCustomExecutor;
  
  public static <T> FutureTask<T> execute(Callable<T> paramCallable)
  {
    if (sCustomExecutor != null) {}
    for (Executor localExecutor = sCustomExecutor;; localExecutor = AsyncTask.THREAD_POOL_EXECUTOR)
    {
      paramCallable = new FutureTask(paramCallable);
      localExecutor.execute(paramCallable);
      return paramCallable;
    }
  }
  
  public static void execute(AsyncTask<Void, ?, ?> paramAsyncTask)
  {
    if (sCustomExecutor != null) {}
    for (Executor localExecutor = sCustomExecutor;; localExecutor = AsyncTask.THREAD_POOL_EXECUTOR)
    {
      paramAsyncTask.executeOnExecutor(localExecutor, new Void[0]);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/AsyncTaskUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */