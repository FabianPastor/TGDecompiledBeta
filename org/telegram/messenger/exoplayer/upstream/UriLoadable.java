package org.telegram.messenger.exoplayer.upstream;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;

public final class UriLoadable<T> implements Loadable {
    private final DataSpec dataSpec;
    private volatile boolean isCanceled;
    private final Parser<T> parser;
    private volatile T result;
    private final UriDataSource uriDataSource;

    public interface Parser<T> {
        T parse(String str, InputStream inputStream) throws ParserException, IOException;
    }

    public UriLoadable(String url, UriDataSource uriDataSource, Parser<T> parser) {
        this.uriDataSource = uriDataSource;
        this.parser = parser;
        this.dataSpec = new DataSpec(Uri.parse(url), 1);
    }

    public final T getResult() {
        return this.result;
    }

    public final void cancelLoad() {
        this.isCanceled = true;
    }

    public final boolean isLoadCanceled() {
        return this.isCanceled;
    }

    public final void load() throws IOException, InterruptedException {
        DataSourceInputStream inputStream = new DataSourceInputStream(this.uriDataSource, this.dataSpec);
        try {
            inputStream.open();
            this.result = this.parser.parse(this.uriDataSource.getUri(), inputStream);
        } finally {
            inputStream.close();
        }
    }
}
