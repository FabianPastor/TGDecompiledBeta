package android.support.v4.content;

import android.os.Build.VERSION;
import java.util.concurrent.Executor;

public final class ParallelExecutorCompat
{
  public static Executor getParallelExecutor()
  {
    if (Build.VERSION.SDK_INT >= 11) {
      return ExecutorCompatHoneycomb.getParallelExecutor();
    }
    return ModernAsyncTask.THREAD_POOL_EXECUTOR;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/ParallelExecutorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */