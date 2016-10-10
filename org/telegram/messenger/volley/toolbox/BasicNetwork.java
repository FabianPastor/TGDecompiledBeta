package org.telegram.messenger.volley.toolbox;

import android.os.SystemClock;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.impl.cookie.DateUtils;
import org.telegram.messenger.volley.Cache.Entry;
import org.telegram.messenger.volley.Network;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.RetryPolicy;
import org.telegram.messenger.volley.ServerError;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.VolleyLog;

public class BasicNetwork
  implements Network
{
  protected static final boolean DEBUG = VolleyLog.DEBUG;
  private static int DEFAULT_POOL_SIZE = 4096;
  private static int SLOW_REQUEST_THRESHOLD_MS = 3000;
  protected final HttpStack mHttpStack;
  protected final ByteArrayPool mPool;
  
  public BasicNetwork(HttpStack paramHttpStack)
  {
    this(paramHttpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
  }
  
  public BasicNetwork(HttpStack paramHttpStack, ByteArrayPool paramByteArrayPool)
  {
    this.mHttpStack = paramHttpStack;
    this.mPool = paramByteArrayPool;
  }
  
  private void addCacheHeaders(Map<String, String> paramMap, Cache.Entry paramEntry)
  {
    if (paramEntry == null) {}
    do
    {
      return;
      if (paramEntry.etag != null) {
        paramMap.put("If-None-Match", paramEntry.etag);
      }
    } while (paramEntry.lastModified <= 0L);
    paramMap.put("If-Modified-Since", DateUtils.formatDate(new Date(paramEntry.lastModified)));
  }
  
  private static void attemptRetryOnException(String paramString, Request<?> paramRequest, VolleyError paramVolleyError)
    throws VolleyError
  {
    RetryPolicy localRetryPolicy = paramRequest.getRetryPolicy();
    int i = paramRequest.getTimeoutMs();
    try
    {
      localRetryPolicy.retry(paramVolleyError);
      paramRequest.addMarker(String.format("%s-retry [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      return;
    }
    catch (VolleyError paramVolleyError)
    {
      paramRequest.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      throw paramVolleyError;
    }
  }
  
  protected static Map<String, String> convertHeaders(Header[] paramArrayOfHeader)
  {
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int i = 0;
    while (i < paramArrayOfHeader.length)
    {
      localTreeMap.put(paramArrayOfHeader[i].getName(), paramArrayOfHeader[i].getValue());
      i += 1;
    }
    return localTreeMap;
  }
  
  private byte[] entityToBytes(HttpEntity paramHttpEntity)
    throws IOException, ServerError
  {
    PoolingByteArrayOutputStream localPoolingByteArrayOutputStream = new PoolingByteArrayOutputStream(this.mPool, (int)paramHttpEntity.getContentLength());
    Object localObject2 = null;
    Object localObject1 = localObject2;
    Object localObject4;
    try
    {
      localObject4 = paramHttpEntity.getContent();
      if (localObject4 == null)
      {
        localObject1 = localObject2;
        throw new ServerError();
      }
    }
    finally {}
    try
    {
      paramHttpEntity.consumeContent();
      this.mPool.returnBuf((byte[])localObject1);
      localPoolingByteArrayOutputStream.close();
      throw ((Throwable)localObject3);
      localObject1 = localObject3;
      byte[] arrayOfByte = this.mPool.getBuf(1024);
      for (;;)
      {
        localObject1 = arrayOfByte;
        int i = ((InputStream)localObject4).read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localObject1 = arrayOfByte;
        localPoolingByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localObject1 = arrayOfByte;
      localObject4 = localPoolingByteArrayOutputStream.toByteArray();
      try
      {
        paramHttpEntity.consumeContent();
        this.mPool.returnBuf(arrayOfByte);
        localPoolingByteArrayOutputStream.close();
        return (byte[])localObject4;
      }
      catch (IOException paramHttpEntity)
      {
        for (;;)
        {
          VolleyLog.v("Error occured when calling consumingContent", new Object[0]);
        }
      }
    }
    catch (IOException paramHttpEntity)
    {
      for (;;)
      {
        VolleyLog.v("Error occured when calling consumingContent", new Object[0]);
      }
    }
  }
  
  private void logSlowRequests(long paramLong, Request<?> paramRequest, byte[] paramArrayOfByte, StatusLine paramStatusLine)
  {
    if ((DEBUG) || (paramLong > SLOW_REQUEST_THRESHOLD_MS)) {
      if (paramArrayOfByte == null) {
        break label82;
      }
    }
    label82:
    for (paramArrayOfByte = Integer.valueOf(paramArrayOfByte.length);; paramArrayOfByte = "null")
    {
      VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[] { paramRequest, Long.valueOf(paramLong), paramArrayOfByte, Integer.valueOf(paramStatusLine.getStatusCode()), Integer.valueOf(paramRequest.getRetryPolicy().getCurrentRetryCount()) });
      return;
    }
  }
  
  protected void logError(String paramString1, String paramString2, long paramLong)
  {
    VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", new Object[] { paramString1, Long.valueOf(SystemClock.elapsedRealtime() - paramLong), paramString2 });
  }
  
  /* Error */
  public org.telegram.messenger.volley.NetworkResponse performRequest(Request<?> paramRequest)
    throws VolleyError
  {
    // Byte code:
    //   0: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore_3
    //   4: aconst_null
    //   5: astore 7
    //   7: invokestatic 243	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   10: astore 8
    //   12: aload 8
    //   14: astore 5
    //   16: aload 7
    //   18: astore 6
    //   20: new 245	java/util/HashMap
    //   23: dup
    //   24: invokespecial 246	java/util/HashMap:<init>	()V
    //   27: astore 9
    //   29: aload 8
    //   31: astore 5
    //   33: aload 7
    //   35: astore 6
    //   37: aload_0
    //   38: aload 9
    //   40: aload_1
    //   41: invokevirtual 250	org/telegram/messenger/volley/Request:getCacheEntry	()Lorg/telegram/messenger/volley/Cache$Entry;
    //   44: invokespecial 252	org/telegram/messenger/volley/toolbox/BasicNetwork:addCacheHeaders	(Ljava/util/Map;Lorg/telegram/messenger/volley/Cache$Entry;)V
    //   47: aload 8
    //   49: astore 5
    //   51: aload 7
    //   53: astore 6
    //   55: aload_0
    //   56: getfield 41	org/telegram/messenger/volley/toolbox/BasicNetwork:mHttpStack	Lorg/telegram/messenger/volley/toolbox/HttpStack;
    //   59: aload_1
    //   60: aload 9
    //   62: invokeinterface 257 3 0
    //   67: astore 7
    //   69: aload 8
    //   71: astore 5
    //   73: aload 7
    //   75: astore 6
    //   77: aload 7
    //   79: invokeinterface 263 1 0
    //   84: astore 10
    //   86: aload 8
    //   88: astore 5
    //   90: aload 7
    //   92: astore 6
    //   94: aload 10
    //   96: invokeinterface 211 1 0
    //   101: istore_2
    //   102: aload 8
    //   104: astore 5
    //   106: aload 7
    //   108: astore 6
    //   110: aload 7
    //   112: invokeinterface 267 1 0
    //   117: invokestatic 269	org/telegram/messenger/volley/toolbox/BasicNetwork:convertHeaders	([Lorg/apache/http/Header;)Ljava/util/Map;
    //   120: astore 8
    //   122: iload_2
    //   123: sipush 304
    //   126: if_icmpne +105 -> 231
    //   129: aload 8
    //   131: astore 5
    //   133: aload 7
    //   135: astore 6
    //   137: aload_1
    //   138: invokevirtual 250	org/telegram/messenger/volley/Request:getCacheEntry	()Lorg/telegram/messenger/volley/Cache$Entry;
    //   141: astore 9
    //   143: aload 9
    //   145: ifnonnull +31 -> 176
    //   148: aload 8
    //   150: astore 5
    //   152: aload 7
    //   154: astore 6
    //   156: new 271	org/telegram/messenger/volley/NetworkResponse
    //   159: dup
    //   160: sipush 304
    //   163: aconst_null
    //   164: aload 8
    //   166: iconst_1
    //   167: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   170: lload_3
    //   171: lsub
    //   172: invokespecial 274	org/telegram/messenger/volley/NetworkResponse:<init>	(I[BLjava/util/Map;ZJ)V
    //   175: areturn
    //   176: aload 8
    //   178: astore 5
    //   180: aload 7
    //   182: astore 6
    //   184: aload 9
    //   186: getfield 278	org/telegram/messenger/volley/Cache$Entry:responseHeaders	Ljava/util/Map;
    //   189: aload 8
    //   191: invokeinterface 282 2 0
    //   196: aload 8
    //   198: astore 5
    //   200: aload 7
    //   202: astore 6
    //   204: new 271	org/telegram/messenger/volley/NetworkResponse
    //   207: dup
    //   208: sipush 304
    //   211: aload 9
    //   213: getfield 286	org/telegram/messenger/volley/Cache$Entry:data	[B
    //   216: aload 9
    //   218: getfield 278	org/telegram/messenger/volley/Cache$Entry:responseHeaders	Ljava/util/Map;
    //   221: iconst_1
    //   222: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   225: lload_3
    //   226: lsub
    //   227: invokespecial 274	org/telegram/messenger/volley/NetworkResponse:<init>	(I[BLjava/util/Map;ZJ)V
    //   230: areturn
    //   231: aload 8
    //   233: astore 5
    //   235: aload 7
    //   237: astore 6
    //   239: aload 7
    //   241: invokeinterface 290 1 0
    //   246: ifnull +83 -> 329
    //   249: aload 8
    //   251: astore 5
    //   253: aload 7
    //   255: astore 6
    //   257: aload_0
    //   258: aload 7
    //   260: invokeinterface 290 1 0
    //   265: invokespecial 292	org/telegram/messenger/volley/toolbox/BasicNetwork:entityToBytes	(Lorg/apache/http/HttpEntity;)[B
    //   268: astore 9
    //   270: aload 9
    //   272: astore 5
    //   274: aload_0
    //   275: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   278: lload_3
    //   279: lsub
    //   280: aload_1
    //   281: aload 5
    //   283: aload 10
    //   285: invokespecial 294	org/telegram/messenger/volley/toolbox/BasicNetwork:logSlowRequests	(JLorg/telegram/messenger/volley/Request;[BLorg/apache/http/StatusLine;)V
    //   288: iload_2
    //   289: sipush 200
    //   292: if_icmplt +10 -> 302
    //   295: iload_2
    //   296: sipush 299
    //   299: if_icmple +50 -> 349
    //   302: new 145	java/io/IOException
    //   305: dup
    //   306: invokespecial 295	java/io/IOException:<init>	()V
    //   309: athrow
    //   310: astore 5
    //   312: ldc_w 297
    //   315: aload_1
    //   316: new 299	org/telegram/messenger/volley/TimeoutError
    //   319: dup
    //   320: invokespecial 300	org/telegram/messenger/volley/TimeoutError:<init>	()V
    //   323: invokestatic 302	org/telegram/messenger/volley/toolbox/BasicNetwork:attemptRetryOnException	(Ljava/lang/String;Lorg/telegram/messenger/volley/Request;Lorg/telegram/messenger/volley/VolleyError;)V
    //   326: goto -322 -> 4
    //   329: aload 8
    //   331: astore 5
    //   333: aload 7
    //   335: astore 6
    //   337: iconst_0
    //   338: newarray <illegal type>
    //   340: astore 9
    //   342: aload 9
    //   344: astore 5
    //   346: goto -72 -> 274
    //   349: new 271	org/telegram/messenger/volley/NetworkResponse
    //   352: dup
    //   353: iload_2
    //   354: aload 5
    //   356: aload 8
    //   358: iconst_0
    //   359: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   362: lload_3
    //   363: lsub
    //   364: invokespecial 274	org/telegram/messenger/volley/NetworkResponse:<init>	(I[BLjava/util/Map;ZJ)V
    //   367: astore 6
    //   369: aload 6
    //   371: areturn
    //   372: astore 5
    //   374: ldc_w 304
    //   377: aload_1
    //   378: new 299	org/telegram/messenger/volley/TimeoutError
    //   381: dup
    //   382: invokespecial 300	org/telegram/messenger/volley/TimeoutError:<init>	()V
    //   385: invokestatic 302	org/telegram/messenger/volley/toolbox/BasicNetwork:attemptRetryOnException	(Ljava/lang/String;Lorg/telegram/messenger/volley/Request;Lorg/telegram/messenger/volley/VolleyError;)V
    //   388: goto -384 -> 4
    //   391: astore 5
    //   393: new 306	java/lang/RuntimeException
    //   396: dup
    //   397: new 308	java/lang/StringBuilder
    //   400: dup
    //   401: invokespecial 309	java/lang/StringBuilder:<init>	()V
    //   404: ldc_w 311
    //   407: invokevirtual 315	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   410: aload_1
    //   411: invokevirtual 318	org/telegram/messenger/volley/Request:getUrl	()Ljava/lang/String;
    //   414: invokevirtual 315	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   417: invokevirtual 321	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   420: aload 5
    //   422: invokespecial 324	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   425: athrow
    //   426: astore 10
    //   428: aconst_null
    //   429: astore 9
    //   431: aload 6
    //   433: astore 7
    //   435: aload 5
    //   437: astore 8
    //   439: aload 10
    //   441: astore 6
    //   443: aload 7
    //   445: ifnull +98 -> 543
    //   448: aload 7
    //   450: invokeinterface 263 1 0
    //   455: invokeinterface 211 1 0
    //   460: istore_2
    //   461: ldc_w 326
    //   464: iconst_2
    //   465: anewarray 4	java/lang/Object
    //   468: dup
    //   469: iconst_0
    //   470: iload_2
    //   471: invokestatic 106	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   474: aastore
    //   475: dup
    //   476: iconst_1
    //   477: aload_1
    //   478: invokevirtual 318	org/telegram/messenger/volley/Request:getUrl	()Ljava/lang/String;
    //   481: aastore
    //   482: invokestatic 329	org/telegram/messenger/volley/VolleyLog:e	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   485: aload 9
    //   487: ifnull +76 -> 563
    //   490: new 271	org/telegram/messenger/volley/NetworkResponse
    //   493: dup
    //   494: iload_2
    //   495: aload 9
    //   497: aload 8
    //   499: iconst_0
    //   500: invokestatic 229	android/os/SystemClock:elapsedRealtime	()J
    //   503: lload_3
    //   504: lsub
    //   505: invokespecial 274	org/telegram/messenger/volley/NetworkResponse:<init>	(I[BLjava/util/Map;ZJ)V
    //   508: astore 5
    //   510: iload_2
    //   511: sipush 401
    //   514: if_icmpeq +10 -> 524
    //   517: iload_2
    //   518: sipush 403
    //   521: if_icmpne +32 -> 553
    //   524: ldc_w 331
    //   527: aload_1
    //   528: new 333	org/telegram/messenger/volley/AuthFailureError
    //   531: dup
    //   532: aload 5
    //   534: invokespecial 336	org/telegram/messenger/volley/AuthFailureError:<init>	(Lorg/telegram/messenger/volley/NetworkResponse;)V
    //   537: invokestatic 302	org/telegram/messenger/volley/toolbox/BasicNetwork:attemptRetryOnException	(Ljava/lang/String;Lorg/telegram/messenger/volley/Request;Lorg/telegram/messenger/volley/VolleyError;)V
    //   540: goto -536 -> 4
    //   543: new 338	org/telegram/messenger/volley/NoConnectionError
    //   546: dup
    //   547: aload 6
    //   549: invokespecial 341	org/telegram/messenger/volley/NoConnectionError:<init>	(Ljava/lang/Throwable;)V
    //   552: athrow
    //   553: new 147	org/telegram/messenger/volley/ServerError
    //   556: dup
    //   557: aload 5
    //   559: invokespecial 342	org/telegram/messenger/volley/ServerError:<init>	(Lorg/telegram/messenger/volley/NetworkResponse;)V
    //   562: athrow
    //   563: new 344	org/telegram/messenger/volley/NetworkError
    //   566: dup
    //   567: aconst_null
    //   568: invokespecial 345	org/telegram/messenger/volley/NetworkError:<init>	(Lorg/telegram/messenger/volley/NetworkResponse;)V
    //   571: athrow
    //   572: astore 6
    //   574: aload 5
    //   576: astore 9
    //   578: goto -135 -> 443
    //   581: astore 5
    //   583: goto -190 -> 393
    //   586: astore 5
    //   588: goto -214 -> 374
    //   591: astore 5
    //   593: goto -281 -> 312
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	596	0	this	BasicNetwork
    //   0	596	1	paramRequest	Request<?>
    //   101	421	2	i	int
    //   3	501	3	l	long
    //   14	268	5	localObject1	Object
    //   310	1	5	localSocketTimeoutException1	java.net.SocketTimeoutException
    //   331	24	5	localObject2	Object
    //   372	1	5	localConnectTimeoutException1	org.apache.http.conn.ConnectTimeoutException
    //   391	45	5	localMalformedURLException1	java.net.MalformedURLException
    //   508	67	5	localNetworkResponse	org.telegram.messenger.volley.NetworkResponse
    //   581	1	5	localMalformedURLException2	java.net.MalformedURLException
    //   586	1	5	localConnectTimeoutException2	org.apache.http.conn.ConnectTimeoutException
    //   591	1	5	localSocketTimeoutException2	java.net.SocketTimeoutException
    //   18	530	6	localObject3	Object
    //   572	1	6	localIOException1	IOException
    //   5	444	7	localObject4	Object
    //   10	488	8	localObject5	Object
    //   27	550	9	localObject6	Object
    //   84	200	10	localStatusLine	StatusLine
    //   426	14	10	localIOException2	IOException
    // Exception table:
    //   from	to	target	type
    //   274	288	310	java/net/SocketTimeoutException
    //   302	310	310	java/net/SocketTimeoutException
    //   349	369	310	java/net/SocketTimeoutException
    //   20	29	372	org/apache/http/conn/ConnectTimeoutException
    //   37	47	372	org/apache/http/conn/ConnectTimeoutException
    //   55	69	372	org/apache/http/conn/ConnectTimeoutException
    //   77	86	372	org/apache/http/conn/ConnectTimeoutException
    //   94	102	372	org/apache/http/conn/ConnectTimeoutException
    //   110	122	372	org/apache/http/conn/ConnectTimeoutException
    //   137	143	372	org/apache/http/conn/ConnectTimeoutException
    //   156	176	372	org/apache/http/conn/ConnectTimeoutException
    //   184	196	372	org/apache/http/conn/ConnectTimeoutException
    //   204	231	372	org/apache/http/conn/ConnectTimeoutException
    //   239	249	372	org/apache/http/conn/ConnectTimeoutException
    //   257	270	372	org/apache/http/conn/ConnectTimeoutException
    //   337	342	372	org/apache/http/conn/ConnectTimeoutException
    //   20	29	391	java/net/MalformedURLException
    //   37	47	391	java/net/MalformedURLException
    //   55	69	391	java/net/MalformedURLException
    //   77	86	391	java/net/MalformedURLException
    //   94	102	391	java/net/MalformedURLException
    //   110	122	391	java/net/MalformedURLException
    //   137	143	391	java/net/MalformedURLException
    //   156	176	391	java/net/MalformedURLException
    //   184	196	391	java/net/MalformedURLException
    //   204	231	391	java/net/MalformedURLException
    //   239	249	391	java/net/MalformedURLException
    //   257	270	391	java/net/MalformedURLException
    //   337	342	391	java/net/MalformedURLException
    //   20	29	426	java/io/IOException
    //   37	47	426	java/io/IOException
    //   55	69	426	java/io/IOException
    //   77	86	426	java/io/IOException
    //   94	102	426	java/io/IOException
    //   110	122	426	java/io/IOException
    //   137	143	426	java/io/IOException
    //   156	176	426	java/io/IOException
    //   184	196	426	java/io/IOException
    //   204	231	426	java/io/IOException
    //   239	249	426	java/io/IOException
    //   257	270	426	java/io/IOException
    //   337	342	426	java/io/IOException
    //   274	288	572	java/io/IOException
    //   302	310	572	java/io/IOException
    //   349	369	572	java/io/IOException
    //   274	288	581	java/net/MalformedURLException
    //   302	310	581	java/net/MalformedURLException
    //   349	369	581	java/net/MalformedURLException
    //   274	288	586	org/apache/http/conn/ConnectTimeoutException
    //   302	310	586	org/apache/http/conn/ConnectTimeoutException
    //   349	369	586	org/apache/http/conn/ConnectTimeoutException
    //   20	29	591	java/net/SocketTimeoutException
    //   37	47	591	java/net/SocketTimeoutException
    //   55	69	591	java/net/SocketTimeoutException
    //   77	86	591	java/net/SocketTimeoutException
    //   94	102	591	java/net/SocketTimeoutException
    //   110	122	591	java/net/SocketTimeoutException
    //   137	143	591	java/net/SocketTimeoutException
    //   156	176	591	java/net/SocketTimeoutException
    //   184	196	591	java/net/SocketTimeoutException
    //   204	231	591	java/net/SocketTimeoutException
    //   239	249	591	java/net/SocketTimeoutException
    //   257	270	591	java/net/SocketTimeoutException
    //   337	342	591	java/net/SocketTimeoutException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/BasicNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */