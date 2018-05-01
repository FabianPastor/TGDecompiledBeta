package org.telegram.messenger;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;

public class FileLog
{
  private static volatile FileLog Instance = null;
  private static final String tag = "tmessages";
  private File currentFile = null;
  private FastDateFormat dateFormat = null;
  private boolean initied;
  private DispatchQueue logQueue = null;
  private File networkFile = null;
  private OutputStreamWriter streamWriter = null;
  
  public FileLog()
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      init();
    }
  }
  
  public static void cleanupLogs()
  {
    ensureInitied();
    Object localObject1 = ApplicationLoader.applicationContext.getExternalFilesDir(null);
    if (localObject1 == null) {}
    do
    {
      return;
      localObject1 = new File(((File)localObject1).getAbsolutePath() + "/logs").listFiles();
    } while (localObject1 == null);
    int i = 0;
    label55:
    Object localObject2;
    if (i < localObject1.length)
    {
      localObject2 = localObject1[i];
      if ((getInstance().currentFile == null) || (!((File)localObject2).getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath()))) {
        break label99;
      }
    }
    for (;;)
    {
      i++;
      break label55;
      break;
      label99:
      if ((getInstance().networkFile == null) || (!((File)localObject2).getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
        ((File)localObject2).delete();
      }
    }
  }
  
  public static void d(String paramString)
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      ensureInitied();
      Log.d("tmessages", paramString);
      if (getInstance().streamWriter != null) {
        getInstance().logQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              OutputStreamWriter localOutputStreamWriter = FileLog.getInstance().streamWriter;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localOutputStreamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + this.val$message + "\n");
              FileLog.getInstance().streamWriter.flush();
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                localException.printStackTrace();
              }
            }
          }
        });
      }
    }
  }
  
  public static void e(String paramString)
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      ensureInitied();
      Log.e("tmessages", paramString);
      if (getInstance().streamWriter != null) {
        getInstance().logQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              OutputStreamWriter localOutputStreamWriter = FileLog.getInstance().streamWriter;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localOutputStreamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$message + "\n");
              FileLog.getInstance().streamWriter.flush();
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                localException.printStackTrace();
              }
            }
          }
        });
      }
    }
  }
  
  public static void e(String paramString, final Throwable paramThrowable)
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      ensureInitied();
      Log.e("tmessages", paramString, paramThrowable);
      if (getInstance().streamWriter != null) {
        getInstance().logQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              OutputStreamWriter localOutputStreamWriter = FileLog.getInstance().streamWriter;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localOutputStreamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$message + "\n");
              FileLog.getInstance().streamWriter.write(paramThrowable.toString());
              FileLog.getInstance().streamWriter.flush();
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                localException.printStackTrace();
              }
            }
          }
        });
      }
    }
  }
  
  public static void e(Throwable paramThrowable)
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      ensureInitied();
      paramThrowable.printStackTrace();
      if (getInstance().streamWriter != null) {
        getInstance().logQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              Object localObject1 = FileLog.getInstance().streamWriter;
              Object localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              ((OutputStreamWriter)localObject1).write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$e + "\n");
              localObject2 = this.val$e.getStackTrace();
              for (int i = 0; i < localObject2.length; i++)
              {
                OutputStreamWriter localOutputStreamWriter = FileLog.getInstance().streamWriter;
                localObject1 = new java/lang/StringBuilder;
                ((StringBuilder)localObject1).<init>();
                localOutputStreamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + localObject2[i] + "\n");
              }
              FileLog.getInstance().streamWriter.flush();
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                localException.printStackTrace();
              }
            }
          }
        });
      } else {
        paramThrowable.printStackTrace();
      }
    }
  }
  
  public static void ensureInitied()
  {
    getInstance().init();
  }
  
  /* Error */
  public static FileLog getInstance()
  {
    // Byte code:
    //   0: getstatic 35	org/telegram/messenger/FileLog:Instance	Lorg/telegram/messenger/FileLog;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +31 -> 38
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 35	org/telegram/messenger/FileLog:Instance	Lorg/telegram/messenger/FileLog;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +15 -> 35
    //   23: new 2	org/telegram/messenger/FileLog
    //   26: astore_1
    //   27: aload_1
    //   28: invokespecial 148	org/telegram/messenger/FileLog:<init>	()V
    //   31: aload_1
    //   32: putstatic 35	org/telegram/messenger/FileLog:Instance	Lorg/telegram/messenger/FileLog;
    //   35: ldc 2
    //   37: monitorexit
    //   38: aload_1
    //   39: areturn
    //   40: astore_1
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_1
    //   45: athrow
    //   46: astore_1
    //   47: goto -6 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localFileLog1	FileLog
    //   5	34	1	localFileLog2	FileLog
    //   40	5	1	localObject1	Object
    //   46	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	40	finally
    //   23	31	40	finally
    //   35	38	40	finally
    //   41	44	40	finally
    //   31	35	46	finally
  }
  
  public static String getNetworkLogPath()
  {
    Object localObject1;
    if (!BuildVars.LOGS_ENABLED) {
      localObject1 = "";
    }
    for (;;)
    {
      return (String)localObject1;
      try
      {
        Object localObject2 = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (localObject2 == null)
        {
          localObject1 = "";
        }
        else
        {
          localObject1 = new java/io/File;
          Object localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((File)localObject1).<init>(((File)localObject2).getAbsolutePath() + "/logs");
          ((File)localObject1).mkdirs();
          localObject2 = getInstance();
          localObject3 = new java/io/File;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          ((File)localObject3).<init>((File)localObject1, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
          ((FileLog)localObject2).networkFile = ((File)localObject3);
          localObject1 = getInstance().networkFile.getAbsolutePath();
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        String str = "";
      }
    }
  }
  
  public static void w(String paramString)
  {
    if (!BuildVars.LOGS_ENABLED) {}
    for (;;)
    {
      return;
      ensureInitied();
      Log.w("tmessages", paramString);
      if (getInstance().streamWriter != null) {
        getInstance().logQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              OutputStreamWriter localOutputStreamWriter = FileLog.getInstance().streamWriter;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localOutputStreamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + this.val$message + "\n");
              FileLog.getInstance().streamWriter.flush();
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                localException.printStackTrace();
              }
            }
          }
        });
      }
    }
  }
  
  public void init()
  {
    if (this.initied) {}
    for (;;)
    {
      return;
      this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
      try
      {
        localObject1 = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (localObject1 == null) {
          continue;
        }
        localObject2 = new java/io/File;
        Object localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((File)localObject2).<init>(((File)localObject1).getAbsolutePath() + "/logs");
        ((File)localObject2).mkdirs();
        localObject3 = new java/io/File;
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        ((File)localObject3).<init>((File)localObject2, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
        this.currentFile = ((File)localObject3);
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            Object localObject2 = new org/telegram/messenger/DispatchQueue;
            ((DispatchQueue)localObject2).<init>("logQueue");
            this.logQueue = ((DispatchQueue)localObject2);
            this.currentFile.createNewFile();
            Object localObject1 = new java/io/FileOutputStream;
            ((FileOutputStream)localObject1).<init>(this.currentFile);
            localObject2 = new java/io/OutputStreamWriter;
            ((OutputStreamWriter)localObject2).<init>((OutputStream)localObject1);
            this.streamWriter = ((OutputStreamWriter)localObject2);
            localObject1 = this.streamWriter;
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            ((OutputStreamWriter)localObject1).write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
            this.streamWriter.flush();
            this.initied = true;
            break;
            localException1 = localException1;
            localException1.printStackTrace();
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            localException2.printStackTrace();
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */