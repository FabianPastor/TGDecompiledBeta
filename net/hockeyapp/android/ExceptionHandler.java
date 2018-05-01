package net.hockeyapp.android;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.utils.HockeyLog;

public class ExceptionHandler
  implements Thread.UncaughtExceptionHandler
{
  private CrashManagerListener mCrashManagerListener;
  private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
  private boolean mIgnoreDefaultHandler = false;
  
  public ExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, CrashManagerListener paramCrashManagerListener, boolean paramBoolean)
  {
    this.mDefaultExceptionHandler = paramUncaughtExceptionHandler;
    this.mIgnoreDefaultHandler = paramBoolean;
    this.mCrashManagerListener = paramCrashManagerListener;
  }
  
  private static String limitedString(String paramString)
  {
    String str = paramString;
    if (!TextUtils.isEmpty(paramString))
    {
      str = paramString;
      if (paramString.length() > 255) {
        str = paramString.substring(0, 255);
      }
    }
    return str;
  }
  
  public static void saveException(Throwable paramThrowable, Thread paramThread, CrashManagerListener paramCrashManagerListener)
  {
    Date localDate1 = new Date();
    Date localDate2 = new Date(CrashManager.getInitializeTimestamp());
    paramThrowable.printStackTrace(new PrintWriter(new StringWriter()));
    Context localContext;
    if (CrashManager.weakContext != null)
    {
      localContext = (Context)CrashManager.weakContext.get();
      if (localContext != null) {
        break label72;
      }
      HockeyLog.error("Failed to save exception: context in CrashManager is null");
    }
    for (;;)
    {
      return;
      localContext = null;
      break;
      label72:
      if (CrashManager.stackTracesCount >= 100)
      {
        HockeyLog.warn("ExceptionHandler: HockeyApp will not save this exception as there are already 100 or more unsent exceptions on disk");
      }
      else
      {
        String str = UUID.randomUUID().toString();
        paramThrowable = new CrashDetails(str, paramThrowable);
        paramThrowable.setAppPackage(Constants.APP_PACKAGE);
        paramThrowable.setAppVersionCode(Constants.APP_VERSION);
        paramThrowable.setAppVersionName(Constants.APP_VERSION_NAME);
        paramThrowable.setAppStartDate(localDate2);
        paramThrowable.setAppCrashDate(localDate1);
        if ((paramCrashManagerListener == null) || (paramCrashManagerListener.includeDeviceData()))
        {
          paramThrowable.setOsVersion(Constants.ANDROID_VERSION);
          paramThrowable.setOsBuild(Constants.ANDROID_BUILD);
          paramThrowable.setDeviceManufacturer(Constants.PHONE_MANUFACTURER);
          paramThrowable.setDeviceModel(Constants.PHONE_MODEL);
        }
        if ((paramThread != null) && ((paramCrashManagerListener == null) || (paramCrashManagerListener.includeThreadDetails()))) {
          paramThrowable.setThreadName(paramThread.getName() + "-" + paramThread.getId());
        }
        paramThread = Constants.DEVICE_IDENTIFIER;
        if ((paramThread != null) && ((paramCrashManagerListener == null) || (paramCrashManagerListener.includeDeviceIdentifier()))) {
          paramThrowable.setReporterKey(paramThread);
        }
        paramThrowable.writeCrashReport(localContext);
        if (paramCrashManagerListener != null) {
          try
          {
            paramThread = limitedString(paramCrashManagerListener.getUserID());
            paramThrowable = new java/lang/StringBuilder;
            paramThrowable.<init>();
            writeValueToFile(localContext, paramThread, str + ".user");
            paramThread = limitedString(paramCrashManagerListener.getContact());
            paramThrowable = new java/lang/StringBuilder;
            paramThrowable.<init>();
            writeValueToFile(localContext, paramThread, str + ".contact");
            paramThread = paramCrashManagerListener.getDescription();
            paramThrowable = new java/lang/StringBuilder;
            paramThrowable.<init>();
            writeValueToFile(localContext, paramThread, str + ".description");
          }
          catch (IOException paramThrowable)
          {
            HockeyLog.error("Error saving crash meta data!", paramThrowable);
          }
        }
      }
    }
  }
  
  /* Error */
  private static void writeValueToFile(Context paramContext, String paramString1, String paramString2)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 32	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore_3
    //   10: aconst_null
    //   11: astore 4
    //   13: aconst_null
    //   14: astore 5
    //   16: aload 4
    //   18: astore 6
    //   20: new 241	java/io/File
    //   23: astore 7
    //   25: aload 4
    //   27: astore 6
    //   29: aload 7
    //   31: aload_0
    //   32: invokevirtual 245	android/content/Context:getFilesDir	()Ljava/io/File;
    //   35: aload_2
    //   36: invokespecial 248	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   39: aload 5
    //   41: astore_0
    //   42: aload 4
    //   44: astore 6
    //   46: aload_1
    //   47: invokestatic 32	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   50: ifne +64 -> 114
    //   53: aload 5
    //   55: astore_0
    //   56: aload 4
    //   58: astore 6
    //   60: aload_1
    //   61: invokestatic 252	android/text/TextUtils:getTrimmedLength	(Ljava/lang/CharSequence;)I
    //   64: ifle +50 -> 114
    //   67: aload 4
    //   69: astore 6
    //   71: new 254	java/io/BufferedWriter
    //   74: astore_0
    //   75: aload 4
    //   77: astore 6
    //   79: new 256	java/io/FileWriter
    //   82: astore 5
    //   84: aload 4
    //   86: astore 6
    //   88: aload 5
    //   90: aload 7
    //   92: invokespecial 259	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   95: aload 4
    //   97: astore 6
    //   99: aload_0
    //   100: aload 5
    //   102: invokespecial 260	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   105: aload_0
    //   106: aload_1
    //   107: invokevirtual 263	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   110: aload_0
    //   111: invokevirtual 266	java/io/BufferedWriter:flush	()V
    //   114: aload_0
    //   115: ifnull -108 -> 7
    //   118: aload_0
    //   119: invokevirtual 269	java/io/BufferedWriter:close	()V
    //   122: goto -115 -> 7
    //   125: astore_1
    //   126: aload_3
    //   127: astore_0
    //   128: aload_0
    //   129: astore 6
    //   131: new 179	java/lang/StringBuilder
    //   134: astore_3
    //   135: aload_0
    //   136: astore 6
    //   138: aload_3
    //   139: invokespecial 180	java/lang/StringBuilder:<init>	()V
    //   142: aload_0
    //   143: astore 6
    //   145: aload_3
    //   146: ldc_w 271
    //   149: invokevirtual 189	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload_2
    //   153: invokevirtual 189	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: invokevirtual 198	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   159: aload_1
    //   160: invokestatic 239	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   163: aload_0
    //   164: ifnull -157 -> 7
    //   167: aload_0
    //   168: invokevirtual 269	java/io/BufferedWriter:close	()V
    //   171: goto -164 -> 7
    //   174: astore_1
    //   175: aload 6
    //   177: ifnull +8 -> 185
    //   180: aload 6
    //   182: invokevirtual 269	java/io/BufferedWriter:close	()V
    //   185: aload_1
    //   186: athrow
    //   187: astore_1
    //   188: aload_0
    //   189: astore 6
    //   191: goto -16 -> 175
    //   194: astore_1
    //   195: goto -67 -> 128
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	paramContext	Context
    //   0	198	1	paramString1	String
    //   0	198	2	paramString2	String
    //   9	137	3	localStringBuilder	StringBuilder
    //   11	85	4	localObject1	Object
    //   14	87	5	localFileWriter	java.io.FileWriter
    //   18	172	6	localObject2	Object
    //   23	68	7	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   20	25	125	java/io/IOException
    //   29	39	125	java/io/IOException
    //   46	53	125	java/io/IOException
    //   60	67	125	java/io/IOException
    //   71	75	125	java/io/IOException
    //   79	84	125	java/io/IOException
    //   88	95	125	java/io/IOException
    //   99	105	125	java/io/IOException
    //   20	25	174	finally
    //   29	39	174	finally
    //   46	53	174	finally
    //   60	67	174	finally
    //   71	75	174	finally
    //   79	84	174	finally
    //   88	95	174	finally
    //   99	105	174	finally
    //   131	135	174	finally
    //   138	142	174	finally
    //   145	163	174	finally
    //   105	114	187	finally
    //   105	114	194	java/io/IOException
  }
  
  public void setListener(CrashManagerListener paramCrashManagerListener)
  {
    this.mCrashManagerListener = paramCrashManagerListener;
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    Context localContext;
    if (CrashManager.weakContext != null)
    {
      localContext = (Context)CrashManager.weakContext.get();
      if ((localContext != null) && (localContext.getFilesDir() != null)) {
        break label44;
      }
      this.mDefaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
    }
    for (;;)
    {
      return;
      localContext = null;
      break;
      label44:
      saveException(paramThrowable, paramThread, this.mCrashManagerListener);
      if (!this.mIgnoreDefaultHandler)
      {
        this.mDefaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
      }
      else
      {
        Process.killProcess(Process.myPid());
        System.exit(10);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/ExceptionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */