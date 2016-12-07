package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbut {
    protected volatile int zzcsg = -1;

    public static final <T extends zzbut> T zza(T t, byte[] bArr) throws zzbus {
        return zzb(t, bArr, 0, bArr.length);
    }

    public static final void zza(zzbut com_google_android_gms_internal_zzbut, byte[] bArr, int i, int i2) {
        try {
            zzbum zzc = zzbum.zzc(bArr, i, i2);
            com_google_android_gms_internal_zzbut.zza(zzc);
            zzc.zzacM();
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static final <T extends zzbut> T zzb(T t, byte[] bArr, int i, int i2) throws zzbus {
        try {
            zzbul zzb = zzbul.zzb(bArr, i, i2);
            t.zzb(zzb);
            zzb.zzqg(0);
            return t;
        } catch (zzbus e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzf(zzbut com_google_android_gms_internal_zzbut) {
        byte[] bArr = new byte[com_google_android_gms_internal_zzbut.zzacZ()];
        zza(com_google_android_gms_internal_zzbut, bArr, 0, bArr.length);
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzacO();
    }

    public String toString() {
        return zzbuu.zzg(this);
    }

    public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
    }

    public zzbut zzacO() throws CloneNotSupportedException {
        return (zzbut) super.clone();
    }

    public int zzacY() {
        if (this.zzcsg < 0) {
            zzacZ();
        }
        return this.zzcsg;
    }

    public int zzacZ() {
        int zzv = zzv();
        this.zzcsg = zzv;
        return zzv;
    }

    public abstract zzbut zzb(zzbul com_google_android_gms_internal_zzbul) throws IOException;

    protected int zzv() {
        return 0;
    }
}
