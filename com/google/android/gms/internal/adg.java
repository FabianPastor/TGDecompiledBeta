package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adg {
    protected volatile int zzcsi = -1;

    public static final <T extends adg> T zza(T t, byte[] bArr) throws adf {
        return zza(t, bArr, 0, bArr.length);
    }

    private static <T extends adg> T zza(T t, byte[] bArr, int i, int i2) throws adf {
        try {
            acx zzb = acx.zzb(bArr, 0, i2);
            t.zza(zzb);
            zzb.zzcl(0);
            return t;
        } catch (adf e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzc(adg com_google_android_gms_internal_adg) {
        byte[] bArr = new byte[com_google_android_gms_internal_adg.zzLT()];
        try {
            acy zzc = acy.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_adg.zza(zzc);
            zzc.zzLK();
            return bArr;
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLM();
    }

    public String toString() {
        return adh.zzd(this);
    }

    public adg zzLM() throws CloneNotSupportedException {
        return (adg) super.clone();
    }

    public final int zzLS() {
        if (this.zzcsi < 0) {
            zzLT();
        }
        return this.zzcsi;
    }

    public final int zzLT() {
        int zzn = zzn();
        this.zzcsi = zzn;
        return zzn;
    }

    public abstract adg zza(acx com_google_android_gms_internal_acx) throws IOException;

    public void zza(acy com_google_android_gms_internal_acy) throws IOException {
    }

    protected int zzn() {
        return 0;
    }
}
