package org.telegram.messenger.exoplayer.upstream;

import android.net.Uri;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Predicate;

public class DefaultHttpDataSource
  implements HttpDataSource
{
  private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
  public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
  private static final int MAX_REDIRECTS = 20;
  private static final String TAG = "DefaultHttpDataSource";
  private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference();
  private final boolean allowCrossProtocolRedirects;
  private long bytesRead;
  private long bytesSkipped;
  private long bytesToRead;
  private long bytesToSkip;
  private final int connectTimeoutMillis;
  private HttpURLConnection connection;
  private final Predicate<String> contentTypePredicate;
  private DataSpec dataSpec;
  private InputStream inputStream;
  private final TransferListener listener;
  private boolean opened;
  private final int readTimeoutMillis;
  private final HashMap<String, String> requestProperties;
  private final String userAgent;
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate)
  {
    this(paramString, paramPredicate, null);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener paramTransferListener)
  {
    this(paramString, paramPredicate, paramTransferListener, 8000, 8000);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener paramTransferListener, int paramInt1, int paramInt2)
  {
    this(paramString, paramPredicate, paramTransferListener, paramInt1, paramInt2, false);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener paramTransferListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.userAgent = Assertions.checkNotEmpty(paramString);
    this.contentTypePredicate = paramPredicate;
    this.listener = paramTransferListener;
    this.requestProperties = new HashMap();
    this.connectTimeoutMillis = paramInt1;
    this.readTimeoutMillis = paramInt2;
    this.allowCrossProtocolRedirects = paramBoolean;
  }
  
  private void closeConnectionQuietly()
  {
    if (this.connection != null) {}
    try
    {
      this.connection.disconnect();
      this.connection = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("DefaultHttpDataSource", "Unexpected error while disconnecting", localException);
      }
    }
  }
  
  /* Error */
  private static long getContentLength(HttpURLConnection paramHttpURLConnection)
  {
    // Byte code:
    //   0: ldc2_w 128
    //   3: lstore_3
    //   4: aload_0
    //   5: ldc -125
    //   7: invokevirtual 134	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   10: astore 7
    //   12: lload_3
    //   13: lstore_1
    //   14: aload 7
    //   16: invokestatic 140	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   19: ifne +9 -> 28
    //   22: aload 7
    //   24: invokestatic 146	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   27: lstore_1
    //   28: aload_0
    //   29: ldc -108
    //   31: invokevirtual 134	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   34: astore_0
    //   35: lload_1
    //   36: lstore_3
    //   37: aload_0
    //   38: invokestatic 140	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   41: ifne +60 -> 101
    //   44: getstatic 58	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:CONTENT_RANGE_HEADER	Ljava/util/regex/Pattern;
    //   47: aload_0
    //   48: invokevirtual 152	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   51: astore 8
    //   53: lload_1
    //   54: lstore_3
    //   55: aload 8
    //   57: invokevirtual 158	java/util/regex/Matcher:find	()Z
    //   60: ifeq +41 -> 101
    //   63: aload 8
    //   65: iconst_2
    //   66: invokevirtual 162	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   69: invokestatic 146	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   72: lstore_3
    //   73: aload 8
    //   75: iconst_1
    //   76: invokevirtual 162	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   79: invokestatic 146	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   82: lstore 5
    //   84: lload_3
    //   85: lload 5
    //   87: lsub
    //   88: lconst_1
    //   89: ladd
    //   90: lstore 5
    //   92: lload_1
    //   93: lconst_0
    //   94: lcmp
    //   95: ifge +46 -> 141
    //   98: lload 5
    //   100: lstore_3
    //   101: lload_3
    //   102: lreturn
    //   103: astore 8
    //   105: ldc 18
    //   107: new 164	java/lang/StringBuilder
    //   110: dup
    //   111: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   114: ldc -89
    //   116: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: aload 7
    //   121: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: ldc -83
    //   126: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: invokestatic 180	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   135: pop
    //   136: lload_3
    //   137: lstore_1
    //   138: goto -110 -> 28
    //   141: lload_1
    //   142: lstore_3
    //   143: lload_1
    //   144: lload 5
    //   146: lcmp
    //   147: ifeq -46 -> 101
    //   150: ldc 18
    //   152: new 164	java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   159: ldc -74
    //   161: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: aload 7
    //   166: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: ldc -72
    //   171: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: aload_0
    //   175: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: ldc -83
    //   180: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   186: invokestatic 187	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   189: pop
    //   190: lload_1
    //   191: lload 5
    //   193: invokestatic 193	java/lang/Math:max	(JJ)J
    //   196: lstore_3
    //   197: lload_3
    //   198: lreturn
    //   199: astore 7
    //   201: ldc 18
    //   203: new 164	java/lang/StringBuilder
    //   206: dup
    //   207: invokespecial 165	java/lang/StringBuilder:<init>	()V
    //   210: ldc -61
    //   212: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: aload_0
    //   216: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   219: ldc -83
    //   221: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   227: invokestatic 180	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   230: pop
    //   231: lload_1
    //   232: lreturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	paramHttpURLConnection	HttpURLConnection
    //   13	219	1	l1	long
    //   3	195	3	l2	long
    //   82	110	5	l3	long
    //   10	155	7	str	String
    //   199	1	7	localNumberFormatException1	NumberFormatException
    //   51	23	8	localMatcher	java.util.regex.Matcher
    //   103	1	8	localNumberFormatException2	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   22	28	103	java/lang/NumberFormatException
    //   63	84	199	java/lang/NumberFormatException
    //   150	197	199	java/lang/NumberFormatException
  }
  
  private static URL handleRedirect(URL paramURL, String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new ProtocolException("Null location redirect");
    }
    paramURL = new URL(paramURL, paramString);
    paramString = paramURL.getProtocol();
    if ((!"https".equals(paramString)) && (!"http".equals(paramString))) {
      throw new ProtocolException("Unsupported protocol redirect: " + paramString);
    }
    return paramURL;
  }
  
  private HttpURLConnection makeConnection(URL arg1, byte[] paramArrayOfByte, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)???.openConnection();
    localHttpURLConnection.setConnectTimeout(this.connectTimeoutMillis);
    localHttpURLConnection.setReadTimeout(this.readTimeoutMillis);
    Object localObject;
    synchronized (this.requestProperties)
    {
      localObject = this.requestProperties.entrySet().iterator();
      if (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        localHttpURLConnection.setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
      }
    }
    if ((paramLong1 != 0L) || (paramLong2 != -1L))
    {
      localObject = "bytes=" + paramLong1 + "-";
      ??? = (URL)localObject;
      if (paramLong2 != -1L) {
        ??? = (String)localObject + (paramLong1 + paramLong2 - 1L);
      }
      localHttpURLConnection.setRequestProperty("Range", ???);
    }
    localHttpURLConnection.setRequestProperty("User-Agent", this.userAgent);
    if (!paramBoolean1) {
      localHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
    }
    localHttpURLConnection.setInstanceFollowRedirects(paramBoolean2);
    if (paramArrayOfByte != null) {}
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      localHttpURLConnection.setDoOutput(paramBoolean1);
      if (paramArrayOfByte == null) {
        break;
      }
      localHttpURLConnection.setFixedLengthStreamingMode(paramArrayOfByte.length);
      localHttpURLConnection.connect();
      ??? = localHttpURLConnection.getOutputStream();
      ???.write(paramArrayOfByte);
      ???.close();
      return localHttpURLConnection;
    }
    localHttpURLConnection.connect();
    return localHttpURLConnection;
  }
  
  private HttpURLConnection makeConnection(DataSpec paramDataSpec)
    throws IOException
  {
    Object localObject = new URL(paramDataSpec.uri.toString());
    byte[] arrayOfByte = paramDataSpec.postBody;
    long l1 = paramDataSpec.position;
    long l2 = paramDataSpec.length;
    if ((paramDataSpec.flags & 0x1) != 0) {}
    for (boolean bool = true; !this.allowCrossProtocolRedirects; bool = false)
    {
      localObject = makeConnection((URL)localObject, arrayOfByte, l1, l2, bool, true);
      return (HttpURLConnection)localObject;
    }
    int i = 0;
    paramDataSpec = (DataSpec)localObject;
    int j;
    for (;;)
    {
      j = i + 1;
      if (i > 20) {
        break label201;
      }
      HttpURLConnection localHttpURLConnection = makeConnection(paramDataSpec, arrayOfByte, l1, l2, bool, false);
      i = localHttpURLConnection.getResponseCode();
      if ((i != 300) && (i != 301) && (i != 302) && (i != 303))
      {
        localObject = localHttpURLConnection;
        if (arrayOfByte != null) {
          break;
        }
        if (i != 307)
        {
          localObject = localHttpURLConnection;
          if (i != 308) {
            break;
          }
        }
      }
      arrayOfByte = null;
      localObject = localHttpURLConnection.getHeaderField("Location");
      localHttpURLConnection.disconnect();
      paramDataSpec = handleRedirect(paramDataSpec, (String)localObject);
      i = j;
    }
    label201:
    throw new NoRouteToHostException("Too many redirects: " + j);
  }
  
  private int readInternal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.bytesToRead == -1L)
    {
      if (paramInt2 != 0) {
        break label38;
      }
      paramInt1 = -1;
    }
    label38:
    do
    {
      return paramInt1;
      paramInt2 = (int)Math.min(paramInt2, this.bytesToRead - this.bytesRead);
      break;
      paramInt2 = this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1)
      {
        if ((this.bytesToRead != -1L) && (this.bytesToRead != this.bytesRead)) {
          throw new EOFException();
        }
        return -1;
      }
      this.bytesRead += paramInt2;
      paramInt1 = paramInt2;
    } while (this.listener == null);
    this.listener.onBytesTransferred(paramInt2);
    return paramInt2;
  }
  
  private void skipInternal()
    throws IOException
  {
    if (this.bytesSkipped == this.bytesToSkip) {
      return;
    }
    byte[] arrayOfByte2 = (byte[])skipBufferReference.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null) {
      arrayOfByte1 = new byte['က'];
    }
    while (this.bytesSkipped != this.bytesToSkip)
    {
      int i = (int)Math.min(this.bytesToSkip - this.bytesSkipped, arrayOfByte1.length);
      i = this.inputStream.read(arrayOfByte1, 0, i);
      if (Thread.interrupted()) {
        throw new InterruptedIOException();
      }
      if (i == -1) {
        throw new EOFException();
      }
      this.bytesSkipped += i;
      if (this.listener != null) {
        this.listener.onBytesTransferred(i);
      }
    }
    skipBufferReference.set(arrayOfByte1);
  }
  
  protected final long bytesRead()
  {
    return this.bytesRead;
  }
  
  protected final long bytesRemaining()
  {
    if (this.bytesToRead == -1L) {
      return this.bytesToRead;
    }
    return this.bytesToRead - this.bytesRead;
  }
  
  protected final long bytesSkipped()
  {
    return this.bytesSkipped;
  }
  
  public void clearAllRequestProperties()
  {
    synchronized (this.requestProperties)
    {
      this.requestProperties.clear();
      return;
    }
  }
  
  public void clearRequestProperty(String paramString)
  {
    Assertions.checkNotNull(paramString);
    synchronized (this.requestProperties)
    {
      this.requestProperties.remove(paramString);
      return;
    }
  }
  
  /* Error */
  public void close()
    throws HttpDataSource.HttpDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 365	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   4: ifnull +21 -> 25
    //   7: aload_0
    //   8: getfield 110	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   11: aload_0
    //   12: invokevirtual 417	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:bytesRemaining	()J
    //   15: invokestatic 423	org/telegram/messenger/exoplayer/util/Util:maybeTerminateInputStream	(Ljava/net/HttpURLConnection;J)V
    //   18: aload_0
    //   19: getfield 365	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   22: invokevirtual 424	java/io/InputStream:close	()V
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 365	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   30: aload_0
    //   31: invokespecial 426	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   34: aload_0
    //   35: getfield 428	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:opened	Z
    //   38: ifeq +24 -> 62
    //   41: aload_0
    //   42: iconst_0
    //   43: putfield 428	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:opened	Z
    //   46: aload_0
    //   47: getfield 93	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   50: ifnull +12 -> 62
    //   53: aload_0
    //   54: getfield 93	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   57: invokeinterface 431 1 0
    //   62: return
    //   63: astore_1
    //   64: new 415	org/telegram/messenger/exoplayer/upstream/HttpDataSource$HttpDataSourceException
    //   67: dup
    //   68: aload_1
    //   69: aload_0
    //   70: getfield 433	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   73: iconst_3
    //   74: invokespecial 436	org/telegram/messenger/exoplayer/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/io/IOException;Lorg/telegram/messenger/exoplayer/upstream/DataSpec;I)V
    //   77: athrow
    //   78: astore_1
    //   79: aload_0
    //   80: aconst_null
    //   81: putfield 365	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   84: aload_0
    //   85: invokespecial 426	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   88: aload_0
    //   89: getfield 428	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:opened	Z
    //   92: ifeq +24 -> 116
    //   95: aload_0
    //   96: iconst_0
    //   97: putfield 428	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:opened	Z
    //   100: aload_0
    //   101: getfield 93	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   104: ifnull +12 -> 116
    //   107: aload_0
    //   108: getfield 93	org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer/upstream/TransferListener;
    //   111: invokeinterface 431 1 0
    //   116: aload_1
    //   117: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	this	DefaultHttpDataSource
    //   63	6	1	localIOException	IOException
    //   78	39	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	25	63	java/io/IOException
    //   0	18	78	finally
    //   18	25	78	finally
    //   64	78	78	finally
  }
  
  protected final HttpURLConnection getConnection()
  {
    return this.connection;
  }
  
  public Map<String, List<String>> getResponseHeaders()
  {
    if (this.connection == null) {
      return null;
    }
    return this.connection.getHeaderFields();
  }
  
  public String getUri()
  {
    if (this.connection == null) {
      return null;
    }
    return this.connection.getURL().toString();
  }
  
  public long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException
  {
    long l2 = 0L;
    this.dataSpec = paramDataSpec;
    this.bytesRead = 0L;
    this.bytesSkipped = 0L;
    int i;
    try
    {
      this.connection = makeConnection(paramDataSpec);
      Map localMap;
      str = this.connection.getContentType();
    }
    catch (IOException localIOException1)
    {
      try
      {
        i = this.connection.getResponseCode();
        if ((i >= 200) && (i <= 299)) {
          break label158;
        }
        localMap = this.connection.getHeaderFields();
        closeConnectionQuietly();
        throw new HttpDataSource.InvalidResponseCodeException(i, localMap, paramDataSpec);
      }
      catch (IOException localIOException2)
      {
        closeConnectionQuietly();
        throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + paramDataSpec.uri.toString(), localIOException2, paramDataSpec, 1);
      }
      localIOException1 = localIOException1;
      throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + paramDataSpec.uri.toString(), localIOException1, paramDataSpec, 1);
    }
    label158:
    String str;
    if ((this.contentTypePredicate != null) && (!this.contentTypePredicate.evaluate(str)))
    {
      closeConnectionQuietly();
      throw new HttpDataSource.InvalidContentTypeException(str, paramDataSpec);
    }
    long l1 = l2;
    if (i == 200)
    {
      l1 = l2;
      if (paramDataSpec.position != 0L) {
        l1 = paramDataSpec.position;
      }
    }
    this.bytesToSkip = l1;
    if ((paramDataSpec.flags & 0x1) == 0)
    {
      l1 = getContentLength(this.connection);
      if (paramDataSpec.length != -1L) {
        l1 = paramDataSpec.length;
      }
    }
    for (this.bytesToRead = l1;; this.bytesToRead = paramDataSpec.length)
    {
      try
      {
        this.inputStream = this.connection.getInputStream();
        this.opened = true;
        if (this.listener != null) {
          this.listener.onTransferStart();
        }
        return this.bytesToRead;
      }
      catch (IOException localIOException3)
      {
        closeConnectionQuietly();
        throw new HttpDataSource.HttpDataSourceException(localIOException3, paramDataSpec, 1);
      }
      if (l1 != -1L)
      {
        l1 -= this.bytesToSkip;
        break;
      }
      l1 = -1L;
      break;
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws HttpDataSource.HttpDataSourceException
  {
    try
    {
      skipInternal();
      paramInt1 = readInternal(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new HttpDataSource.HttpDataSourceException(paramArrayOfByte, this.dataSpec, 2);
    }
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    Assertions.checkNotNull(paramString1);
    Assertions.checkNotNull(paramString2);
    synchronized (this.requestProperties)
    {
      this.requestProperties.put(paramString1, paramString2);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/DefaultHttpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */