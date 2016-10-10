package org.telegram.messenger.exoplayer.upstream;

import android.text.TextUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer.util.Predicate;
import org.telegram.messenger.exoplayer.util.Util;

public abstract interface HttpDataSource
  extends UriDataSource
{
  public static final Predicate<String> REJECT_PAYWALL_TYPES = new Predicate()
  {
    public boolean evaluate(String paramAnonymousString)
    {
      paramAnonymousString = Util.toLowerInvariant(paramAnonymousString);
      return (!TextUtils.isEmpty(paramAnonymousString)) && ((!paramAnonymousString.contains("text")) || (paramAnonymousString.contains("text/vtt"))) && (!paramAnonymousString.contains("html")) && (!paramAnonymousString.contains("xml"));
    }
  };
  
  public abstract void clearAllRequestProperties();
  
  public abstract void clearRequestProperty(String paramString);
  
  public abstract void close()
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract Map<String, List<String>> getResponseHeaders();
  
  public abstract long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract void setRequestProperty(String paramString1, String paramString2);
  
  public static class HttpDataSourceException
    extends IOException
  {
    public static final int TYPE_CLOSE = 3;
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_READ = 2;
    public final DataSpec dataSpec;
    public final int type;
    
    public HttpDataSourceException(IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(String paramString, IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super(paramIOException);
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(String paramString, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(DataSpec paramDataSpec, int paramInt)
    {
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
  }
  
  public static final class InvalidContentTypeException
    extends HttpDataSource.HttpDataSourceException
  {
    public final String contentType;
    
    public InvalidContentTypeException(String paramString, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.contentType = paramString;
    }
  }
  
  public static final class InvalidResponseCodeException
    extends HttpDataSource.HttpDataSourceException
  {
    public final Map<String, List<String>> headerFields;
    public final int responseCode;
    
    public InvalidResponseCodeException(int paramInt, Map<String, List<String>> paramMap, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.responseCode = paramInt;
      this.headerFields = paramMap;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/HttpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */