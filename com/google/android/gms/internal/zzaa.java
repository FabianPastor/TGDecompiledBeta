package com.google.android.gms.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class zzaa extends ByteArrayOutputStream {
    private final zzu zzbq;

    public zzaa(zzu com_google_android_gms_internal_zzu, int i) {
        this.zzbq = com_google_android_gms_internal_zzu;
        this.buf = this.zzbq.zzb(Math.max(i, 256));
    }

    private void zzd(int i) {
        if (this.count + i > this.buf.length) {
            Object zzb = this.zzbq.zzb((this.count + i) * 2);
            System.arraycopy(this.buf, 0, zzb, 0, this.count);
            this.zzbq.zza(this.buf);
            this.buf = zzb;
        }
    }

    public void close() throws IOException {
        this.zzbq.zza(this.buf);
        this.buf = null;
        super.close();
    }

    public void finalize() {
        this.zzbq.zza(this.buf);
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
