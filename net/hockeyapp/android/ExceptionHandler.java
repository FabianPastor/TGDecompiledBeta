package net.hockeyapp.android;

import android.os.Process;
import android.text.TextUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    if ((Constants.CRASH_IDENTIFIER != null) && ((paramCrashManagerListener == null) || (paramCrashManagerListener.includeDeviceIdentifier()))) {
      paramThrowable.setReporterKey(Constants.CRASH_IDENTIFIER);
    }
    paramThrowable.writeCrashReport();
    if (paramCrashManagerListener != null) {}
    try
    {
      writeValueToFile(limitedString(paramCrashManagerListener.getUserID()), str + ".user");
      writeValueToFile(limitedString(paramCrashManagerListener.getContact()), str + ".contact");
      writeValueToFile(paramCrashManagerListener.getDescription(), str + ".description");
      return;
    }
    catch (IOException paramThrowable)
    {
      HockeyLog.error("Error saving crash meta data!", paramThrowable);
    }
  }
  
  @Deprecated
  public static void saveException(Throwable paramThrowable, CrashManagerListener paramCrashManagerListener)
  {
    saveException(paramThrowable, null, paramCrashManagerListener);
  }
  
  /* Error */
  private static void writeValueToFile(String paramString1, String paramString2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 32	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore_3
    //   10: aconst_null
    //   11: astore_2
    //   12: aconst_null
    //   13: astore 4
    //   15: new 151	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 152	java/lang/StringBuilder:<init>	()V
    //   22: getstatic 221	net/hockeyapp/android/Constants:FILES_PATH	Ljava/lang/String;
    //   25: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: ldc -33
    //   30: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_1
    //   34: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   40: astore 5
    //   42: aload 4
    //   44: astore_1
    //   45: aload_0
    //   46: invokestatic 32	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   49: ifne +39 -> 88
    //   52: aload 4
    //   54: astore_1
    //   55: aload_0
    //   56: invokestatic 227	android/text/TextUtils:getTrimmedLength	(Ljava/lang/CharSequence;)I
    //   59: ifle +29 -> 88
    //   62: new 229	java/io/BufferedWriter
    //   65: dup
    //   66: new 231	java/io/FileWriter
    //   69: dup
    //   70: aload 5
    //   72: invokespecial 233	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   75: invokespecial 234	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   78: astore_1
    //   79: aload_1
    //   80: aload_0
    //   81: invokevirtual 237	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   84: aload_1
    //   85: invokevirtual 240	java/io/BufferedWriter:flush	()V
    //   88: aload_1
    //   89: ifnull -82 -> 7
    //   92: aload_1
    //   93: invokevirtual 243	java/io/BufferedWriter:close	()V
    //   96: return
    //   97: astore_0
    //   98: aload_3
    //   99: astore_0
    //   100: aload_0
    //   101: ifnull -94 -> 7
    //   104: aload_0
    //   105: invokevirtual 243	java/io/BufferedWriter:close	()V
    //   108: return
    //   109: astore_1
    //   110: aload_2
    //   111: astore_0
    //   112: aload_0
    //   113: ifnull +7 -> 120
    //   116: aload_0
    //   117: invokevirtual 243	java/io/BufferedWriter:close	()V
    //   120: aload_1
    //   121: athrow
    //   122: astore_2
    //   123: aload_1
    //   124: astore_0
    //   125: aload_2
    //   126: astore_1
    //   127: goto -15 -> 112
    //   130: astore_0
    //   131: aload_1
    //   132: astore_0
    //   133: goto -33 -> 100
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	paramString1	String
    //   0	136	1	paramString2	String
    //   11	100	2	localObject1	Object
    //   122	4	2	localObject2	Object
    //   9	90	3	localObject3	Object
    //   13	40	4	localObject4	Object
    //   40	31	5	str	String
    // Exception table:
    //   from	to	target	type
    //   15	42	97	java/io/IOException
    //   45	52	97	java/io/IOException
    //   55	79	97	java/io/IOException
    //   15	42	109	finally
    //   45	52	109	finally
    //   55	79	109	finally
    //   79	88	122	finally
    //   79	88	130	java/io/IOException
  }
  
  public void setListener(CrashManagerListener paramCrashManagerListener)
  {
    this.mCrashManagerListener = paramCrashManagerListener;
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    if (Constants.FILES_PATH == null)
    {
      this.mDefaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
      return;
    }
    saveException(paramThrowable, paramThread, this.mCrashManagerListener);
    if (!this.mIgnoreDefaultHandler)
    {
      this.mDefaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
      return;
    }
    Process.killProcess(Process.myPid());
    System.exit(10);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/ExceptionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */