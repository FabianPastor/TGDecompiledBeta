package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Predicate;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultHttpDataSource
  implements HttpDataSource
{
  private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
  public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
  private static final long MAX_BYTES_TO_DRAIN = 2048L;
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
  private final HttpDataSource.RequestProperties defaultRequestProperties;
  private InputStream inputStream;
  private final TransferListener<? super DefaultHttpDataSource> listener;
  private boolean opened;
  private final int readTimeoutMillis;
  private final HttpDataSource.RequestProperties requestProperties;
  private final String userAgent;
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate)
  {
    this(paramString, paramPredicate, null);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener)
  {
    this(paramString, paramPredicate, paramTransferListener, 8000, 8000);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener, int paramInt1, int paramInt2)
  {
    this(paramString, paramPredicate, paramTransferListener, paramInt1, paramInt2, false, null);
  }
  
  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener, int paramInt1, int paramInt2, boolean paramBoolean, HttpDataSource.RequestProperties paramRequestProperties)
  {
    this.userAgent = Assertions.checkNotEmpty(paramString);
    this.contentTypePredicate = paramPredicate;
    this.listener = paramTransferListener;
    this.requestProperties = new HttpDataSource.RequestProperties();
    this.connectTimeoutMillis = paramInt1;
    this.readTimeoutMillis = paramInt2;
    this.allowCrossProtocolRedirects = paramBoolean;
    this.defaultRequestProperties = paramRequestProperties;
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
  
  private static long getContentLength(HttpURLConnection paramHttpURLConnection)
  {
    l1 = -1L;
    String str = paramHttpURLConnection.getHeaderField("Content-Length");
    l2 = l1;
    if (!TextUtils.isEmpty(str)) {}
    try
    {
      l2 = Long.parseLong(str);
      paramHttpURLConnection = paramHttpURLConnection.getHeaderField("Content-Range");
      l1 = l2;
      Matcher localMatcher;
      if (!TextUtils.isEmpty(paramHttpURLConnection))
      {
        localMatcher = CONTENT_RANGE_HEADER.matcher(paramHttpURLConnection);
        l1 = l2;
        if (!localMatcher.find()) {}
      }
      try
      {
        l3 = Long.parseLong(localMatcher.group(2));
        l1 = Long.parseLong(localMatcher.group(1));
        l3 = l3 - l1 + 1L;
        if (l2 >= 0L) {
          break label143;
        }
        l1 = l3;
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        for (;;)
        {
          long l3;
          StringBuilder localStringBuilder;
          Log.e("DefaultHttpDataSource", "Unexpected Content-Range [" + paramHttpURLConnection + "]");
          l1 = l2;
        }
      }
      return l1;
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      for (;;)
      {
        Log.e("DefaultHttpDataSource", "Unexpected Content-Length [" + str + "]");
        l2 = l1;
        continue;
        label143:
        l1 = l2;
        if (l2 != l3)
        {
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          Log.w("DefaultHttpDataSource", "Inconsistent headers [" + str + "] [" + paramHttpURLConnection + "]");
          l1 = Math.max(l2, l3);
        }
      }
    }
  }
  
  private static URL handleRedirect(URL paramURL, String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new ProtocolException("Null location redirect");
    }
    paramString = new URL(paramURL, paramString);
    paramURL = paramString.getProtocol();
    if ((!"https".equals(paramURL)) && (!"http".equals(paramURL))) {
      throw new ProtocolException("Unsupported protocol redirect: " + paramURL);
    }
    return paramString;
  }
  
  private HttpURLConnection makeConnection(URL paramURL, byte[] paramArrayOfByte, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
    localHttpURLConnection.setConnectTimeout(this.connectTimeoutMillis);
    localHttpURLConnection.setReadTimeout(this.readTimeoutMillis);
    Object localObject;
    if (this.defaultRequestProperties != null)
    {
      paramURL = this.defaultRequestProperties.getSnapshot().entrySet().iterator();
      while (paramURL.hasNext())
      {
        localObject = (Map.Entry)paramURL.next();
        localHttpURLConnection.setRequestProperty((String)((Map.Entry)localObject).getKey(), (String)((Map.Entry)localObject).getValue());
      }
    }
    paramURL = this.requestProperties.getSnapshot().entrySet().iterator();
    while (paramURL.hasNext())
    {
      localObject = (Map.Entry)paramURL.next();
      localHttpURLConnection.setRequestProperty((String)((Map.Entry)localObject).getKey(), (String)((Map.Entry)localObject).getValue());
    }
    if ((paramLong1 != 0L) || (paramLong2 != -1L))
    {
      localObject = "bytes=" + paramLong1 + "-";
      paramURL = (URL)localObject;
      if (paramLong2 != -1L) {
        paramURL = (String)localObject + (paramLong1 + paramLong2 - 1L);
      }
      localHttpURLConnection.setRequestProperty("Range", paramURL);
    }
    localHttpURLConnection.setRequestProperty("User-Agent", this.userAgent);
    if (!paramBoolean1) {
      localHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
    }
    localHttpURLConnection.setInstanceFollowRedirects(paramBoolean2);
    if (paramArrayOfByte != null)
    {
      paramBoolean1 = true;
      localHttpURLConnection.setDoOutput(paramBoolean1);
      if (paramArrayOfByte == null) {
        break label365;
      }
      localHttpURLConnection.setRequestMethod("POST");
      if (paramArrayOfByte.length != 0) {
        break label335;
      }
      localHttpURLConnection.connect();
    }
    for (;;)
    {
      return localHttpURLConnection;
      paramBoolean1 = false;
      break;
      label335:
      localHttpURLConnection.setFixedLengthStreamingMode(paramArrayOfByte.length);
      localHttpURLConnection.connect();
      paramURL = localHttpURLConnection.getOutputStream();
      paramURL.write(paramArrayOfByte);
      paramURL.close();
      continue;
      label365:
      localHttpURLConnection.connect();
    }
  }
  
  private HttpURLConnection makeConnection(DataSpec paramDataSpec)
    throws IOException
  {
    Object localObject = new URL(paramDataSpec.uri.toString());
    byte[] arrayOfByte = paramDataSpec.postBody;
    long l1 = paramDataSpec.position;
    long l2 = paramDataSpec.length;
    boolean bool = paramDataSpec.isFlagSet(1);
    if (!this.allowCrossProtocolRedirects)
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
        break label189;
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
    label189:
    throw new NoRouteToHostException("Too many redirects: " + j);
  }
  
  private static void maybeTerminateInputStream(HttpURLConnection paramHttpURLConnection, long paramLong)
  {
    if ((Util.SDK_INT != 19) && (Util.SDK_INT != 20)) {}
    for (;;)
    {
      return;
      do
      {
        try
        {
          paramHttpURLConnection = paramHttpURLConnection.getInputStream();
          if (paramLong != -1L) {
            continue;
          }
          if (paramHttpURLConnection.read() == -1) {
            break;
          }
          Object localObject = paramHttpURLConnection.getClass().getName();
          if ((!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream")) && (!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream"))) {
            break;
          }
          localObject = paramHttpURLConnection.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
          ((Method)localObject).setAccessible(true);
          ((Method)localObject).invoke(paramHttpURLConnection, new Object[0]);
        }
        catch (Exception paramHttpURLConnection) {}
        break;
      } while (paramLong > 2048L);
    }
  }
  
  private int readInternal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return paramInt1;
      int i = paramInt2;
      if (this.bytesToRead != -1L)
      {
        long l = this.bytesToRead - this.bytesRead;
        if (l == 0L) {
          paramInt1 = -1;
        } else {
          i = (int)Math.min(paramInt2, l);
        }
      }
      else
      {
        paramInt2 = this.inputStream.read(paramArrayOfByte, paramInt1, i);
        if (paramInt2 == -1)
        {
          if (this.bytesToRead != -1L) {
            throw new EOFException();
          }
          paramInt1 = -1;
        }
        else
        {
          this.bytesRead += paramInt2;
          paramInt1 = paramInt2;
          if (this.listener != null)
          {
            this.listener.onBytesTransferred(this, paramInt2);
            paramInt1 = paramInt2;
          }
        }
      }
    }
  }
  
  private void skipInternal()
    throws IOException
  {
    if (this.bytesSkipped == this.bytesToSkip) {}
    for (;;)
    {
      return;
      byte[] arrayOfByte1 = (byte[])skipBufferReference.getAndSet(null);
      byte[] arrayOfByte2 = arrayOfByte1;
      if (arrayOfByte1 == null) {
        arrayOfByte2 = new byte['က'];
      }
      while (this.bytesSkipped != this.bytesToSkip)
      {
        int i = (int)Math.min(this.bytesToSkip - this.bytesSkipped, arrayOfByte2.length);
        i = this.inputStream.read(arrayOfByte2, 0, i);
        if (Thread.interrupted()) {
          throw new InterruptedIOException();
        }
        if (i == -1) {
          throw new EOFException();
        }
        this.bytesSkipped += i;
        if (this.listener != null) {
          this.listener.onBytesTransferred(this, i);
        }
      }
      skipBufferReference.set(arrayOfByte2);
    }
  }
  
  protected final long bytesRead()
  {
    return this.bytesRead;
  }
  
  protected final long bytesRemaining()
  {
    if (this.bytesToRead == -1L) {}
    for (long l = this.bytesToRead;; l = this.bytesToRead - this.bytesRead) {
      return l;
    }
  }
  
  protected final long bytesSkipped()
  {
    return this.bytesSkipped;
  }
  
  public void clearAllRequestProperties()
  {
    this.requestProperties.clear();
  }
  
  public void clearRequestProperty(String paramString)
  {
    Assertions.checkNotNull(paramString);
    this.requestProperties.remove(paramString);
  }
  
  /* Error */
  public void close()
    throws HttpDataSource.HttpDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 430	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   4: ifnull +21 -> 25
    //   7: aload_0
    //   8: getfield 116	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   11: aload_0
    //   12: invokevirtual 480	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesRemaining	()J
    //   15: invokestatic 482	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:maybeTerminateInputStream	(Ljava/net/HttpURLConnection;J)V
    //   18: aload_0
    //   19: getfield 430	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   22: invokevirtual 483	java/io/InputStream:close	()V
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 430	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   30: aload_0
    //   31: invokespecial 485	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   34: aload_0
    //   35: getfield 487	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   38: ifeq +25 -> 63
    //   41: aload_0
    //   42: iconst_0
    //   43: putfield 487	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   46: aload_0
    //   47: getfield 97	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   50: ifnull +13 -> 63
    //   53: aload_0
    //   54: getfield 97	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   57: aload_0
    //   58: invokeinterface 490 2 0
    //   63: return
    //   64: astore_1
    //   65: new 478	org/telegram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   68: astore_2
    //   69: aload_2
    //   70: aload_1
    //   71: aload_0
    //   72: getfield 492	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:dataSpec	Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;
    //   75: iconst_3
    //   76: invokespecial 495	org/telegram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/io/IOException;Lorg/telegram/messenger/exoplayer2/upstream/DataSpec;I)V
    //   79: aload_2
    //   80: athrow
    //   81: astore_2
    //   82: aload_0
    //   83: aconst_null
    //   84: putfield 430	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   87: aload_0
    //   88: invokespecial 485	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   91: aload_0
    //   92: getfield 487	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   95: ifeq +25 -> 120
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 487	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   103: aload_0
    //   104: getfield 97	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   107: ifnull +13 -> 120
    //   110: aload_0
    //   111: getfield 97	org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/telegram/messenger/exoplayer2/upstream/TransferListener;
    //   114: aload_0
    //   115: invokeinterface 490 2 0
    //   120: aload_2
    //   121: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	this	DefaultHttpDataSource
    //   64	7	1	localIOException	IOException
    //   68	12	2	localHttpDataSourceException	HttpDataSource.HttpDataSourceException
    //   81	40	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	25	64	java/io/IOException
    //   0	18	81	finally
    //   18	25	81	finally
    //   65	81	81	finally
  }
  
  protected final HttpURLConnection getConnection()
  {
    return this.connection;
  }
  
  public Map<String, List<String>> getResponseHeaders()
  {
    if (this.connection == null) {}
    for (Object localObject = null;; localObject = this.connection.getHeaderFields()) {
      return (Map<String, List<String>>)localObject;
    }
  }
  
  public Uri getUri()
  {
    if (this.connection == null) {}
    for (Uri localUri = null;; localUri = Uri.parse(this.connection.getURL().toString())) {
      return localUri;
    }
  }
  
  public long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException
  {
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
          break label171;
        }
        localMap = this.connection.getHeaderFields();
        closeConnectionQuietly();
        paramDataSpec = new HttpDataSource.InvalidResponseCodeException(i, localMap, paramDataSpec);
        if (i == 416) {
          paramDataSpec.initCause(new DataSourceException(0));
        }
        throw paramDataSpec;
      }
      catch (IOException localIOException2)
      {
        closeConnectionQuietly();
        throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + paramDataSpec.uri.toString(), localIOException2, paramDataSpec, 1);
      }
      localIOException1 = localIOException1;
      throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + paramDataSpec.uri.toString(), localIOException1, paramDataSpec, 1);
    }
    label171:
    String str;
    if ((this.contentTypePredicate != null) && (!this.contentTypePredicate.evaluate(str)))
    {
      closeConnectionQuietly();
      throw new HttpDataSource.InvalidContentTypeException(str, paramDataSpec);
    }
    long l;
    if ((i == 200) && (paramDataSpec.position != 0L))
    {
      l = paramDataSpec.position;
      this.bytesToSkip = l;
      if (paramDataSpec.isFlagSet(1)) {
        break label357;
      }
      if (paramDataSpec.length == -1L) {
        break label313;
      }
      this.bytesToRead = paramDataSpec.length;
    }
    for (;;)
    {
      try
      {
        this.inputStream = this.connection.getInputStream();
        this.opened = true;
        if (this.listener != null) {
          this.listener.onTransferStart(this, paramDataSpec);
        }
        return this.bytesToRead;
      }
      catch (IOException localIOException3)
      {
        label313:
        label357:
        closeConnectionQuietly();
        throw new HttpDataSource.HttpDataSourceException(localIOException3, paramDataSpec, 1);
      }
      l = 0L;
      break;
      l = getContentLength(this.connection);
      if (l != -1L)
      {
        l -= this.bytesToSkip;
        this.bytesToRead = l;
      }
      else
      {
        l = -1L;
        continue;
        this.bytesToRead = paramDataSpec.length;
      }
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
    this.requestProperties.set(paramString1, paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DefaultHttpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */