package org.telegram.messenger.secretmedia;

import android.content.Context;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;

public final class ExtendedDefaultDataSourceFactory implements DataSource.Factory {
    private final DataSource.Factory baseDataSourceFactory;
    private final Context context;
    private final TransferListener listener;

    public ExtendedDefaultDataSourceFactory(Context context2, String userAgent) {
        this(context2, userAgent, (TransferListener) null);
    }

    public ExtendedDefaultDataSourceFactory(Context context2, String userAgent, TransferListener listener2) {
        this(context2, listener2, (DataSource.Factory) new DefaultHttpDataSourceFactory(userAgent, listener2));
    }

    public ExtendedDefaultDataSourceFactory(Context context2, TransferListener listener2, DataSource.Factory baseDataSourceFactory2) {
        this.context = context2.getApplicationContext();
        this.listener = listener2;
        this.baseDataSourceFactory = baseDataSourceFactory2;
    }

    public ExtendedDefaultDataSource createDataSource() {
        return new ExtendedDefaultDataSource(this.context, this.listener, this.baseDataSourceFactory.createDataSource());
    }
}
