package net.hockeyapp.android.metrics;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import net.hockeyapp.android.utils.HockeyLog;

class Persistence
{
  private static final String BIT_TELEMETRY_DIRECTORY = "/net.hockeyapp.android/telemetry/";
  private static final Object LOCK = new Object();
  private static final Integer MAX_FILE_COUNT = Integer.valueOf(50);
  private static final String TAG = "HA-MetricsPersistence";
  protected ArrayList<File> mServedFiles;
  protected final File mTelemetryDirectory;
  private final WeakReference<Context> mWeakContext;
  protected WeakReference<Sender> mWeakSender;
  
  protected Persistence(Context paramContext, File paramFile, Sender paramSender)
  {
    this.mWeakContext = new WeakReference(paramContext);
    this.mServedFiles = new ArrayList(51);
    this.mTelemetryDirectory = paramFile;
    this.mWeakSender = new WeakReference(paramSender);
    createDirectoriesIfNecessary();
  }
  
  protected Persistence(Context paramContext, Sender paramSender)
  {
    this(paramContext, new File(paramContext.getFilesDir().getAbsolutePath() + "/net.hockeyapp.android/telemetry/"), null);
    setSender(paramSender);
  }
  
  private Context getContext()
  {
    Context localContext = null;
    if (this.mWeakContext != null) {
      localContext = (Context)this.mWeakContext.get();
    }
    return localContext;
  }
  
  protected void createDirectoriesIfNecessary()
  {
    if ((this.mTelemetryDirectory != null) && (!this.mTelemetryDirectory.exists()))
    {
      if (this.mTelemetryDirectory.mkdirs()) {
        HockeyLog.info("HA-MetricsPersistence", "Successfully created directory");
      }
    }
    else {
      return;
    }
    HockeyLog.info("HA-MetricsPersistence", "Error creating directory");
  }
  
  protected void deleteFile(File paramFile)
  {
    if (paramFile != null) {
      synchronized (LOCK)
      {
        if (!paramFile.delete())
        {
          HockeyLog.warn("HA-MetricsPersistence", "Error deleting telemetry file " + paramFile.toString());
          return;
        }
        HockeyLog.warn("HA-MetricsPersistence", "Successfully deleted telemetry file at: " + paramFile.toString());
        this.mServedFiles.remove(paramFile);
      }
    }
    HockeyLog.warn("HA-MetricsPersistence", "Couldn't delete file, the reference to the file was null");
  }
  
  protected Sender getSender()
  {
    Sender localSender = null;
    if (this.mWeakSender != null) {
      localSender = (Sender)this.mWeakSender.get();
    }
    return localSender;
  }
  
  protected boolean hasFilesAvailable()
  {
    return nextAvailableFileInDirectory() != null;
  }
  
  protected boolean isFreeSpaceAvailable()
  {
    boolean bool2 = false;
    synchronized (LOCK)
    {
      Object localObject2 = getContext();
      if (((Context)localObject2).getFilesDir() != null)
      {
        localObject2 = ((Context)localObject2).getFilesDir();
        localObject2 = ((File)localObject2).getAbsolutePath() + "/net.hockeyapp.android/telemetry/";
        if (!TextUtils.isEmpty((CharSequence)localObject2))
        {
          localObject2 = new File((String)localObject2).listFiles();
          boolean bool1 = bool2;
          if (localObject2 != null)
          {
            bool1 = bool2;
            if (localObject2.length < MAX_FILE_COUNT.intValue()) {
              bool1 = true;
            }
          }
          return bool1;
        }
      }
      return false;
    }
  }
  
  protected String load(File paramFile)
  {
    localStringBuilder = new StringBuilder();
    Object localObject6;
    Object localObject7;
    Object localObject3;
    if (paramFile != null)
    {
      localObject6 = null;
      localObject7 = null;
      localObject3 = null;
      localObject1 = localObject3;
      localObject2 = localObject6;
    }
    for (;;)
    {
      try
      {
        localObject8 = LOCK;
        localObject1 = localObject3;
        localObject2 = localObject6;
        localObject1 = localObject7;
      }
      catch (Exception paramFile)
      {
        Object localObject8;
        localObject2 = localObject1;
        HockeyLog.warn("HA-MetricsPersistence", "Error reading telemetry data from file with exception message " + paramFile.getMessage());
        if (localObject1 != null) {}
        try
        {
          ((BufferedReader)localObject1).close();
          return localStringBuilder.toString();
          if (paramFile == null) {
            continue;
          }
          try
          {
            paramFile.close();
          }
          catch (IOException paramFile)
          {
            HockeyLog.warn("HA-MetricsPersistence", "Error closing stream." + paramFile.getMessage());
          }
          continue;
        }
        catch (IOException paramFile)
        {
          HockeyLog.warn("HA-MetricsPersistence", "Error closing stream." + paramFile.getMessage());
          continue;
        }
      }
      finally
      {
        if (localObject2 != null) {}
        try
        {
          ((BufferedReader)localObject2).close();
          throw paramFile;
        }
        catch (IOException localIOException)
        {
          HockeyLog.warn("HA-MetricsPersistence", "Error closing stream." + localIOException.getMessage());
          continue;
        }
      }
      try
      {
        paramFile = new BufferedReader(new InputStreamReader(new FileInputStream(paramFile)));
        try
        {
          int i = paramFile.read();
          if (i == -1) {
            continue;
          }
          localStringBuilder.append((char)i);
          continue;
          localObject1 = paramFile;
        }
        finally {}
      }
      finally
      {
        paramFile = localIOException;
      }
    }
    localObject1 = paramFile;
    localObject2 = paramFile;
    throw ((Throwable)localObject4);
  }
  
  protected void makeAvailable(File paramFile)
  {
    Object localObject = LOCK;
    if (paramFile != null) {}
    try
    {
      this.mServedFiles.remove(paramFile);
      return;
    }
    finally {}
  }
  
  protected File nextAvailableFileInDirectory()
  {
    synchronized (LOCK)
    {
      if (this.mTelemetryDirectory != null)
      {
        File[] arrayOfFile = this.mTelemetryDirectory.listFiles();
        if ((arrayOfFile != null) && (arrayOfFile.length > 0))
        {
          int i = 0;
          while (i <= arrayOfFile.length - 1)
          {
            File localFile = arrayOfFile[i];
            if (!this.mServedFiles.contains(localFile))
            {
              HockeyLog.info("HA-MetricsPersistence", "The directory " + localFile.toString() + " (ADDING TO SERVED AND RETURN)");
              this.mServedFiles.add(localFile);
              return localFile;
            }
            HockeyLog.info("HA-MetricsPersistence", "The directory " + localFile.toString() + " (WAS ALREADY SERVED)");
            i += 1;
          }
        }
      }
      if (this.mTelemetryDirectory != null) {
        HockeyLog.info("HA-MetricsPersistence", "The directory " + this.mTelemetryDirectory.toString() + " did not contain any " + "unserved files");
      }
      return null;
    }
  }
  
