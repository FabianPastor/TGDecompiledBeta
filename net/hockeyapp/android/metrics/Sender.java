package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;

public class Sender
{
  static final String DEFAULT_ENDPOINT_URL = "https://gate.hockeyapp.net/v2/track";
  static final int DEFAULT_SENDER_CONNECT_TIMEOUT = 15000;
  static final int DEFAULT_SENDER_READ_TIMEOUT = 10000;
  static final int MAX_REQUEST_COUNT = 10;
  private static final String TAG = "HockeyApp-Metrics";
  private String mCustomServerURL;
  private AtomicInteger mRequestCount = new AtomicInteger(0);
  protected WeakReference<Persistence> mWeakPersistence;
  
  private void logRequest(HttpURLConnection paramHttpURLConnection, String paramString)
  {
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramHttpURLConnection != null)
    {
      localObject1 = localObject2;
      if (paramString != null)
      {
        localObject2 = localObject3;
        localObject1 = localObject4;
      }
    }
    for (;;)
    {
      try
      {
        HockeyLog.debug("HockeyApp-Metrics", "Sending payload:\n" + paramString);
        localObject2 = localObject3;
        localObject1 = localObject4;
        HockeyLog.debug("HockeyApp-Metrics", "Using URL:" + paramHttpURLConnection.getURL().toString());
        localObject2 = localObject3;
        localObject1 = localObject4;
        paramHttpURLConnection = getWriter(paramHttpURLConnection);
        localObject2 = paramHttpURLConnection;
        localObject1 = paramHttpURLConnection;
        paramHttpURLConnection.write(paramString);
        localObject2 = paramHttpURLConnection;
        localObject1 = paramHttpURLConnection;
        paramHttpURLConnection.flush();
        localObject1 = paramHttpURLConnection;
      }
      catch (IOException paramHttpURLConnection)
      {
        localObject1 = localObject2;
        HockeyLog.debug("HockeyApp-Metrics", "Couldn't log data with: " + paramHttpURLConnection.toString());
        if (localObject2 == null) {
          continue;
        }
        try
        {
          ((Writer)localObject2).close();
          return;
        }
        catch (IOException paramHttpURLConnection)
        {
          HockeyLog.error("HockeyApp-Metrics", "Couldn't close writer with: " + paramHttpURLConnection.toString());
          return;
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label243;
        }
      }
      try
      {
        ((Writer)localObject1).close();
        return;
      }
      catch (IOException paramHttpURLConnection)
      {
        HockeyLog.error("HockeyApp-Metrics", "Couldn't close writer with: " + paramHttpURLConnection.toString());
        return;
      }
    }
    try
    {
      ((Writer)localObject1).close();
      label243:
      throw paramHttpURLConnection;
    }
    catch (IOException paramString)
    {
      for (;;)
      {
        HockeyLog.error("HockeyApp-Metrics", "Couldn't close writer with: " + paramString.toString());
      }
    }
  }
  
  protected HttpURLConnection createConnection()
  {
    Object localObject3 = null;
    localObject2 = localObject3;
    try
    {
      Object localObject1;
      if (getCustomServerURL() == null)
      {
        localObject2 = localObject3;
        localObject1 = new URL("https://gate.hockeyapp.net/v2/track");
      }
      for (;;)
      {
        localObject2 = localObject3;
        localObject1 = (HttpURLConnection)((URL)localObject1).openConnection();
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setReadTimeout(10000);
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setConnectTimeout(15000);
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setRequestMethod("POST");
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setRequestProperty("Content-Type", "application/x-json-stream");
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setDoOutput(true);
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setDoInput(true);
        localObject2 = localObject1;
        ((HttpURLConnection)localObject1).setUseCaches(false);
        return (HttpURLConnection)localObject1;
        localObject2 = localObject3;
        URL localURL = new URL(this.mCustomServerURL);
        localObject1 = localURL;
        if (localURL == null)
        {
          localObject2 = localObject3;
          localObject1 = new URL("https://gate.hockeyapp.net/v2/track");
        }
      }
      return (HttpURLConnection)localObject2;
    }
    catch (IOException localIOException)
    {
      HockeyLog.error("HockeyApp-Metrics", "Could not open connection for provided URL with exception: ", localIOException);
    }
  }
  
  protected String getCustomServerURL()
  {
    return this.mCustomServerURL;
  }
  
  protected Persistence getPersistence()
  {
    Persistence localPersistence = null;
    if (this.mWeakPersistence != null) {
      localPersistence = (Persistence)this.mWeakPersistence.get();
    }
    return localPersistence;
  }
  
  @TargetApi(19)
  protected Writer getWriter(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      paramHttpURLConnection.addRequestProperty("Content-Encoding", "gzip");
      paramHttpURLConnection.setRequestProperty("Content-Type", "application/x-json-stream");
      return new OutputStreamWriter(new GZIPOutputStream(paramHttpURLConnection.getOutputStream(), true), "UTF-8");
    }
    return new OutputStreamWriter(paramHttpURLConnection.getOutputStream(), "UTF-8");
  }
  
  protected boolean isExpected(int paramInt)
  {
    return (199 < paramInt) && (paramInt <= 203);
  }
  
  protected boolean isRecoverableError(int paramInt)
  {
    return Arrays.asList(new Integer[] { Integer.valueOf(408), Integer.valueOf(429), Integer.valueOf(500), Integer.valueOf(503), Integer.valueOf(511) }).contains(Integer.valueOf(paramInt));
  }
  
  protected String loadData(File paramFile)
  {
    String str2 = null;
    String str1 = str2;
    if (getPersistence() != null)
    {
      str1 = str2;
      if (paramFile != null)
      {
        str2 = getPersistence().load(paramFile);
        str1 = str2;
        if (str2 != null)
        {
          str1 = str2;
          if (str2.isEmpty())
          {
            getPersistence().deleteFile(paramFile);
            str1 = str2;
          }
        }
      }
    }
    return str1;
  }
  
  protected void onResponse(HttpURLConnection paramHttpURLConnection, int paramInt, String paramString, File paramFile)
  {
    this.mRequestCount.getAndDecrement();
    HockeyLog.debug("HockeyApp-Metrics", "response code " + Integer.toString(paramInt));
    if (isRecoverableError(paramInt))
    {
      HockeyLog.debug("HockeyApp-Metrics", "Recoverable error (probably a server error), persisting data:\n" + paramString);
      if (getPersistence() != null) {
        getPersistence().makeAvailable(paramFile);
      }
      return;
    }
    if (getPersistence() != null) {
      getPersistence().deleteFile(paramFile);
    }
    paramString = new StringBuilder();
    if (isExpected(paramInt))
    {
      triggerSending();
      return;
    }
    onUnexpected(paramHttpURLConnection, paramInt, paramString);
  }
  
  protected void onUnexpected(HttpURLConnection paramHttpURLConnection, int paramInt, StringBuilder paramStringBuilder)
  {
    String str = String.format(Locale.ROOT, "Unexpected response code: %d", new Object[] { Integer.valueOf(paramInt) });
    paramStringBuilder.append(str);
    paramStringBuilder.append("\n");
    HockeyLog.debug("HockeyApp-Metrics", str);
    readResponse(paramHttpURLConnection, paramStringBuilder);
  }
  
  /* Error */
  protected void readResponse(HttpURLConnection paramHttpURLConnection, StringBuilder paramStringBuilder)
  {
    // Byte code:
    //   0: new 274	java/lang/StringBuffer
    //   3: dup
    //   4: invokespecial 275	java/lang/StringBuffer:<init>	()V
    //   7: astore 6
    //   9: aconst_null
    //   10: astore_3
    //   11: aconst_null
    //   12: astore_2
    //   13: aload_1
    //   14: invokevirtual 279	java/net/HttpURLConnection:getErrorStream	()Ljava/io/InputStream;
    //   17: astore 5
    //   19: aload 5
    //   21: astore 4
    //   23: aload 5
    //   25: ifnonnull +15 -> 40
    //   28: aload 5
    //   30: astore_2
    //   31: aload 5
    //   33: astore_3
    //   34: aload_1
    //   35: invokevirtual 282	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   38: astore 4
    //   40: aload 4
    //   42: ifnull +140 -> 182
    //   45: aload 4
    //   47: astore_2
    //   48: aload 4
    //   50: astore_3
    //   51: new 284	java/io/BufferedReader
    //   54: dup
    //   55: new 286	java/io/InputStreamReader
    //   58: dup
    //   59: aload 4
    //   61: ldc -72
    //   63: invokespecial 289	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   66: invokespecial 292	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   69: astore_1
    //   70: aload 4
    //   72: astore_2
    //   73: aload 4
    //   75: astore_3
    //   76: aload_1
    //   77: invokevirtual 295	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   80: astore 5
    //   82: aload 5
    //   84: ifnull +41 -> 125
    //   87: aload 4
    //   89: astore_2
    //   90: aload 4
    //   92: astore_3
    //   93: aload 6
    //   95: aload 5
    //   97: invokevirtual 298	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   100: pop
    //   101: goto -31 -> 70
    //   104: astore_1
    //   105: aload_2
    //   106: astore_3
    //   107: ldc 22
    //   109: aload_1
    //   110: invokevirtual 93	java/io/IOException:toString	()Ljava/lang/String;
    //   113: invokestatic 96	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/String;)V
    //   116: aload_2
    //   117: ifnull +7 -> 124
    //   120: aload_2
    //   121: invokevirtual 301	java/io/InputStream:close	()V
    //   124: return
    //   125: aload 4
    //   127: astore_2
    //   128: aload 4
    //   130: astore_3
    //   131: aload 6
    //   133: invokevirtual 302	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   136: astore_1
    //   137: aload 4
    //   139: astore_2
    //   140: aload 4
    //   142: astore_3
    //   143: aload_1
    //   144: invokestatic 307	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   147: ifne +49 -> 196
    //   150: aload 4
    //   152: astore_2
    //   153: aload 4
    //   155: astore_3
    //   156: aload_1
    //   157: invokestatic 310	net/hockeyapp/android/utils/HockeyLog:verbose	(Ljava/lang/String;)V
    //   160: aload 4
    //   162: ifnull -38 -> 124
    //   165: aload 4
    //   167: invokevirtual 301	java/io/InputStream:close	()V
    //   170: return
    //   171: astore_1
    //   172: ldc 22
    //   174: aload_1
    //   175: invokevirtual 93	java/io/IOException:toString	()Ljava/lang/String;
    //   178: invokestatic 96	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/String;)V
    //   181: return
    //   182: aload 4
    //   184: astore_2
    //   185: aload 4
    //   187: astore_3
    //   188: aload_1
    //   189: invokevirtual 313	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
    //   192: astore_1
    //   193: goto -56 -> 137
    //   196: aload 4
    //   198: astore_2
    //   199: aload 4
    //   201: astore_3
    //   202: ldc 22
    //   204: ldc_w 315
    //   207: invokestatic 317	net/hockeyapp/android/utils/HockeyLog:verbose	(Ljava/lang/String;Ljava/lang/String;)V
    //   210: goto -50 -> 160
    //   213: astore_1
    //   214: aload_3
    //   215: ifnull +7 -> 222
    //   218: aload_3
    //   219: invokevirtual 301	java/io/InputStream:close	()V
    //   222: aload_1
    //   223: athrow
    //   224: astore_1
    //   225: ldc 22
    //   227: aload_1
    //   228: invokevirtual 93	java/io/IOException:toString	()Ljava/lang/String;
    //   231: invokestatic 96	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/String;)V
    //   234: return
    //   235: astore_2
    //   236: ldc 22
    //   238: aload_2
    //   239: invokevirtual 93	java/io/IOException:toString	()Ljava/lang/String;
    //   242: invokestatic 96	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/String;)V
    //   245: goto -23 -> 222
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	248	0	this	Sender
    //   0	248	1	paramHttpURLConnection	HttpURLConnection
    //   0	248	2	paramStringBuilder	StringBuilder
    //   10	209	3	localObject1	Object
    //   21	179	4	localObject2	Object
    //   17	79	5	localObject3	Object
    //   7	125	6	localStringBuffer	StringBuffer
    // Exception table:
    //   from	to	target	type
    //   13	19	104	java/io/IOException
    //   34	40	104	java/io/IOException
    //   51	70	104	java/io/IOException
    //   76	82	104	java/io/IOException
    //   93	101	104	java/io/IOException
    //   131	137	104	java/io/IOException
    //   143	150	104	java/io/IOException
    //   156	160	104	java/io/IOException
    //   188	193	104	java/io/IOException
    //   202	210	104	java/io/IOException
    //   165	170	171	java/io/IOException
    //   13	19	213	finally
    //   34	40	213	finally
    //   51	70	213	finally
    //   76	82	213	finally
    //   93	101	213	finally
    //   107	116	213	finally
    //   131	137	213	finally
    //   143	150	213	finally
    //   156	160	213	finally
    //   188	193	213	finally
    //   202	210	213	finally
    //   120	124	224	java/io/IOException
    //   218	222	235	java/io/IOException
  }
  
  protected int requestCount()
  {
    return this.mRequestCount.get();
  }
  
  protected void send()
  {
    if (getPersistence() != null)
    {
      File localFile = getPersistence().nextAvailableFileInDirectory();
      String str = loadData(localFile);
      HttpURLConnection localHttpURLConnection = createConnection();
      if ((str != null) && (localHttpURLConnection != null)) {
        send(localHttpURLConnection, localFile, str);
      }
    }
  }
  
  protected void send(HttpURLConnection paramHttpURLConnection, File paramFile, String paramString)
  {
    logRequest(paramHttpURLConnection, paramString);
    if ((paramHttpURLConnection != null) && (paramFile != null) && (paramString != null)) {}
    try
    {
      paramHttpURLConnection.connect();
      onResponse(paramHttpURLConnection, paramHttpURLConnection.getResponseCode(), paramString, paramFile);
      return;
    }
    catch (IOException paramHttpURLConnection)
    {
      do
      {
        HockeyLog.debug("HockeyApp-Metrics", "Couldn't send data with IOException: " + paramHttpURLConnection.toString());
      } while (getPersistence() == null);
      HockeyLog.debug("HockeyApp-Metrics", "Persisting because of IOException: We're probably offline.");
      getPersistence().makeAvailable(paramFile);
    }
  }
  
  protected void setCustomServerURL(String paramString)
  {
    this.mCustomServerURL = paramString;
  }
  
  protected void setPersistence(Persistence paramPersistence)
  {
    this.mWeakPersistence = new WeakReference(paramPersistence);
  }
  
  protected void triggerSending()
  {
    if (requestCount() < 10)
    {
      this.mRequestCount.getAndIncrement();
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          Sender.this.send();
          return null;
        }
      });
      return;
    }
    HockeyLog.debug("HockeyApp-Metrics", "We have already 10 pending requests, not sending anything.");
  }
  
  protected void triggerSendingForTesting(final HttpURLConnection paramHttpURLConnection, final File paramFile, final String paramString)
  {
    if (requestCount() < 10)
    {
      this.mRequestCount.getAndIncrement();
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected Void doInBackground(Void... paramAnonymousVarArgs)
        {
          Sender.this.send(paramHttpURLConnection, paramFile, paramString);
          return null;
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/Sender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */