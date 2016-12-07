package org.telegram.messenger.exoplayer.dash.mpd;

import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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

public final class UtcTimingElementResolver implements Callback {
    private final UtcTimingCallback callback;
    private UriLoadable<Long> singleUseLoadable;
    private Loader singleUseLoader;
    private final UtcTimingElement timingElement;
    private final long timingElementElapsedRealtime;
    private final UriDataSource uriDataSource;

    public interface UtcTimingCallback {
        void onTimestampError(UtcTimingElement utcTimingElement, IOException iOException);

        void onTimestampResolved(UtcTimingElement utcTimingElement, long j);
    }

    private static class Iso8601Parser implements Parser<Long> {
        private Iso8601Parser() {
        }

        public Long parse(String connectionUrl, InputStream inputStream) throws ParserException, IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                return Long.valueOf(format.parse(firstLine).getTime());
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private static class XsDateTimeParser implements Parser<Long> {
        private XsDateTimeParser() {
        }

        public Long parse(String connectionUrl, InputStream inputStream) throws ParserException, IOException {
            try {
                return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    public static void resolveTimingElement(UriDataSource uriDataSource, UtcTimingElement timingElement, long timingElementElapsedRealtime, UtcTimingCallback callback) {
        new UtcTimingElementResolver(uriDataSource, timingElement, timingElementElapsedRealtime, callback).resolve();
    }

    private UtcTimingElementResolver(UriDataSource uriDataSource, UtcTimingElement timingElement, long timingElementElapsedRealtime, UtcTimingCallback callback) {
        this.uriDataSource = uriDataSource;
        this.timingElement = (UtcTimingElement) Assertions.checkNotNull(timingElement);
        this.timingElementElapsedRealtime = timingElementElapsedRealtime;
        this.callback = (UtcTimingCallback) Assertions.checkNotNull(callback);
    }

    private void resolve() {
        String scheme = this.timingElement.schemeIdUri;
        if (Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
            resolveDirect();
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014")) {
            resolveHttp(new Iso8601Parser());
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014")) {
            resolveHttp(new XsDateTimeParser());
        } else {
            this.callback.onTimestampError(this.timingElement, new IOException("Unsupported utc timing scheme"));
        }
    }

    private void resolveDirect() {
        try {
            this.callback.onTimestampResolved(this.timingElement, Util.parseXsDateTime(this.timingElement.value) - this.timingElementElapsedRealtime);
        } catch (Throwable e) {
            this.callback.onTimestampError(this.timingElement, new ParserException(e));
        }
    }

    private void resolveHttp(Parser<Long> parser) {
        this.singleUseLoader = new Loader("utctiming");
        this.singleUseLoadable = new UriLoadable(this.timingElement.value, this.uriDataSource, parser);
        this.singleUseLoader.startLoading(this.singleUseLoadable, this);
    }

    public void onLoadCanceled(Loadable loadable) {
        onLoadError(loadable, new IOException("Load cancelled", new CancellationException()));
    }

    public void onLoadCompleted(Loadable loadable) {
        releaseLoader();
        this.callback.onTimestampResolved(this.timingElement, ((Long) this.singleUseLoadable.getResult()).longValue() - SystemClock.elapsedRealtime());
    }

    public void onLoadError(Loadable loadable, IOException exception) {
        releaseLoader();
        this.callback.onTimestampError(this.timingElement, exception);
    }

    private void releaseLoader() {
        this.singleUseLoader.release();
    }
}
