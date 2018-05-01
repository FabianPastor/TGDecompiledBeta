package org.telegram.messenger.exoplayer2.upstream;

import android.text.TextUtils;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.util.Predicate;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract interface HttpDataSource
  extends DataSource
{
  public static final Predicate<String> REJECT_PAYWALL_TYPES = new Predicate()
  {
    public boolean evaluate(String paramAnonymousString)
    {
      paramAnonymousString = Util.toLowerInvariant(paramAnonymousString);
      if ((!TextUtils.isEmpty(paramAnonymousString)) && ((!paramAnonymousString.contains("text")) || (paramAnonymousString.contains("text/vtt"))) && (!paramAnonymousString.contains("html")) && (!paramAnonymousString.contains("xml"))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
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
  
  public static abstract class BaseFactory
    implements HttpDataSource.Factory
  {
    private final HttpDataSource.RequestProperties defaultRequestProperties = new HttpDataSource.RequestProperties();
    
    @Deprecated
    public final void clearAllDefaultRequestProperties()
    {
      this.defaultRequestProperties.clear();
    }
    
    @Deprecated
    public final void clearDefaultRequestProperty(String paramString)
    {
      this.defaultRequestProperties.remove(paramString);
    }
    
    public final HttpDataSource createDataSource()
    {
      return createDataSourceInternal(this.defaultRequestProperties);
    }
    
    protected abstract HttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties paramRequestProperties);
    
    public final HttpDataSource.RequestProperties getDefaultRequestProperties()
    {
      return this.defaultRequestProperties;
    }
    
    @Deprecated
    public final void setDefaultRequestProperty(String paramString1, String paramString2)
    {
      this.defaultRequestProperties.set(paramString1, paramString2);
    }
  }
  
  public static abstract interface Factory
    extends DataSource.Factory
  {
    @Deprecated
    public abstract void clearAllDefaultRequestProperties();
    
    @Deprecated
    public abstract void clearDefaultRequestProperty(String paramString);
    
    public abstract HttpDataSource createDataSource();
    
    public abstract HttpDataSource.RequestProperties getDefaultRequestProperties();
    
    @Deprecated
    public abstract void setDefaultRequestProperty(String paramString1, String paramString2);
  }
  
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
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Type {}
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
  
  public static final class RequestProperties
  {
    private final Map<String, String> requestProperties = new HashMap();
    private Map<String, String> requestPropertiesSnapshot;
    
    public void clear()
    {
      try
      {
        this.requestPropertiesSnapshot = null;
        this.requestProperties.clear();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void clearAndSet(Map<String, String> paramMap)
    {
      try
      {
        this.requestPropertiesSnapshot = null;
        this.requestProperties.clear();
        this.requestProperties.putAll(paramMap);
        return;
      }
      finally
      {
        paramMap = finally;
        throw paramMap;
      }
    }
    
    public Map<String, String> getSnapshot()
    {
      try
      {
        if (this.requestPropertiesSnapshot == null)
        {
          localObject1 = new java/util/HashMap;
          ((HashMap)localObject1).<init>(this.requestProperties);
          this.requestPropertiesSnapshot = Collections.unmodifiableMap((Map)localObject1);
        }
        Object localObject1 = this.requestPropertiesSnapshot;
        return (Map<String, String>)localObject1;
      }
      finally {}
    }
    
    public void remove(String paramString)
    {
      try
      {
        this.requestPropertiesSnapshot = null;
        this.requestProperties.remove(paramString);
        return;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public void set(String paramString1, String paramString2)
    {
      try
      {
        this.requestPropertiesSnapshot = null;
        this.requestProperties.put(paramString1, paramString2);
        return;
      }
      finally
      {
        paramString1 = finally;
        throw paramString1;
      }
    }
    
    public void set(Map<String, String> paramMap)
    {
      try
      {
        this.requestPropertiesSnapshot = null;
        this.requestProperties.putAll(paramMap);
        return;
      }
      finally
      {
        paramMap = finally;
        throw paramMap;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/HttpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */