package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob
  extends JobIntentService
{
  private static volatile CountDownLatch countDownLatch;
  private static Runnable finishJobByTimeoutRunnable = new Runnable()
  {
    public void run() {}
  };
  private static volatile boolean startingJob;
  private static final Object sync = new Object();
  
  public static void finishJob()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        synchronized (KeepAliveJob.sync)
        {
          if (KeepAliveJob.countDownLatch != null)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("finish keep-alive job");
            }
            KeepAliveJob.countDownLatch.countDown();
          }
          if (KeepAliveJob.startingJob)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("finish queued keep-alive job");
            }
            KeepAliveJob.access$002(false);
          }
          return;
        }
      }
    });
  }
  
  public static void startJob()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: invokestatic 22	org/telegram/messenger/KeepAliveJob:access$000	()Z
        //   3: ifne +9 -> 12
        //   6: invokestatic 26	org/telegram/messenger/KeepAliveJob:access$100	()Ljava/util/concurrent/CountDownLatch;
        //   9: ifnull +4 -> 13
        //   12: return
        //   13: getstatic 32	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
        //   16: ifeq +8 -> 24
        //   19: ldc 34
        //   21: invokestatic 40	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
        //   24: invokestatic 44	org/telegram/messenger/KeepAliveJob:access$200	()Ljava/lang/Object;
        //   27: astore_1
        //   28: aload_1
        //   29: monitorenter
        //   30: iconst_1
        //   31: invokestatic 48	org/telegram/messenger/KeepAliveJob:access$002	(Z)Z
        //   34: pop
        //   35: aload_1
        //   36: monitorexit
        //   37: getstatic 54	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   40: astore_1
        //   41: new 56	android/content/Intent
        //   44: astore_2
        //   45: aload_2
        //   46: invokespecial 57	android/content/Intent:<init>	()V
        //   49: aload_1
        //   50: ldc 8
        //   52: sipush 1000
        //   55: aload_2
        //   56: invokestatic 63	org/telegram/messenger/support/JobIntentService:enqueueWork	(Landroid/content/Context;Ljava/lang/Class;ILandroid/content/Intent;)V
        //   59: goto -47 -> 12
        //   62: astore_2
        //   63: goto -51 -> 12
        //   66: astore_2
        //   67: aload_1
        //   68: monitorexit
        //   69: aload_2
        //   70: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	71	0	this	1
        //   44	12	2	localIntent	Intent
        //   62	1	2	localException	Exception
        //   66	4	2	localObject2	Object
        // Exception table:
        //   from	to	target	type
        //   13	24	62	java/lang/Exception
        //   24	30	62	java/lang/Exception
        //   37	59	62	java/lang/Exception
        //   69	71	62	java/lang/Exception
        //   30	37	66	finally
        //   67	69	66	finally
      }
    });
  }
  
  protected void onHandleWork(Intent arg1)
  {
    for (;;)
    {
      synchronized (sync)
      {
        if (!startingJob) {
          return;
        }
        ??? = new java/util/concurrent/CountDownLatch;
        ((CountDownLatch)???).<init>(1);
        countDownLatch = (CountDownLatch)???;
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("started keep-alive job");
        }
        Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, 60000L);
      }
      try
      {
        countDownLatch.await();
        Utilities.globalQueue.cancelRunnable(finishJobByTimeoutRunnable);
        synchronized (sync)
        {
          countDownLatch = null;
          if (!BuildVars.LOGS_ENABLED) {
            continue;
          }
          FileLog.d("ended keep-alive job");
          continue;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
      }
      catch (Throwable ???)
      {
        for (;;) {}
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/KeepAliveJob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */