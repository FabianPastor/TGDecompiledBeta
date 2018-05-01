package net.hockeyapp.android.objects;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import net.hockeyapp.android.utils.HockeyLog;
import org.json.JSONException;

public class CrashDetails
{
  private Date appCrashDate;
  private String appPackage;
  private Date appStartDate;
  private String appVersionCode;
  private String appVersionName;
  private final String crashIdentifier;
  private String deviceManufacturer;
  private String deviceModel;
  private Boolean isXamarinException;
  private String osBuild;
  private String osVersion;
  private String reporterKey;
  private String threadName;
  private String throwableStackTrace;
  
  public CrashDetails(String paramString)
  {
    this.crashIdentifier = paramString;
    this.isXamarinException = Boolean.valueOf(false);
    this.throwableStackTrace = "";
  }
  
  public CrashDetails(String paramString, Throwable paramThrowable)
  {
    this(paramString);
    this.isXamarinException = Boolean.valueOf(false);
    paramString = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(paramString));
    this.throwableStackTrace = paramString.toString();
  }
  
  private void writeHeader(Writer paramWriter, String paramString1, String paramString2)
    throws IOException
  {
    paramWriter.write(paramString1 + ": " + paramString2 + "\n");
  }
  
  public void setAppCrashDate(Date paramDate)
  {
    this.appCrashDate = paramDate;
  }
  
  public void setAppPackage(String paramString)
  {
    this.appPackage = paramString;
  }
  
  public void setAppStartDate(Date paramDate)
  {
    this.appStartDate = paramDate;
  }
  
  public void setAppVersionCode(String paramString)
  {
    this.appVersionCode = paramString;
  }
  
  public void setAppVersionName(String paramString)
  {
    this.appVersionName = paramString;
  }
  
  public void setDeviceManufacturer(String paramString)
  {
    this.deviceManufacturer = paramString;
  }
  
  public void setDeviceModel(String paramString)
  {
    this.deviceModel = paramString;
  }
  
  public void setOsBuild(String paramString)
  {
    this.osBuild = paramString;
  }
  
  public void setOsVersion(String paramString)
  {
    this.osVersion = paramString;
  }
  
  public void setReporterKey(String paramString)
  {
    this.reporterKey = paramString;
  }
  
  public void setThreadName(String paramString)
  {
    this.threadName = paramString;
  }
  
  public void writeCrashReport(Context paramContext)
  {
    paramContext = new File(paramContext.getFilesDir(), this.crashIdentifier + ".stacktrace");
    try
    {
      writeCrashReport(paramContext);
      return;
    }
    catch (JSONException paramContext)
    {
      for (;;)
      {
        HockeyLog.error("Could not write crash report with error " + paramContext.toString());
      }
    }
  }
  
  /* Error */
  public void writeCrashReport(File paramFile)
    throws JSONException
  {
    // Byte code:
    //   0: new 68	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   7: ldc -108
    //   9: invokevirtual 73	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12: aload_1
    //   13: invokevirtual 151	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   16: invokevirtual 73	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   22: invokestatic 154	net/hockeyapp/android/utils/HockeyLog:debug	(Ljava/lang/String;)V
    //   25: aconst_null
    //   26: astore_2
    //   27: aconst_null
    //   28: astore_3
    //   29: aload_2
    //   30: astore 4
    //   32: new 156	java/io/BufferedWriter
    //   35: astore 5
    //   37: aload_2
    //   38: astore 4
    //   40: new 158	java/io/FileWriter
    //   43: astore 6
    //   45: aload_2
    //   46: astore 4
    //   48: aload 6
    //   50: aload_1
    //   51: invokespecial 160	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   54: aload_2
    //   55: astore 4
    //   57: aload 5
    //   59: aload 6
    //   61: invokespecial 161	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   64: aload_0
    //   65: aload 5
    //   67: ldc -93
    //   69: aload_0
    //   70: getfield 91	net/hockeyapp/android/objects/CrashDetails:appPackage	Ljava/lang/String;
    //   73: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   76: aload_0
    //   77: aload 5
    //   79: ldc -89
    //   81: aload_0
    //   82: getfield 97	net/hockeyapp/android/objects/CrashDetails:appVersionCode	Ljava/lang/String;
    //   85: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   88: aload_0
    //   89: aload 5
    //   91: ldc -87
    //   93: aload_0
    //   94: getfield 100	net/hockeyapp/android/objects/CrashDetails:appVersionName	Ljava/lang/String;
    //   97: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   100: aload_0
    //   101: aload 5
    //   103: ldc -85
    //   105: aload_0
    //   106: getfield 112	net/hockeyapp/android/objects/CrashDetails:osVersion	Ljava/lang/String;
    //   109: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload_0
    //   113: aload 5
    //   115: ldc -83
    //   117: aload_0
    //   118: getfield 109	net/hockeyapp/android/objects/CrashDetails:osBuild	Ljava/lang/String;
    //   121: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   124: aload_0
    //   125: aload 5
    //   127: ldc -81
    //   129: aload_0
    //   130: getfield 103	net/hockeyapp/android/objects/CrashDetails:deviceManufacturer	Ljava/lang/String;
    //   133: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   136: aload_0
    //   137: aload 5
    //   139: ldc -79
    //   141: aload_0
    //   142: getfield 106	net/hockeyapp/android/objects/CrashDetails:deviceModel	Ljava/lang/String;
    //   145: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   148: aload_0
    //   149: aload 5
    //   151: ldc -77
    //   153: aload_0
    //   154: getfield 118	net/hockeyapp/android/objects/CrashDetails:threadName	Ljava/lang/String;
    //   157: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   160: aload_0
    //   161: aload 5
    //   163: ldc -75
    //   165: aload_0
    //   166: getfield 115	net/hockeyapp/android/objects/CrashDetails:reporterKey	Ljava/lang/String;
    //   169: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   172: aload_0
    //   173: aload 5
    //   175: ldc -73
    //   177: aload_0
    //   178: getfield 94	net/hockeyapp/android/objects/CrashDetails:appStartDate	Ljava/util/Date;
    //   181: invokestatic 188	net/hockeyapp/android/utils/JSONDateUtils:toString	(Ljava/util/Date;)Ljava/lang/String;
    //   184: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   187: aload_0
    //   188: aload 5
    //   190: ldc -66
    //   192: aload_0
    //   193: getfield 88	net/hockeyapp/android/objects/CrashDetails:appCrashDate	Ljava/util/Date;
    //   196: invokestatic 188	net/hockeyapp/android/utils/JSONDateUtils:toString	(Ljava/util/Date;)Ljava/lang/String;
    //   199: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   202: aload_0
    //   203: getfield 36	net/hockeyapp/android/objects/CrashDetails:isXamarinException	Ljava/lang/Boolean;
    //   206: invokevirtual 194	java/lang/Boolean:booleanValue	()Z
    //   209: ifeq +13 -> 222
    //   212: aload_0
    //   213: aload 5
    //   215: ldc -60
    //   217: ldc -58
    //   219: invokespecial 165	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   222: aload 5
    //   224: ldc 77
    //   226: invokevirtual 199	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   229: aload 5
    //   231: aload_0
    //   232: getfield 40	net/hockeyapp/android/objects/CrashDetails:throwableStackTrace	Ljava/lang/String;
    //   235: invokevirtual 199	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   238: aload 5
    //   240: invokevirtual 202	java/io/BufferedWriter:flush	()V
    //   243: aload 5
    //   245: ifnull +8 -> 253
    //   248: aload 5
    //   250: invokevirtual 205	java/io/BufferedWriter:close	()V
    //   253: return
    //   254: astore_1
    //   255: ldc -49
    //   257: aload_1
    //   258: invokestatic 209	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   261: goto -8 -> 253
    //   264: astore 5
    //   266: aload_3
    //   267: astore_1
    //   268: aload_1
    //   269: astore 4
    //   271: ldc -49
    //   273: aload 5
    //   275: invokestatic 209	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   278: aload_1
    //   279: ifnull -26 -> 253
    //   282: aload_1
    //   283: invokevirtual 205	java/io/BufferedWriter:close	()V
    //   286: goto -33 -> 253
    //   289: astore_1
    //   290: ldc -49
    //   292: aload_1
    //   293: invokestatic 209	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   296: goto -43 -> 253
    //   299: astore_1
    //   300: aload 4
    //   302: ifnull +8 -> 310
    //   305: aload 4
    //   307: invokevirtual 205	java/io/BufferedWriter:close	()V
    //   310: aload_1
    //   311: athrow
    //   312: astore 4
    //   314: ldc -49
    //   316: aload 4
    //   318: invokestatic 209	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   321: goto -11 -> 310
    //   324: astore_1
    //   325: aload 5
    //   327: astore 4
    //   329: goto -29 -> 300
    //   332: astore_1
    //   333: aload 5
    //   335: astore 4
    //   337: aload_1
    //   338: astore 5
    //   340: aload 4
    //   342: astore_1
    //   343: goto -75 -> 268
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	346	0	this	CrashDetails
    //   0	346	1	paramFile	File
    //   26	29	2	localObject1	Object
    //   28	239	3	localObject2	Object
    //   30	276	4	localObject3	Object
    //   312	5	4	localIOException1	IOException
    //   327	14	4	localObject4	Object
    //   35	214	5	localBufferedWriter	java.io.BufferedWriter
    //   264	70	5	localIOException2	IOException
    //   338	1	5	localFile	File
    //   43	17	6	localFileWriter	java.io.FileWriter
    // Exception table:
    //   from	to	target	type
    //   248	253	254	java/io/IOException
    //   32	37	264	java/io/IOException
    //   40	45	264	java/io/IOException
    //   48	54	264	java/io/IOException
    //   57	64	264	java/io/IOException
    //   282	286	289	java/io/IOException
    //   32	37	299	finally
    //   40	45	299	finally
    //   48	54	299	finally
    //   57	64	299	finally
    //   271	278	299	finally
    //   305	310	312	java/io/IOException
    //   64	222	324	finally
    //   222	243	324	finally
    //   64	222	332	java/io/IOException
    //   222	243	332	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/CrashDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */