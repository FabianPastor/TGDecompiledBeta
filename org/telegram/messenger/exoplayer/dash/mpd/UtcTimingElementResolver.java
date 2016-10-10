package org.telegram.messenger.exoplayer.dash.mpd;

import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.upstream.UriDataSource;
import org.telegram.messenger.exoplayer.upstream.UriLoadable;
import org.telegram.messenger.exoplayer.upstream.UriLoadable.Parser;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class UtcTimingElementResolver
  implements Loader.Callback
{
  private final UtcTimingCallback callback;
  private UriLoadable<Long> singleUseLoadable;
  private Loader singleUseLoader;
  private final UtcTimingElement timingElement;
  private final long timingElementElapsedRealtime;
  private final UriDataSource uriDataSource;
  
  private UtcTimingElementResolver(UriDataSource paramUriDataSource, UtcTimingElement paramUtcTimingElement, long paramLong, UtcTimingCallback paramUtcTimingCallback)
  {
    this.uriDataSource = paramUriDataSource;
    this.timingElement = ((UtcTimingElement)Assertions.checkNotNull(paramUtcTimingElement));
    this.timingElementElapsedRealtime = paramLong;
    this.callback = ((UtcTimingCallback)Assertions.checkNotNull(paramUtcTimingCallback));
  }
  
  private void releaseLoader()
  {
    this.singleUseLoader.release();
  }
  
  private void resolve()
  {
    String str = this.timingElement.schemeIdUri;
    if (Util.areEqual(str, "urn:mpeg:dash:utc:direct:2012"))
    {
      resolveDirect();
      return;
    }
    if (Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2014"))
    {
      resolveHttp(new Iso8601Parser(null));
      return;
    }
    if ((Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2012")) || (Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2014")))
    {
      resolveHttp(new XsDateTimeParser(null));
      return;
    }
    this.callback.onTimestampError(this.timingElement, new IOException("Unsupported utc timing scheme"));
  }
  
  private void resolveDirect()
  {
    try
    {
      long l1 = Util.parseXsDateTime(this.timingElement.value);
      long l2 = this.timingElementElapsedRealtime;
      this.callback.onTimestampResolved(this.timingElement, l1 - l2);
      return;
    }
    catch (ParseException localParseException)
    {
      this.callback.onTimestampError(this.timingElement, new ParserException(localParseException));
    }
  }
  
  private void resolveHttp(UriLoadable.Parser<Long> paramParser)
  {
    this.singleUseLoader = new Loader("utctiming");
    this.singleUseLoadable = new UriLoadable(this.timingElement.value, this.uriDataSource, paramParser);
    this.singleUseLoader.startLoading(this.singleUseLoadable, this);
  }
  
  public static void resolveTimingElement(UriDataSource paramUriDataSource, UtcTimingElement paramUtcTimingElement, long paramLong, UtcTimingCallback paramUtcTimingCallback)
  {
    new UtcTimingElementResolver(paramUriDataSource, paramUtcTimingElement, paramLong, paramUtcTimingCallback).resolve();
  }
  
  public void onLoadCanceled(Loader.Loadable paramLoadable)
  {
    onLoadError(paramLoadable, new IOException("Load cancelled", new CancellationException()));
  }
  
  public void onLoadCompleted(Loader.Loadable paramLoadable)
  {
    releaseLoader();
    long l1 = ((Long)this.singleUseLoadable.getResult()).longValue();
    long l2 = SystemClock.elapsedRealtime();
    this.callback.onTimestampResolved(this.timingElement, l1 - l2);
  }
  
  public void onLoadError(Loader.Loadable paramLoadable, IOException paramIOException)
  {
    releaseLoader();
    this.callback.onTimestampError(this.timingElement, paramIOException);
  }
  
  private static class Iso8601Parser
    implements UriLoadable.Parser<Long>
  {
    public Long parse(String paramString, InputStream paramInputStream)
      throws ParserException, IOException
    {
      paramString = new BufferedReader(new InputStreamReader(paramInputStream)).readLine();
      try
      {
        paramInputStream = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        paramInputStream.setTimeZone(TimeZone.getTimeZone("UTC"));
        long l = paramInputStream.parse(paramString).getTime();
        return Long.valueOf(l);
      }
      catch (ParseException paramString)
      {
        throw new ParserException(paramString);
      }
    }
  }
  
  public static abstract interface UtcTimingCallback
  {
    public abstract void onTimestampError(UtcTimingElement paramUtcTimingElement, IOException paramIOException);
    
    public abstract void onTimestampResolved(UtcTimingElement paramUtcTimingElement, long paramLong);
  }
  
  private static class XsDateTimeParser
    implements UriLoadable.Parser<Long>
  {
    public Long parse(String paramString, InputStream paramInputStream)
      throws ParserException, IOException
    {
      paramString = new BufferedReader(new InputStreamReader(paramInputStream)).readLine();
      try
      {
        long l = Util.parseXsDateTime(paramString);
        return Long.valueOf(l);
      }
      catch (ParseException paramString)
      {
        throw new ParserException(paramString);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/mpd/UtcTimingElementResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */