package org.telegram.messenger.secretmedia;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource$$CC;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Map;
import org.telegram.messenger.FileLoader;

public final class ExtendedDefaultDataSource implements DataSource {
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private final DataSource assetDataSource;
    private final DataSource baseDataSource;
    private final DataSource contentDataSource;
    private DataSource dataSource;
    private final DataSource encryptedFileDataSource;
    private final DataSource fileDataSource;
    private TransferListener listener;

    public void addTransferListener(TransferListener transferListener) {
        DataSource$$CC.addTransferListener(this, transferListener);
    }

    public Map getResponseHeaders() {
        return DataSource$$CC.getResponseHeaders(this);
    }

    public ExtendedDefaultDataSource(Context context, TransferListener listener, String userAgent, boolean allowCrossProtocolRedirects) {
        this(context, listener, userAgent, 8000, 8000, allowCrossProtocolRedirects);
    }

    public ExtendedDefaultDataSource(Context context, TransferListener listener, String userAgent, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects) {
        this(context, listener, new DefaultHttpDataSource(userAgent, null, listener, connectTimeoutMillis, readTimeoutMillis, allowCrossProtocolRedirects, null));
    }

    public ExtendedDefaultDataSource(Context context, TransferListener listener, DataSource baseDataSource) {
        this.baseDataSource = (DataSource) Assertions.checkNotNull(baseDataSource);
        this.fileDataSource = new FileDataSource(listener);
        this.encryptedFileDataSource = new EncryptedFileDataSource(listener);
        this.assetDataSource = new AssetDataSource(context, listener);
        this.contentDataSource = new ContentDataSource(context, listener);
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        Assertions.checkState(this.dataSource == null);
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            if (dataSpec.uri.getPath().startsWith("/android_asset/")) {
                this.dataSource = this.assetDataSource;
            } else if (dataSpec.uri.getPath().endsWith(".enc")) {
                this.dataSource = this.encryptedFileDataSource;
            } else {
                this.dataSource = this.fileDataSource;
            }
        } else if ("tg".equals(scheme)) {
            this.dataSource = FileLoader.getStreamLoadOperation(this.listener);
        } else if (SCHEME_ASSET.equals(scheme)) {
            this.dataSource = this.assetDataSource;
        } else if (SCHEME_CONTENT.equals(scheme)) {
            this.dataSource = this.contentDataSource;
        } else {
            this.dataSource = this.baseDataSource;
        }
        return this.dataSource.open(dataSpec);
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return this.dataSource.read(buffer, offset, readLength);
    }

    public Uri getUri() {
        return this.dataSource == null ? null : this.dataSource.getUri();
    }

    public void close() throws IOException {
        if (this.dataSource != null) {
            try {
                this.dataSource.close();
            } finally {
                this.dataSource = null;
            }
        }
    }
}
