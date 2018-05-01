package org.telegram.messenger.exoplayer2.upstream;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultDataSource implements DataSource {
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RAW = "rawresource";
    private static final String SCHEME_RTMP = "rtmp";
    private static final String TAG = "DefaultDataSource";
    private DataSource assetDataSource;
    private final DataSource baseDataSource;
    private DataSource contentDataSource;
    private final Context context;
    private DataSource dataSchemeDataSource;
    private DataSource dataSource;
    private DataSource fileDataSource;
    private final TransferListener<? super DataSource> listener;
    private DataSource rawResourceDataSource;
    private DataSource rtmpDataSource;

    public DefaultDataSource(Context context, TransferListener<? super DataSource> transferListener, String str, boolean z) {
        this(context, transferListener, str, 8000, 8000, z);
    }

    public DefaultDataSource(Context context, TransferListener<? super DataSource> transferListener, String str, int i, int i2, boolean z) {
        this(context, transferListener, new DefaultHttpDataSource(str, null, transferListener, i, i2, z, null));
    }

    public DefaultDataSource(Context context, TransferListener<? super DataSource> transferListener, DataSource dataSource) {
        this.context = context.getApplicationContext();
        this.listener = transferListener;
        this.baseDataSource = (DataSource) Assertions.checkNotNull(dataSource);
    }

    public long open(DataSpec dataSpec) throws IOException {
        Assertions.checkState(this.dataSource == null);
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            if (dataSpec.uri.getPath().startsWith("/android_asset/")) {
                this.dataSource = getAssetDataSource();
            } else {
                this.dataSource = getFileDataSource();
            }
        } else if (SCHEME_ASSET.equals(scheme)) {
            this.dataSource = getAssetDataSource();
        } else if (SCHEME_CONTENT.equals(scheme)) {
            this.dataSource = getContentDataSource();
        } else if (SCHEME_RTMP.equals(scheme)) {
            this.dataSource = getRtmpDataSource();
        } else if (DataSchemeDataSource.SCHEME_DATA.equals(scheme)) {
            this.dataSource = getDataSchemeDataSource();
        } else if ("rawresource".equals(scheme)) {
            this.dataSource = getRawResourceDataSource();
        } else {
            this.dataSource = this.baseDataSource;
        }
        return this.dataSource.open(dataSpec);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        return this.dataSource.read(bArr, i, i2);
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

    private DataSource getFileDataSource() {
        if (this.fileDataSource == null) {
            this.fileDataSource = new FileDataSource(this.listener);
        }
        return this.fileDataSource;
    }

    private DataSource getAssetDataSource() {
        if (this.assetDataSource == null) {
            this.assetDataSource = new AssetDataSource(this.context, this.listener);
        }
        return this.assetDataSource;
    }

    private DataSource getContentDataSource() {
        if (this.contentDataSource == null) {
            this.contentDataSource = new ContentDataSource(this.context, this.listener);
        }
        return this.contentDataSource;
    }

    private org.telegram.messenger.exoplayer2.upstream.DataSource getRtmpDataSource() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = r3.rtmpDataSource;
        if (r0 != 0) goto L_0x004f;
    L_0x0004:
        r0 = "org.telegram.messenger.exoplayer2.ext.rtmp.RtmpDataSource";	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r0 = java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r1 = 0;	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r2 = new java.lang.Class[r1];	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r0 = r0.getDeclaredConstructor(r2);	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r1 = new java.lang.Object[r1];	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r0 = r0.newInstance(r1);	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r0 = (org.telegram.messenger.exoplayer2.upstream.DataSource) r0;	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        r3.rtmpDataSource = r0;	 Catch:{ ClassNotFoundException -> 0x0040, InstantiationException -> 0x0037, IllegalAccessException -> 0x002e, NoSuchMethodException -> 0x0025, InvocationTargetException -> 0x001c }
        goto L_0x0047;
    L_0x001c:
        r0 = move-exception;
        r1 = "DefaultDataSource";
        r2 = "Error instantiating RtmpDataSource";
        android.util.Log.e(r1, r2, r0);
        goto L_0x0047;
    L_0x0025:
        r0 = move-exception;
        r1 = "DefaultDataSource";
        r2 = "Error instantiating RtmpDataSource";
        android.util.Log.e(r1, r2, r0);
        goto L_0x0047;
    L_0x002e:
        r0 = move-exception;
        r1 = "DefaultDataSource";
        r2 = "Error instantiating RtmpDataSource";
        android.util.Log.e(r1, r2, r0);
        goto L_0x0047;
    L_0x0037:
        r0 = move-exception;
        r1 = "DefaultDataSource";
        r2 = "Error instantiating RtmpDataSource";
        android.util.Log.e(r1, r2, r0);
        goto L_0x0047;
    L_0x0040:
        r0 = "DefaultDataSource";
        r1 = "Attempting to play RTMP stream without depending on the RTMP extension";
        android.util.Log.w(r0, r1);
    L_0x0047:
        r0 = r3.rtmpDataSource;
        if (r0 != 0) goto L_0x004f;
    L_0x004b:
        r0 = r3.baseDataSource;
        r3.rtmpDataSource = r0;
    L_0x004f:
        r0 = r3.rtmpDataSource;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.DefaultDataSource.getRtmpDataSource():org.telegram.messenger.exoplayer2.upstream.DataSource");
    }

    private DataSource getDataSchemeDataSource() {
        if (this.dataSchemeDataSource == null) {
            this.dataSchemeDataSource = new DataSchemeDataSource();
        }
        return this.dataSchemeDataSource;
    }

    private DataSource getRawResourceDataSource() {
        if (this.rawResourceDataSource == null) {
            this.rawResourceDataSource = new RawResourceDataSource(this.context, this.listener);
        }
        return this.rawResourceDataSource;
    }
}