  protected void persist(String[] paramArrayOfString)
  {
    if (!isFreeSpaceAvailable())
    {
      HockeyLog.warn("HA-MetricsPersistence", "Failed to persist file: Too many files on disk.");
      getSender().triggerSending();
    }
    StringBuilder localStringBuilder;
    do
    {
      return;
      localStringBuilder = new StringBuilder();
      int j = paramArrayOfString.length;
      int i = 0;
      while (i < j)
      {
        String str = paramArrayOfString[i];
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append('\n');
        }
        localStringBuilder.append(str);
        i += 1;
      }
    } while (!writeToDisk(localStringBuilder.toString()));
    getSender().triggerSending();
  }
  
  protected void setSender(Sender paramSender)
  {
    this.mWeakSender = new WeakReference(paramSender);
  }
  
  /* Error */
  protected boolean writeToDisk(String arg1)
  {
    // Byte code:
    //   0: invokestatic 240	java/util/UUID:randomUUID	()Ljava/util/UUID;
    //   3: invokevirtual 241	java/util/UUID:toString	()Ljava/lang/String;
    //   6: astore 9
    //   8: iconst_0
    //   9: invokestatic 246	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   12: astore 4
    //   14: aconst_null
    //   15: astore 6
    //   17: aconst_null
    //   18: astore 7
    //   20: aconst_null
    //   21: astore 5
    //   23: aload 6
    //   25: astore_3
    //   26: aload 7
    //   28: astore_2
    //   29: getstatic 32	net/hockeyapp/android/metrics/Persistence:LOCK	Ljava/lang/Object;
    //   32: astore 8
    //   34: aload 6
    //   36: astore_3
    //   37: aload 7
    //   39: astore_2
    //   40: aload 8
    //   42: monitorenter
    //   43: aload 5
    //   45: astore_3
    //   46: new 66	java/io/File
    //   49: dup
    //   50: new 68	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   57: aload_0
    //   58: getfield 58	net/hockeyapp/android/metrics/Persistence:mTelemetryDirectory	Ljava/io/File;
    //   61: invokevirtual 249	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   64: ldc -5
    //   66: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload 9
    //   71: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   77: invokespecial 89	java/io/File:<init>	(Ljava/lang/String;)V
    //   80: astore 6
    //   82: aload 5
    //   84: astore_3
    //   85: new 253	java/io/FileOutputStream
    //   88: dup
    //   89: aload 6
    //   91: iconst_1
    //   92: invokespecial 256	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   95: astore_2
    //   96: aload_2
    //   97: aload_1
    //   98: invokevirtual 262	java/lang/String:getBytes	()[B
    //   101: invokevirtual 266	java/io/FileOutputStream:write	([B)V
    //   104: ldc 15
    //   106: new 68	java/lang/StringBuilder
    //   109: dup
    //   110: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   113: ldc_w 268
    //   116: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: aload 6
    //   121: invokevirtual 126	java/io/File:toString	()Ljava/lang/String;
    //   124: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   130: invokestatic 129	net/hockeyapp/android/utils/HockeyLog:warn	(Ljava/lang/String;Ljava/lang/String;)V
    //   133: aload 8
    //   135: monitorexit
    //   136: iconst_1
    //   137: invokestatic 246	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   140: astore_1
    //   141: aload_2
    //   142: ifnull +122 -> 264
    //   145: aload_2
    //   146: invokevirtual 269	java/io/FileOutputStream:close	()V
    //   149: aload_1
    //   150: invokevirtual 272	java/lang/Boolean:booleanValue	()Z
    //   153: ireturn
    //   154: astore_1
    //   155: aload_3
    //   156: astore_2
    //   157: aload_2
    //   158: astore_3
    //   159: aload 8
    //   161: monitorexit
    //   162: aload_2
    //   163: astore_3
    //   164: aload_1
    //   165: athrow
    //   166: astore_1
    //   167: aload_3
    //   168: astore_2
    //   169: ldc 15
    //   171: new 68	java/lang/StringBuilder
    //   174: dup
    //   175: invokespecial 69	java/lang/StringBuilder:<init>	()V
    //   178: ldc_w 274
    //   181: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: aload_1
    //   185: invokevirtual 275	java/lang/Exception:toString	()Ljava/lang/String;
    //   188: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: invokestatic 129	net/hockeyapp/android/utils/HockeyLog:warn	(Ljava/lang/String;Ljava/lang/String;)V
    //   197: aload 4
    //   199: astore_1
    //   200: aload_3
    //   201: ifnull -52 -> 149
    //   204: aload_3
    //   205: invokevirtual 269	java/io/FileOutputStream:close	()V
    //   208: aload 4
    //   210: astore_1
    //   211: goto -62 -> 149
    //   214: astore_1
    //   215: aload_1
    //   216: invokevirtual 278	java/io/IOException:printStackTrace	()V
    //   219: aload 4
    //   221: astore_1
    //   222: goto -73 -> 149
    //   225: astore_2
    //   226: aload_2
    //   227: invokevirtual 278	java/io/IOException:printStackTrace	()V
    //   230: goto -81 -> 149
    //   233: astore_1
    //   234: aload_2
    //   235: ifnull +7 -> 242
    //   238: aload_2
    //   239: invokevirtual 269	java/io/FileOutputStream:close	()V
    //   242: aload_1
    //   243: athrow
    //   244: astore_2
    //   245: aload_2
    //   246: invokevirtual 278	java/io/IOException:printStackTrace	()V
    //   249: goto -7 -> 242
    //   252: astore_1
    //   253: goto -19 -> 234
    //   256: astore_1
    //   257: goto -90 -> 167
    //   260: astore_1
    //   261: goto -104 -> 157
    //   264: goto -115 -> 149
    // Exception table:
    //   from	to	target	type
    //   46	82	154	finally
    //   85	96	154	finally
    //   159	162	154	finally
    //   29	34	166	java/lang/Exception
    //   40	43	166	java/lang/Exception
    //   164	166	166	java/lang/Exception
    //   204	208	214	java/io/IOException
    //   145	149	225	java/io/IOException
    //   29	34	233	finally
    //   40	43	233	finally
    //   164	166	233	finally
    //   169	197	233	finally
    //   238	242	244	java/io/IOException
    //   96	136	260	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/Persistence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */