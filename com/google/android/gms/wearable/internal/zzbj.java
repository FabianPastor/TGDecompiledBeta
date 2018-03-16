package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.InputStream;

public final class zzbj extends InputStream {
    private final InputStream zzljl;
    private volatile zzav zzljm;

    public zzbj(InputStream inputStream) {
        this.zzljl = (InputStream) zzbq.checkNotNull(inputStream);
    }

    private final int zzfd(int i) throws ChannelIOException {
        if (i == -1) {
            zzav com_google_android_gms_wearable_internal_zzav = this.zzljm;
            if (com_google_android_gms_wearable_internal_zzav != null) {
                throw new ChannelIOException("Channel closed unexpectedly before stream was finished", com_google_android_gms_wearable_internal_zzav.zzljc, com_google_android_gms_wearable_internal_zzav.zzljd);
            }
        }
        return i;
    }

    public final int available() throws IOException {
        return this.zzljl.available();
    }

    public final void close() throws IOException {
        this.zzljl.close();
    }

    public final void mark(int i) {
        this.zzljl.mark(i);
    }

    public final boolean markSupported() {
        return this.zzljl.markSupported();
    }

    public final int read() throws IOException {
        return zzfd(this.zzljl.read());
    }

    public final int read(byte[] bArr) throws IOException {
        return zzfd(this.zzljl.read(bArr));
    }

    public final int read(byte[] bArr, int i, int i2) throws IOException {
        return zzfd(this.zzljl.read(bArr, i, i2));
    }

    public final void reset() throws IOException {
        this.zzljl.reset();
    }

    public final long skip(long j) throws IOException {
        return this.zzljl.skip(j);
    }

    final void zza(zzav com_google_android_gms_wearable_internal_zzav) {
        this.zzljm = (zzav) zzbq.checkNotNull(com_google_android_gms_wearable_internal_zzav);
    }
}
