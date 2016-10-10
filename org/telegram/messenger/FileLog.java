package org.telegram.messenger;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;

public class FileLog
{
  private static volatile FileLog Instance = null;
  private File currentFile = null;
  private FastDateFormat dateFormat = null;
  private DispatchQueue logQueue = null;
  private File networkFile = null;
  private OutputStreamWriter streamWriter = null;
  
  public FileLog()
  {
    if (!BuildVars.DEBUG_VERSION) {
      return;
    }
    this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
    for (;;)
    {
      try
      {
        File localFile = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (localFile == null) {
          break;
        }
        localFile = new File(localFile.getAbsolutePath() + "/logs");
        localFile.mkdirs();
        this.currentFile = new File(localFile, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
        continue;
      }
      try
      {
        this.logQueue = new DispatchQueue("logQueue");
        this.currentFile.createNewFile();
        this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
        this.streamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
        this.streamWriter.flush();
        return;
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
        return;
      }
    }
  }
  
  public static void cleanupLogs()
  {
    Object localObject1 = ApplicationLoader.applicationContext.getExternalFilesDir(null);
    if (localObject1 == null) {}
    do
    {
      return;
      localObject1 = new File(((File)localObject1).getAbsolutePath() + "/logs").listFiles();
    } while (localObject1 == null);
    int i = 0;
    label52:
    Object localObject2;
    if (i < localObject1.length)
    {
      localObject2 = localObject1[i];
      if ((getInstance().currentFile == null) || (!((File)localObject2).getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath()))) {
        break label97;
      }
    }
    for (;;)
    {
      i += 1;
      break label52;
      break;
      label97:
      if ((getInstance().networkFile == null) || (!((File)localObject2).getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
        ((File)localObject2).delete();
      }
    }
  }
  
  public static void d(String paramString1, final String paramString2)
  {
    if (!BuildVars.DEBUG_VERSION) {}
    do
    {
      return;
      Log.d(paramString1, paramString2);
    } while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/" + this.val$tag + "﹕ " + paramString2 + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
  
  public static void e(String paramString1, final String paramString2)
  {
    if (!BuildVars.DEBUG_VERSION) {}
    do
    {
      return;
      Log.e(paramString1, paramString2);
    } while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "﹕ " + paramString2 + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
  
  public static void e(String paramString1, final String paramString2, final Throwable paramThrowable)
  {
    if (!BuildVars.DEBUG_VERSION) {}
    do
    {
      return;
      Log.e(paramString1, paramString2, paramThrowable);
    } while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "﹕ " + paramString2 + "\n");
          FileLog.getInstance().streamWriter.write(paramThrowable.toString());
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
  
  public static void e(String paramString, final Throwable paramThrowable)
  {
    if (!BuildVars.DEBUG_VERSION) {
      return;
    }
    paramThrowable.printStackTrace();
    if (getInstance().streamWriter != null)
    {
      getInstance().logQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "﹕ " + paramThrowable + "\n");
            StackTraceElement[] arrayOfStackTraceElement = paramThrowable.getStackTrace();
            int i = 0;
            while (i < arrayOfStackTraceElement.length)
            {
              FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + this.val$tag + "﹕ " + arrayOfStackTraceElement[i] + "\n");
              i += 1;
            }
            FileLog.getInstance().streamWriter.flush();
            return;
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
          }
        }
      });
      return;
    }
    paramThrowable.printStackTrace();
  }
  
  public static FileLog getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          FileLog localFileLog2 = Instance;
          localObject1 = localFileLog2;
          if (localFileLog2 == null) {
            localObject1 = new FileLog();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (FileLog)localObject1;
          return (FileLog)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localFileLog1;
  }
  
  public static String getNetworkLogPath()
  {
    if (!BuildVars.DEBUG_VERSION) {
      return "";
    }
    try
    {
      Object localObject = ApplicationLoader.applicationContext.getExternalFilesDir(null);
      if (localObject == null) {
        return "";
      }
      localObject = new File(((File)localObject).getAbsolutePath() + "/logs");
      ((File)localObject).mkdirs();
      getInstance().networkFile = new File((File)localObject, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
      localObject = getInstance().networkFile.getAbsolutePath();
      return (String)localObject;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return "";
  }
  
  public static void w(String paramString1, final String paramString2)
  {
    if (!BuildVars.DEBUG_VERSION) {}
    do
    {
      return;
      Log.w(paramString1, paramString2);
    } while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/" + this.val$tag + ": " + paramString2 + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/FileLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */