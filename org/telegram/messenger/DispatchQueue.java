package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue
  extends Thread
{
  private volatile Handler handler = null;
  private CountDownLatch syncLatch = new CountDownLatch(1);
  
  public DispatchQueue(String paramString)
  {
    setName(paramString);
    start();
  }
  
  public void cancelRunnable(Runnable paramRunnable)
  {
    try
    {
      this.syncLatch.await();
      this.handler.removeCallbacks(paramRunnable);
      return;
    }
    catch (Exception paramRunnable)
    {
      for (;;)
      {
        FileLog.e(paramRunnable);
      }
    }
  }
  
  public void cleanupQueue()
  {
    try
    {
      this.syncLatch.await();
      this.handler.removeCallbacksAndMessages(null);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void handleMessage(Message paramMessage) {}
  
  public void postRunnable(Runnable paramRunnable)
  {
    postRunnable(paramRunnable, 0L);
  }
  
  public void postRunnable(Runnable paramRunnable, long paramLong)
  {
    for (;;)
    {
      try
      {
        this.syncLatch.await();
        if (paramLong <= 0L)
        {
          this.handler.post(paramRunnable);
          return;
        }
      }
      catch (Exception paramRunnable)
      {
        FileLog.e(paramRunnable);
        continue;
      }
      this.handler.postDelayed(paramRunnable, paramLong);
    }
  }
  
  public void run()
  {
    Looper.prepare();
    this.handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        DispatchQueue.this.handleMessage(paramAnonymousMessage);
      }
    };
    this.syncLatch.countDown();
    Looper.loop();
  }
  
  public void sendMessage(Message paramMessage, int paramInt)
  {
    for (;;)
    {
      try
      {
        this.syncLatch.await();
        if (paramInt <= 0)
        {
          this.handler.sendMessage(paramMessage);
          return;
        }
      }
      catch (Exception paramMessage)
      {
        FileLog.e(paramMessage);
        continue;
      }
      this.handler.sendMessageDelayed(paramMessage, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/DispatchQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */