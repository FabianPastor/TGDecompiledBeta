package org.telegram.messenger.secretmedia;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.FileStreamLoadOperation;

public final class ExtendedDefaultDataSource implements DataSource {
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RAW = "rawresource";
    private static final String SCHEME_RTMP = "rtmp";
    private static final String TAG = "ExtendedDefaultDataSource";
    private DataSource assetDataSource;
    private final DataSource baseDataSource;
    private DataSource contentDataSource;
    private final Context context;
    private DataSource dataSchemeDataSource;
    private DataSource dataSource;
    private DataSource encryptedFileDataSource;
    private DataSource fileDataSource;
    private DataSource rawResourceDataSource;
    private DataSource rtmpDataSource;
    private final List<TransferListener> transferListeners;

    public ExtendedDefaultDataSource(Context context2, String str, boolean z) {
        this(context2, str, 8000, 8000, z);
    }

    public ExtendedDefaultDataSource(Context context2, String str, int i, int i2, boolean z) {
        this(context2, new DefaultHttpDataSource(str, (Predicate<String>) null, i, i2, z, (HttpDataSource.RequestProperties) null));
    }

    public ExtendedDefaultDataSource(Context context2, DataSource dataSource2) {
        this.context = context2.getApplicationContext();
        this.baseDataSource = (DataSource) Assertions.checkNotNull(dataSource2);
        this.transferListeners = new ArrayList();
    }

    @Deprecated
    public ExtendedDefaultDataSource(Context context2, TransferListener transferListener, String str, boolean z) {
        this(context2, transferListener, str, 8000, 8000, z);
    }

    @Deprecated
    public ExtendedDefaultDataSource(Context context2, TransferListener transferListener, String str, int i, int i2, boolean z) {
        this(context2, transferListener, (DataSource) new DefaultHttpDataSource(str, i, i2, z, (HttpDataSource.RequestProperties) null));
    }

    @Deprecated
    public ExtendedDefaultDataSource(Context context2, TransferListener transferListener, DataSource dataSource2) {
        this(context2, dataSource2);
        if (transferListener != null) {
            this.transferListeners.add(transferListener);
            dataSource2.addTransferListener(transferListener);
        }
    }

    public void addTransferListener(TransferListener transferListener) {
        this.baseDataSource.addTransferListener(transferListener);
        this.transferListeners.add(transferListener);
        maybeAddListenerToDataSource(this.fileDataSource, transferListener);
        maybeAddListenerToDataSource(this.assetDataSource, transferListener);
        maybeAddListenerToDataSource(this.contentDataSource, transferListener);
        maybeAddListenerToDataSource(this.rtmpDataSource, transferListener);
        maybeAddListenerToDataSource(this.dataSchemeDataSource, transferListener);
        maybeAddListenerToDataSource(this.rawResourceDataSource, transferListener);
    }

    public long open(DataSpec dataSpec) throws IOException {
        Assertions.checkState(this.dataSource == null);
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            String path = dataSpec.uri.getPath();
            if (path != null && path.startsWith("/android_asset/")) {
                this.dataSource = getAssetDataSource();
            } else if (dataSpec.uri.getPath().endsWith(".enc")) {
                this.dataSource = getEncryptedFileDataSource();
            } else {
                this.dataSource = getFileDataSource();
            }
        } else if ("tg".equals(scheme)) {
            this.dataSource = getStreamDataSource();
        } else if ("asset".equals(scheme)) {
            this.dataSource = getAssetDataSource();
        } else if ("content".equals(scheme)) {
            this.dataSource = getContentDataSource();
        } else if ("rtmp".equals(scheme)) {
            this.dataSource = getRtmpDataSource();
        } else if ("data".equals(scheme)) {
            this.dataSource = getDataSchemeDataSource();
        } else if ("rawresource".equals(scheme)) {
            this.dataSource = getRawResourceDataSource();
        } else {
            this.dataSource = this.baseDataSource;
        }
        return this.dataSource.open(dataSpec);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        return ((DataSource) Assertions.checkNotNull(this.dataSource)).read(bArr, i, i2);
    }

    public Uri getUri() {
        DataSource dataSource2 = this.dataSource;
        if (dataSource2 == null) {
            return null;
        }
        return dataSource2.getUri();
    }

    public Map<String, List<String>> getResponseHeaders() {
        DataSource dataSource2 = this.dataSource;
        return dataSource2 == null ? Collections.emptyMap() : dataSource2.getResponseHeaders();
    }

    public void close() throws IOException {
        DataSource dataSource2 = this.dataSource;
        if (dataSource2 != null) {
            try {
                dataSource2.close();
            } finally {
                this.dataSource = null;
            }
        }
    }

    private DataSource getFileDataSource() {
        if (this.fileDataSource == null) {
            FileDataSource fileDataSource2 = new FileDataSource();
            this.fileDataSource = fileDataSource2;
            addListenersToDataSource(fileDataSource2);
        }
        return this.fileDataSource;
    }

    private DataSource getAssetDataSource() {
        if (this.assetDataSource == null) {
            AssetDataSource assetDataSource2 = new AssetDataSource(this.context);
            this.assetDataSource = assetDataSource2;
            addListenersToDataSource(assetDataSource2);
        }
        return this.assetDataSource;
    }

    private DataSource getEncryptedFileDataSource() {
        if (this.encryptedFileDataSource == null) {
            EncryptedFileDataSource encryptedFileDataSource2 = new EncryptedFileDataSource();
            this.encryptedFileDataSource = encryptedFileDataSource2;
            addListenersToDataSource(encryptedFileDataSource2);
        }
        return this.encryptedFileDataSource;
    }

    private DataSource getStreamDataSource() {
        FileStreamLoadOperation fileStreamLoadOperation = new FileStreamLoadOperation();
        addListenersToDataSource(fileStreamLoadOperation);
        return fileStreamLoadOperation;
    }

    private DataSource getContentDataSource() {
        if (this.contentDataSource == null) {
            ContentDataSource contentDataSource2 = new ContentDataSource(this.context);
            this.contentDataSource = contentDataSource2;
            addListenersToDataSource(contentDataSource2);
        }
        return this.contentDataSource;
    }

    private DataSource getRtmpDataSource() {
        if (this.rtmpDataSource == null) {
            try {
                DataSource dataSource2 = (DataSource) Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource").getConstructor(new Class[0]).newInstance(new Object[0]);
                this.rtmpDataSource = dataSource2;
                addListenersToDataSource(dataSource2);
            } catch (ClassNotFoundException unused) {
                Log.w("ExtendedDefaultDataSource", "Attempting to play RTMP stream without depending on the RTMP extension");
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating RTMP extension", e);
            }
            if (this.rtmpDataSource == null) {
                this.rtmpDataSource = this.baseDataSource;
            }
        }
        return this.rtmpDataSource;
    }

    private DataSource getDataSchemeDataSource() {
        if (this.dataSchemeDataSource == null) {
            DataSchemeDataSource dataSchemeDataSource2 = new DataSchemeDataSource();
            this.dataSchemeDataSource = dataSchemeDataSource2;
            addListenersToDataSource(dataSchemeDataSource2);
        }
        return this.dataSchemeDataSource;
    }

    private DataSource getRawResourceDataSource() {
        if (this.rawResourceDataSource == null) {
            RawResourceDataSource rawResourceDataSource2 = new RawResourceDataSource(this.context);
            this.rawResourceDataSource = rawResourceDataSource2;
            addListenersToDataSource(rawResourceDataSource2);
        }
        return this.rawResourceDataSource;
    }

    private void addListenersToDataSource(DataSource dataSource2) {
        for (int i = 0; i < this.transferListeners.size(); i++) {
            dataSource2.addTransferListener(this.transferListeners.get(i));
        }
    }

    private void maybeAddListenerToDataSource(DataSource dataSource2, TransferListener transferListener) {
        if (dataSource2 != null) {
            dataSource2.addTransferListener(transferListener);
        }
    }
}
