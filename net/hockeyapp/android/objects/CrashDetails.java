package net.hockeyapp.android.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.hockeyapp.android.utils.HockeyLog;

public class CrashDetails
{
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
  private static final String FIELD_APP_CRASH_DATE = "Date";
  private static final String FIELD_APP_PACKAGE = "Package";
  private static final String FIELD_APP_START_DATE = "Start Date";
  private static final String FIELD_APP_VERSION_CODE = "Version Code";
  private static final String FIELD_APP_VERSION_NAME = "Version Name";
  private static final String FIELD_CRASH_REPORTER_KEY = "CrashReporter Key";
  private static final String FIELD_DEVICE_MANUFACTURER = "Manufacturer";
  private static final String FIELD_DEVICE_MODEL = "Model";
  private static final String FIELD_OS_BUILD = "Android Build";
  private static final String FIELD_OS_VERSION = "Android";
  private static final String FIELD_THREAD_NAME = "Thread";
  private Date appCrashDate;
  private String appPackage;
  private Date appStartDate;
  private String appVersionCode;
  private String appVersionName;
  private final String crashIdentifier;
  private String deviceManufacturer;
  private String deviceModel;
  private String osBuild;
  private String osVersion;
  private String reporterKey;
  private String threadName;
  private String throwableStackTrace;
  
  public CrashDetails(String paramString)
  {
    this.crashIdentifier = paramString;
  }
  
  public CrashDetails(String paramString, Throwable paramThrowable)
  {
    this(paramString);
    paramString = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(paramString));
    this.throwableStackTrace = paramString.toString();
  }
  
  public static CrashDetails fromFile(File paramFile)
    throws IOException
  {
    return fromReader(paramFile.getName().substring(0, paramFile.getName().indexOf(".stacktrace")), new FileReader(paramFile));
  }
  
  public static CrashDetails fromReader(String paramString, Reader paramReader)
    throws IOException
  {
    paramReader = new BufferedReader(paramReader);
    paramString = new CrashDetails(paramString);
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;)
    {
      String str2 = paramReader.readLine();
      if (str2 == null) {
        break;
      }
      if (i == 0)
      {
        if (str2.isEmpty())
        {
          i = 1;
        }
        else
        {
          int j = str2.indexOf(":");
          if (j < 0) {
            HockeyLog.error("Malformed header line when parsing crash details: \"" + str2 + "\"");
          }
          String str1 = str2.substring(0, j).trim();
          str2 = str2.substring(j + 1, str2.length()).trim();
          if (str1.equals("CrashReporter Key")) {
            paramString.setReporterKey(str2);
          } else if (str1.equals("Start Date")) {
            try
            {
              paramString.setAppStartDate(DATE_FORMAT.parse(str2));
            }
            catch (ParseException paramString)
            {
              throw new RuntimeException(paramString);
            }
          } else if (str1.equals("Date")) {
            try
            {
              paramString.setAppCrashDate(DATE_FORMAT.parse(str2));
            }
            catch (ParseException paramString)
            {
              throw new RuntimeException(paramString);
            }
          } else if (str1.equals("Android")) {
            paramString.setOsVersion(str2);
          } else if (str1.equals("Android Build")) {
            paramString.setOsBuild(str2);
          } else if (str1.equals("Manufacturer")) {
            paramString.setDeviceManufacturer(str2);
          } else if (str1.equals("Model")) {
            paramString.setDeviceModel(str2);
          } else if (str1.equals("Package")) {
            paramString.setAppPackage(str2);
          } else if (str1.equals("Version Name")) {
            paramString.setAppVersionName(str2);
          } else if (str1.equals("Version Code")) {
            paramString.setAppVersionCode(str2);
          } else if (str1.equals("Thread")) {
            paramString.setThreadName(str2);
          }
        }
      }
      else {
        localStringBuilder.append(str2).append("\n");
      }
    }
    paramString.setThrowableStackTrace(localStringBuilder.toString());
    return paramString;
  }
  
  private void writeHeader(Writer paramWriter, String paramString1, String paramString2)
    throws IOException
  {
    paramWriter.write(paramString1 + ": " + paramString2 + "\n");
  }
  
  public Date getAppCrashDate()
  {
    return this.appCrashDate;
  }
  
  public String getAppPackage()
  {
    return this.appPackage;
  }
  
  public Date getAppStartDate()
  {
    return this.appStartDate;
  }
  
  public String getAppVersionCode()
  {
    return this.appVersionCode;
  }
  
  public String getAppVersionName()
  {
    return this.appVersionName;
  }
  
  public String getCrashIdentifier()
  {
    return this.crashIdentifier;
  }
  
  public String getDeviceManufacturer()
  {
    return this.deviceManufacturer;
  }
  
  public String getDeviceModel()
  {
    return this.deviceModel;
  }
  
  public String getOsBuild()
  {
    return this.osBuild;
  }
  
  public String getOsVersion()
  {
    return this.osVersion;
  }
  
  public String getReporterKey()
  {
    return this.reporterKey;
  }
  
  public String getThreadName()
  {
    return this.threadName;
  }
  
  public String getThrowableStackTrace()
  {
    return this.throwableStackTrace;
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
  
  public void setThrowableStackTrace(String paramString)
  {
    this.throwableStackTrace = paramString;
  }
  
  /* Error */
  public void writeCrashReport()
  {
    // Byte code:
    //   0: new 141	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 142	java/lang/StringBuilder:<init>	()V
    //   7: getstatic 275	net/hockeyapp/android/Constants:FILES_PATH	Ljava/lang/String;
    //   10: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: ldc_w 277
    //   16: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: aload_0
    //   20: getfield 78	net/hockeyapp/android/objects/CrashDetails:crashIdentifier	Ljava/lang/String;
    //   23: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: ldc 112
    //   28: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   34: astore_2
    //   35: new 141	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 142	java/lang/StringBuilder:<init>	()V
    //   42: ldc_w 279
    //   45: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload_2
    //   49: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 282	net/hockeyapp/android/utils/HockeyLog:debug	(Ljava/lang/String;)V
    //   58: aconst_null
    //   59: astore_1
    //   60: aconst_null
    //   61: astore_3
    //   62: new 284	java/io/BufferedWriter
    //   65: dup
    //   66: new 286	java/io/FileWriter
    //   69: dup
    //   70: aload_2
    //   71: invokespecial 287	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   74: invokespecial 288	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   77: astore_2
    //   78: aload_0
    //   79: aload_2
    //   80: ldc 13
    //   82: aload_0
    //   83: getfield 240	net/hockeyapp/android/objects/CrashDetails:appPackage	Ljava/lang/String;
    //   86: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   89: aload_0
    //   90: aload_2
    //   91: ldc 19
    //   93: aload_0
    //   94: getfield 246	net/hockeyapp/android/objects/CrashDetails:appVersionCode	Ljava/lang/String;
    //   97: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   100: aload_0
    //   101: aload_2
    //   102: ldc 22
    //   104: aload_0
    //   105: getfield 249	net/hockeyapp/android/objects/CrashDetails:appVersionName	Ljava/lang/String;
    //   108: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   111: aload_0
    //   112: aload_2
    //   113: ldc 37
    //   115: aload_0
    //   116: getfield 262	net/hockeyapp/android/objects/CrashDetails:osVersion	Ljava/lang/String;
    //   119: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   122: aload_0
    //   123: aload_2
    //   124: ldc 34
    //   126: aload_0
    //   127: getfield 259	net/hockeyapp/android/objects/CrashDetails:osBuild	Ljava/lang/String;
    //   130: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   133: aload_0
    //   134: aload_2
    //   135: ldc 28
    //   137: aload_0
    //   138: getfield 253	net/hockeyapp/android/objects/CrashDetails:deviceManufacturer	Ljava/lang/String;
    //   141: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   144: aload_0
    //   145: aload_2
    //   146: ldc 31
    //   148: aload_0
    //   149: getfield 256	net/hockeyapp/android/objects/CrashDetails:deviceModel	Ljava/lang/String;
    //   152: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   155: aload_0
    //   156: aload_2
    //   157: ldc 40
    //   159: aload_0
    //   160: getfield 268	net/hockeyapp/android/objects/CrashDetails:threadName	Ljava/lang/String;
    //   163: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   166: aload_0
    //   167: aload_2
    //   168: ldc 25
    //   170: aload_0
    //   171: getfield 265	net/hockeyapp/android/objects/CrashDetails:reporterKey	Ljava/lang/String;
    //   174: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   177: aload_0
    //   178: aload_2
    //   179: ldc 16
    //   181: getstatic 72	net/hockeyapp/android/objects/CrashDetails:DATE_FORMAT	Ljava/text/SimpleDateFormat;
    //   184: aload_0
    //   185: getfield 243	net/hockeyapp/android/objects/CrashDetails:appStartDate	Ljava/util/Date;
    //   188: invokevirtual 294	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   191: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   194: aload_0
    //   195: aload_2
    //   196: ldc 10
    //   198: getstatic 72	net/hockeyapp/android/objects/CrashDetails:DATE_FORMAT	Ljava/text/SimpleDateFormat;
    //   201: aload_0
    //   202: getfield 237	net/hockeyapp/android/objects/CrashDetails:appCrashDate	Ljava/util/Date;
    //   205: invokevirtual 294	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   208: invokespecial 290	net/hockeyapp/android/objects/CrashDetails:writeHeader	(Ljava/io/Writer;Ljava/lang/String;Ljava/lang/String;)V
    //   211: aload_2
    //   212: ldc -35
    //   214: invokevirtual 295	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   217: aload_2
    //   218: aload_0
    //   219: getfield 101	net/hockeyapp/android/objects/CrashDetails:throwableStackTrace	Ljava/lang/String;
    //   222: invokevirtual 295	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   225: aload_2
    //   226: invokevirtual 298	java/io/BufferedWriter:flush	()V
    //   229: aload_2
    //   230: ifnull +7 -> 237
    //   233: aload_2
    //   234: invokevirtual 301	java/io/BufferedWriter:close	()V
    //   237: return
    //   238: astore_1
    //   239: ldc_w 303
    //   242: aload_1
    //   243: invokestatic 305	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   246: return
    //   247: astore_1
    //   248: aload_3
    //   249: astore_2
    //   250: aload_1
    //   251: astore_3
    //   252: aload_2
    //   253: astore_1
    //   254: ldc_w 303
    //   257: aload_3
    //   258: invokestatic 305	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   261: aload_2
    //   262: ifnull -25 -> 237
    //   265: aload_2
    //   266: invokevirtual 301	java/io/BufferedWriter:close	()V
    //   269: return
    //   270: astore_1
    //   271: ldc_w 303
    //   274: aload_1
    //   275: invokestatic 305	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   278: return
    //   279: astore_2
    //   280: aload_1
    //   281: ifnull +7 -> 288
    //   284: aload_1
    //   285: invokevirtual 301	java/io/BufferedWriter:close	()V
    //   288: aload_2
    //   289: athrow
    //   290: astore_1
    //   291: ldc_w 303
    //   294: aload_1
    //   295: invokestatic 305	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   298: goto -10 -> 288
    //   301: astore_3
    //   302: aload_2
    //   303: astore_1
    //   304: aload_3
    //   305: astore_2
    //   306: goto -26 -> 280
    //   309: astore_3
    //   310: goto -58 -> 252
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	313	0	this	CrashDetails
    //   59	1	1	localObject1	Object
    //   238	5	1	localIOException1	IOException
    //   247	4	1	localIOException2	IOException
    //   253	1	1	localObject2	Object
    //   270	15	1	localIOException3	IOException
    //   290	5	1	localIOException4	IOException
    //   303	1	1	localObject3	Object
    //   34	232	2	localObject4	Object
    //   279	24	2	localObject5	Object
    //   305	1	2	localObject6	Object
    //   61	197	3	localIOException5	IOException
    //   301	4	3	localObject7	Object
    //   309	1	3	localIOException6	IOException
    // Exception table:
    //   from	to	target	type
    //   233	237	238	java/io/IOException
    //   62	78	247	java/io/IOException
    //   265	269	270	java/io/IOException
    //   62	78	279	finally
    //   254	261	279	finally
    //   284	288	290	java/io/IOException
    //   78	229	301	finally
    //   78	229	309	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/CrashDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */