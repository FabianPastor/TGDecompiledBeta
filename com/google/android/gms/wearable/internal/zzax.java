package com.google.android.gms.wearable.internal;

import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.OutputStream;

public final class zzax extends OutputStream {
    private volatile zzah zzbSp;
    private final OutputStream zzbSr;

    public zzax(OutputStream outputStream) {
        this.zzbSr = (OutputStream) zzbo.zzu(outputStream);
    }

    private final IOException zza(IOException iOException) {
        zzah com_google_android_gms_wearable_internal_zzah = this.zzbSp;
        if (com_google_android_gms_wearable_internal_zzah == null) {
            return iOException;
        }
        if (Log.isLoggable("ChannelOutputStream", 2)) {
            Log.v("ChannelOutputStream", "Caught IOException, but channel has been closed. Translating to ChannelIOException.", iOException);
        }
        return new ChannelIOException("Channel closed unexpectedly before stream was finished", com_google_android_gms_wearable_internal_zzah.zzbSf, com_google_android_gms_wearable_internal_zzah.zzbSg);
    }

    public final void close() throws IOException {
        try {
            this.zzbSr.close();
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void flush() throws IOException {
        try {
            this.zzbSr.flush();
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(int i) throws IOException {
        try {
            this.zzbSr.write(i);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(byte[] bArr) throws IOException {
        try {
            this.zzbSr.write(bArr);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    public final void write(byte[] bArr, int i, int i2) throws IOException {
        try {
            this.zzbSr.write(bArr, i, i2);
        } catch (IOException e) {
            throw zza(e);
        }
    }

    final void zzc(zzah com_google_android_gms_wearable_internal_zzah) {
        this.zzbSp = com_google_android_gms_wearable_internal_zzah;
    }
}
