package com.google.android.gms.wearable.internal;

import android.util.Log;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.OutputStream;

public final class zzbl extends OutputStream {
    private volatile zzav zzljm;
    private final OutputStream zzljo;

    public zzbl(OutputStream outputStream) {
        this.zzljo = (OutputStream) zzbq.checkNotNull(outputStream);
    }

    private final IOException zza(IOException iOException) {
        zzav com_google_android_gms_wearable_internal_zzav = this.zzljm;
        if (com_google_android_gms_wearable_internal_zzav == null) {
            return iOException;
        }
        if (Log.isLoggable("ChannelOutputStream", 2)) {
            Log.v("ChannelOutputStream", "Caught IOException, but channel has been closed. Translating to ChannelIOException.", iOException);
        }
        return new ChannelIOException("Channel closed unexpectedly before stream was finished", com_google_android_gms_wearable_internal_zzav.zzljc, com_google_android_gms_wearable_internal_zzav.zzljd);
    }

    public final void close() throws IOException {
        try {
            this.zzljo.close();
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void flush() throws IOException {
        try {
            this.zzljo.flush();
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(int i) throws IOException {
        try {
            this.zzljo.write(i);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(byte[] bArr) throws IOException {
        try {
            this.zzljo.write(bArr);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(byte[] bArr, int i, int i2) throws IOException {
        try {
            this.zzljo.write(bArr, i, i2);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    final void zzc(zzav com_google_android_gms_wearable_internal_zzav) {
        this.zzljm = com_google_android_gms_wearable_internal_zzav;
    }
}
