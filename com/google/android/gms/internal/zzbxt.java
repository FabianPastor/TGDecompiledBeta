package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbxt {
    protected volatile int zzcuR = -1;

    public static final <T extends zzbxt> T zza(T t, byte[] bArr) throws zzbxs {
        return zzb(t, bArr, 0, bArr.length);
    }

    public static final void zza(zzbxt com_google_android_gms_internal_zzbxt, byte[] bArr, int i, int i2) {
        try {
            zzbxm zzc = zzbxm.zzc(bArr, i, i2);
            com_google_android_gms_internal_zzbxt.zza(zzc);
            zzc.zzaeG();
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static final <T extends zzbxt> T zzb(T t, byte[] bArr, int i, int i2) throws zzbxs {
        try {
            zzbxl zzb = zzbxl.zzb(bArr, i, i2);
            t.zzb(zzb);
            zzb.zzqX(0);
            return t;
        } catch (zzbxs e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzf(zzbxt com_google_android_gms_internal_zzbxt) {
        byte[] bArr = new byte[com_google_android_gms_internal_zzbxt.zzaeT()];
        zza(com_google_android_gms_internal_zzbxt, bArr, 0, bArr.length);
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzaeI();
    }

    public String toString() {
        return zzbxu.zzg(this);
    }

    public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
    }

    public zzbxt zzaeI() throws CloneNotSupportedException {
        return (zzbxt) super.clone();
    }

    public int zzaeS() {
        if (this.zzcuR < 0) {
            zzaeT();
        }
        return this.zzcuR;
    }

    public int zzaeT() {
        int zzu = zzu();
        this.zzcuR = zzu;
        return zzu;
    }

    public abstract zzbxt zzb(zzbxl com_google_android_gms_internal_zzbxl) throws IOException;

    protected int zzu() {
        return 0;
    }
}
