package org.telegram.messenger.exoplayer2.upstream;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Loader
  implements LoaderErrorThrower
{
  public static final int DONT_RETRY = 2;
  public static final int DONT_RETRY_FATAL = 3;
  public static final int RETRY = 0;
  public static final int RETRY_RESET_ERROR_COUNT = 1;
  private LoadTask<? extends Loadable> currentTask;
  private final ExecutorService downloadExecutorService;
  private IOException fatalError;
  
  public Loader(String paramString)
  {
    this.downloadExecutorService = Util.newSingleThreadExecutor(paramString);
  }
  
  public void cancelLoading()
  {
    this.currentTask.cancel(false);
  }
  
  public boolean isLoading()
  {
    if (this.currentTask != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    maybeThrowError(Integer.MIN_VALUE);
  }
  
  public void maybeThrowError(int paramInt)
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
    if (this.currentTask != null)
    {
      LoadTask localLoadTask = this.currentTask;
      int i = paramInt;
      if (paramInt == Integer.MIN_VALUE) {
        i = this.currentTask.defaultMinRetryCount;
      }
      localLoadTask.maybeThrowError(i);
    }
  }
  
  public void release()
  {
    release(null);
  }
  
  public boolean release(ReleaseCallback paramReleaseCallback)
  {
    boolean bool1 = false;
    boolean bool2;
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      bool2 = bool1;
      if (paramReleaseCallback != null)
      {
        this.downloadExecutorService.execute(new ReleaseTask(paramReleaseCallback));
        bool2 = bool1;
      }
    }
    for (;;)
    {
      this.downloadExecutorService.shutdown();
      return bool2;
      bool2 = bool1;
      if (paramReleaseCallback != null)
      {
        paramReleaseCallback.onLoaderReleased();
        bool2 = true;
      }
    }
  }
  
  public <T extends Loadable> long startLoading(T paramT, Callback<T> paramCallback, int paramInt)
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      long l = SystemClock.elapsedRealtime();
      new LoadTask(localLooper, paramT, paramCallback, paramInt, l).start(0L);
      return l;
    }
  }
  
  public static abstract interface Callback<T extends Loader.Loadable>
  {
    public abstract void onLoadCanceled(T paramT, long paramLong1, long paramLong2, boolean paramBoolean);
    
    public abstract void onLoadCompleted(T paramT, long paramLong1, long paramLong2);
    
    public abstract int onLoadError(T paramT, long paramLong1, long paramLong2, IOException paramIOException);
  }
  
  @SuppressLint({"HandlerLeak"})
  private final class LoadTask<T extends Loader.Loadable>
    extends Handler
    implements Runnable
  {
    private static final int MSG_CANCEL = 1;
    private static final int MSG_END_OF_SOURCE = 2;
    private static final int MSG_FATAL_ERROR = 4;
    private static final int MSG_IO_EXCEPTION = 3;
    private static final int MSG_START = 0;
    private static final String TAG = "LoadTask";
    private final Loader.Callback<T> callback;
    private IOException currentError;
    public final int defaultMinRetryCount;
    private int errorCount;
    private volatile Thread executorThread;
    private final T loadable;
    private volatile boolean released;
    private final long startTimeMs;
    
    public LoadTask(T paramT, Loader.Callback<T> paramCallback, int paramInt, long paramLong)
    {
      super();
      this.loadable = paramCallback;
      this.callback = paramInt;
      this.defaultMinRetryCount = paramLong;
      Object localObject;
      this.startTimeMs = localObject;
    }
    
    private void execute()
    {
      this.currentError = null;
      Loader.this.downloadExecutorService.execute(Loader.this.currentTask);
    }
    
    private void finish()
    {
      Loader.access$002(Loader.this, null);
    }
    
    private long getRetryDelayMillis()
    {
      return Math.min((this.errorCount - 1) * 1000, 5000);
    }
    
    public void cancel(boolean paramBoolean)
    {
      this.released = paramBoolean;
      this.currentError = null;
      if (hasMessages(0))
      {
        removeMessages(0);
        if (!paramBoolean) {
          sendEmptyMessage(1);
        }
      }
      for (;;)
      {
        if (paramBoolean)
        {
          finish();
          long l = SystemClock.elapsedRealtime();
          this.callback.onLoadCanceled(this.loadable, l, l - this.startTimeMs, true);
        }
        return;
        this.loadable.cancelLoad();
        if (this.executorThread != null) {
          this.executorThread.interrupt();
        }
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (this.released) {}
      label241:
      do
      {
        for (;;)
        {
          return;
          if (paramMessage.what == 0)
          {
            execute();
          }
          else
          {
            if (paramMessage.what == 4) {
              throw ((Error)paramMessage.obj);
            }
            finish();
            long l1 = SystemClock.elapsedRealtime();
            long l2 = l1 - this.startTimeMs;
            if (this.loadable.isLoadCanceled()) {
              this.callback.onLoadCanceled(this.loadable, l1, l2, false);
            } else {
              switch (paramMessage.what)
              {
              default: 
                break;
              case 1: 
                this.callback.onLoadCanceled(this.loadable, l1, l2, false);
                break;
              case 2: 
                try
                {
                  this.callback.onLoadCompleted(this.loadable, l1, l2);
                }
                catch (RuntimeException paramMessage)
                {
                  Log.e("LoadTask", "Unexpected exception handling load completed", paramMessage);
                  Loader.access$102(Loader.this, new Loader.UnexpectedLoaderException(paramMessage));
                }
                break;
              case 3: 
                this.currentError = ((IOException)paramMessage.obj);
                i = this.callback.onLoadError(this.loadable, l1, l2, this.currentError);
                if (i != 3) {
                  break label241;
                }
                Loader.access$102(Loader.this, this.currentError);
              }
            }
          }
        }
      } while (i == 2);
      if (i == 1) {}
      for (int i = 1;; i = this.errorCount + 1)
      {
        this.errorCount = i;
        start(getRetryDelayMillis());
        break;
      }
    }
    
    public void maybeThrowError(int paramInt)
      throws IOException
    {
      if ((this.currentError != null) && (this.errorCount > paramInt)) {
        throw this.currentError;
      }
    }
    
    public void run()
    {
      for (;;)
      {
        try
        {
          this.executorThread = Thread.currentThread();
          if (!this.loadable.isLoadCanceled())
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            TraceUtil.beginSection("load:" + this.loadable.getClass().getSimpleName());
          }
        }
        catch (IOException localIOException)
        {
          if (this.released) {
            continue;
          }
          obtainMessage(3, localIOException).sendToTarget();
          continue;
        }
        catch (InterruptedException localInterruptedException)
        {
          Assertions.checkState(this.loadable.isLoadCanceled());
          if (this.released) {
            continue;
          }
          sendEmptyMessage(2);
          continue;
        }
        catch (Exception localException)
        {
          Log.e("LoadTask", "Unexpected exception loading stream", localException);
          if (this.released) {
            continue;
          }
          obtainMessage(3, new Loader.UnexpectedLoaderException(localException)).sendToTarget();
          continue;
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          Log.e("LoadTask", "OutOfMemory error loading stream", localOutOfMemoryError);
          if (this.released) {
            continue;
          }
          obtainMessage(3, new Loader.UnexpectedLoaderException(localOutOfMemoryError)).sendToTarget();
          continue;
        }
        catch (Error localError)
        {
          Log.e("LoadTask", "Unexpected error loading stream", localError);
          if (this.released) {
            continue;
          }
          obtainMessage(4, localError).sendToTarget();
          throw localError;
        }
        try
        {
          this.loadable.load();
          TraceUtil.endSection();
          if (!this.released) {
            sendEmptyMessage(2);
          }
          return;
        }
        finally
        {
          TraceUtil.endSection();
        }
      }
    }
    
    public void start(long paramLong)
    {
      boolean bool;
      if (Loader.this.currentTask == null)
      {
        bool = true;
        Assertions.checkState(bool);
        Loader.access$002(Loader.this, this);
        if (paramLong <= 0L) {
          break label44;
        }
        sendEmptyMessageDelayed(0, paramLong);
      }
      for (;;)
      {
        return;
        bool = false;
        break;
        label44:
        execute();
      }
    }
  }
  
  public static abstract interface Loadable
  {
    public abstract void cancelLoad();
    
    public abstract boolean isLoadCanceled();
    
    public abstract void load()
      throws IOException, InterruptedException;
  }
  
  public static abstract interface ReleaseCallback
  {
    public abstract void onLoaderReleased();
  }
  
  private static final class ReleaseTask
    extends Handler
    implements Runnable
  {
    private final Loader.ReleaseCallback callback;
    
    public ReleaseTask(Loader.ReleaseCallback paramReleaseCallback)
    {
      this.callback = paramReleaseCallback;
    }
    
    public void handleMessage(Message paramMessage)
    {
      this.callback.onLoaderReleased();
    }
    
    public void run()
    {
      if (getLooper().getThread().isAlive()) {
        sendEmptyMessage(0);
      }
    }
  }
  
  public static final class UnexpectedLoaderException
    extends IOException
  {
    public UnexpectedLoaderException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/Loader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */