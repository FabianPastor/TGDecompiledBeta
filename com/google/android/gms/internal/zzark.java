package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzark {
    protected volatile int bqE = -1;

    public static final <T extends zzark> T zza(T t, byte[] bArr) throws zzarj {
        return zzb(t, bArr, 0, bArr.length);
    }

    public static final void zza(zzark com_google_android_gms_internal_zzark, byte[] bArr, int i, int i2) {
        try {
            zzard zzc = zzard.zzc(bArr, i, i2);
            com_google_android_gms_internal_zzark.zza(zzc);
            zzc.cO();
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static final <T extends zzark> T zzb(T t, byte[] bArr, int i, int i2) throws zzarj {
        try {
            zzarc zzb = zzarc.zzb(bArr, i, i2);
            t.zzb(zzb);
            zzb.zzagz(0);
            return t;
        } catch (zzarj e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzf(zzark com_google_android_gms_internal_zzark) {
        byte[] bArr = new byte[com_google_android_gms_internal_zzark.db()];
        zza(com_google_android_gms_internal_zzark, bArr, 0, bArr.length);
        return bArr;
    }

    public zzark cQ() throws CloneNotSupportedException {
        return (zzark) super.clone();
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return cQ();
    }

    public int da() {
        if (this.bqE < 0) {
            db();
        }
        return this.bqE;
    }

    public int db() {
        int zzx = zzx();
        this.bqE = zzx;
        return zzx;
    }

    public String toString() {
        return zzarl.zzg(this);
    }

    public void zza(zzard com_google_android_gms_internal_zzard) throws IOException {
    }

    public abstract zzark zzb(zzarc com_google_android_gms_internal_zzarc) throws IOException;

    protected int zzx() {
        return 0;
    }
}
