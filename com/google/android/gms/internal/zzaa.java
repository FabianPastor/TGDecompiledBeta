package com.google.android.gms.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class zzaa extends ByteArrayOutputStream {
    private final zzu zzap;

    public zzaa(zzu com_google_android_gms_internal_zzu, int i) {
        this.zzap = com_google_android_gms_internal_zzu;
        this.buf = this.zzap.zzb(Math.max(i, 256));
    }

    private void zzd(int i) {
        if (this.count + i > this.buf.length) {
            Object zzb = this.zzap.zzb((this.count + i) * 2);
            System.arraycopy(this.buf, 0, zzb, 0, this.count);
            this.zzap.zza(this.buf);
            this.buf = zzb;
        }
    }

    public void close() throws IOException {
        this.zzap.zza(this.buf);
        this.buf = null;
        super.close();
    }

    public void finalize() {
        this.zzap.zza(this.buf);
    }

    public synchronized void write(int i) {
        zzd(1);
        super.write(i);
    }

    public synchronized void write(byte[] bArr, int i, int i2) {
        zzd(i2);
        super.write(bArr, i, i2);
    }
}
