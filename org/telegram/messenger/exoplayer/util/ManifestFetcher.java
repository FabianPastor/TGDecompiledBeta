package org.telegram.messenger.exoplayer.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.upstream.UriDataSource;
import org.telegram.messenger.exoplayer.upstream.UriLoadable;
import org.telegram.messenger.exoplayer.upstream.UriLoadable.Parser;

public class ManifestFetcher<T>
  implements Loader.Callback
{
  private long currentLoadStartTimestamp;
  private UriLoadable<T> currentLoadable;
  private int enabledCount;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private ManifestIOException loadException;
  private int loadExceptionCount;
  private long loadExceptionTimestamp;
  private Loader loader;
  private volatile T manifest;
  private volatile long manifestLoadCompleteTimestamp;
  private volatile long manifestLoadStartTimestamp;
  volatile String manifestUri;
  private final UriLoadable.Parser<T> parser;
  private final UriDataSource uriDataSource;
  
  public ManifestFetcher(String paramString, UriDataSource paramUriDataSource, UriLoadable.Parser<T> paramParser)
  {
    this(paramString, paramUriDataSource, paramParser, null, null);
  }
  
  public ManifestFetcher(String paramString, UriDataSource paramUriDataSource, UriLoadable.Parser<T> paramParser, Handler paramHandler, EventListener paramEventListener)
  {
    this.parser = paramParser;
    this.manifestUri = paramString;
    this.uriDataSource = paramUriDataSource;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
  }
  
  private long getRetryDelayMillis(long paramLong)
  {
    return Math.min((paramLong - 1L) * 1000L, 5000L);
  }
  
  private void notifyManifestError(final IOException paramIOException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ManifestFetcher.this.eventListener.onManifestError(paramIOException);
        }
      });
    }
  }
  
  private void notifyManifestRefreshStarted()
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ManifestFetcher.this.eventListener.onManifestRefreshStarted();
        }
      });
    }
  }
  
  private void notifyManifestRefreshed()
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ManifestFetcher.this.eventListener.onManifestRefreshed();
        }
      });
    }
  }
  
  public void disable()
  {
    int i = this.enabledCount - 1;
    this.enabledCount = i;
    if ((i == 0) && (this.loader != null))
    {
      this.loader.release();
      this.loader = null;
    }
  }
  
  public void enable()
  {
    int i = this.enabledCount;
    this.enabledCount = (i + 1);
    if (i == 0)
    {
      this.loadExceptionCount = 0;
      this.loadException = null;
    }
  }
  
  public T getManifest()
  {
    return (T)this.manifest;
  }
  
  public long getManifestLoadCompleteTimestamp()
  {
    return this.manifestLoadCompleteTimestamp;
  }
  
  public long getManifestLoadStartTimestamp()
  {
    return this.manifestLoadStartTimestamp;
  }
  
  public void maybeThrowError()
    throws ManifestFetcher.ManifestIOException
  {
    if ((this.loadException == null) || (this.loadExceptionCount <= 1)) {
      return;
    }
    throw this.loadException;
  }
  
  public void onLoadCanceled(Loader.Loadable paramLoadable) {}
  
  public void onLoadCompleted(Loader.Loadable paramLoadable)
  {
    if (this.currentLoadable != paramLoadable) {
      return;
    }
    this.manifest = this.currentLoadable.getResult();
    this.manifestLoadStartTimestamp = this.currentLoadStartTimestamp;
    this.manifestLoadCompleteTimestamp = SystemClock.elapsedRealtime();
    this.loadExceptionCount = 0;
    this.loadException = null;
    if ((this.manifest instanceof RedirectingManifest))
    {
      paramLoadable = ((RedirectingManifest)this.manifest).getNextManifestUri();
      if (!TextUtils.isEmpty(paramLoadable)) {
        this.manifestUri = paramLoadable;
      }
    }
    notifyManifestRefreshed();
  }
  
  public void onLoadError(Loader.Loadable paramLoadable, IOException paramIOException)
  {
    if (this.currentLoadable != paramLoadable) {
      return;
    }
    this.loadExceptionCount += 1;
    this.loadExceptionTimestamp = SystemClock.elapsedRealtime();
    this.loadException = new ManifestIOException(paramIOException);
    notifyManifestError(this.loadException);
  }
  
  void onSingleFetchCompleted(T paramT, long paramLong)
  {
    this.manifest = paramT;
    this.manifestLoadStartTimestamp = paramLong;
    this.manifestLoadCompleteTimestamp = SystemClock.elapsedRealtime();
  }
  
  public void requestRefresh()
  {
    if ((this.loadException != null) && (SystemClock.elapsedRealtime() < this.loadExceptionTimestamp + getRetryDelayMillis(this.loadExceptionCount))) {}
    do
    {
      return;
      if (this.loader == null) {
        this.loader = new Loader("manifestLoader");
      }
    } while (this.loader.isLoading());
    this.currentLoadable = new UriLoadable(this.manifestUri, this.uriDataSource, this.parser);
    this.currentLoadStartTimestamp = SystemClock.elapsedRealtime();
    this.loader.startLoading(this.currentLoadable, this);
    notifyManifestRefreshStarted();
  }
  
  public void singleLoad(Looper paramLooper, ManifestCallback<T> paramManifestCallback)
  {
    new SingleFetchHelper(new UriLoadable(this.manifestUri, this.uriDataSource, this.parser), paramLooper, paramManifestCallback).startLoading();
  }
  
  public void updateManifestUri(String paramString)
  {
    this.manifestUri = paramString;
  }
  
  public static abstract interface EventListener
  {
    public abstract void onManifestError(IOException paramIOException);
    
    public abstract void onManifestRefreshStarted();
    
    public abstract void onManifestRefreshed();
  }
  
  public static abstract interface ManifestCallback<T>
  {
    public abstract void onSingleManifest(T paramT);
    
    public abstract void onSingleManifestError(IOException paramIOException);
  }
  
  public static final class ManifestIOException
    extends IOException
  {
    public ManifestIOException(Throwable paramThrowable)
    {
      super();
    }
  }
  
  public static abstract interface RedirectingManifest
  {
    public abstract String getNextManifestUri();
  }
  
  private class SingleFetchHelper
    implements Loader.Callback
  {
    private final Looper callbackLooper;
    private long loadStartTimestamp;
    private final UriLoadable<T> singleUseLoadable;
    private final Loader singleUseLoader;
    private final ManifestFetcher.ManifestCallback<T> wrappedCallback;
    
    public SingleFetchHelper(Looper paramLooper, ManifestFetcher.ManifestCallback<T> paramManifestCallback)
    {
      this.singleUseLoadable = paramLooper;
      this.callbackLooper = paramManifestCallback;
      ManifestFetcher.ManifestCallback localManifestCallback;
      this.wrappedCallback = localManifestCallback;
      this.singleUseLoader = new Loader("manifestLoader:single");
    }
    
    private void releaseLoader()
    {
      this.singleUseLoader.release();
    }
    
    public void onLoadCanceled(Loader.Loadable paramLoadable)
    {
      try
      {
        paramLoadable = new ManifestFetcher.ManifestIOException(new CancellationException());
        this.wrappedCallback.onSingleManifestError(paramLoadable);
        return;
      }
      finally
      {
        releaseLoader();
      }
    }
    
    public void onLoadCompleted(Loader.Loadable paramLoadable)
    {
      try
      {
        paramLoadable = this.singleUseLoadable.getResult();
        ManifestFetcher.this.onSingleFetchCompleted(paramLoadable, this.loadStartTimestamp);
        this.wrappedCallback.onSingleManifest(paramLoadable);
        return;
      }
      finally
      {
        releaseLoader();
      }
    }
    
    public void onLoadError(Loader.Loadable paramLoadable, IOException paramIOException)
    {
      try
      {
        this.wrappedCallback.onSingleManifestError(paramIOException);
        return;
      }
      finally
      {
        releaseLoader();
      }
    }
    
    public void startLoading()
    {
      this.loadStartTimestamp = SystemClock.elapsedRealtime();
      this.singleUseLoader.startLoading(this.callbackLooper, this.singleUseLoadable, this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/ManifestFetcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */