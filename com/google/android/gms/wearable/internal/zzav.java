package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.InputStream;

public final class zzav extends InputStream {
    private final InputStream zzbSo;
    private volatile zzah zzbSp;

    public zzav(InputStream inputStream) {
        this.zzbSo = (InputStream) zzbo.zzu(inputStream);
    }

    private final int zzbR(int i) throws ChannelIOException {
        if (i == -1) {
            zzah com_google_android_gms_wearable_internal_zzah = this.zzbSp;
            if (com_google_android_gms_wearable_internal_zzah != null) {
                throw new ChannelIOException("Channel closed unexpectedly before stream was finished", com_google_android_gms_wearable_internal_zzah.zzbSf, com_google_android_gms_wearable_internal_zzah.zzbSg);
            }
        }
        return i;
    }

    public final int available() throws IOException {
        return this.zzbSo.available();
    }

    public final void close() throws IOException {
        this.zzbSo.close();
    }

    public final void mark(int i) {
        this.zzbSo.mark(i);
    }

    public final boolean markSupported() {
        return this.zzbSo.markSupported();
    }

    public final int read() throws IOException {
        return zzbR(this.zzbSo.read());
    }

    public final int read(byte[] bArr) throws IOException {
        return zzbR(this.zzbSo.read(bArr));
    }

    public final int read(byte[] bArr, int i, int i2) throws IOException {
        return zzbR(this.zzbSo.read(bArr, i, i2));
    }

    public final void reset() throws IOException {
        this.zzbSo.reset();
    }

    public final long skip(long j) throws IOException {
        return this.zzbSo.skip(j);
    }

    final void zza(zzah com_google_android_gms_wearable_internal_zzah) {
        this.zzbSp = (zzah) zzbo.zzu(com_google_android_gms_wearable_internal_zzah);
    }
}
