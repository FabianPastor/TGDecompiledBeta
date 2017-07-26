package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adp {
    protected volatile int zzcsm = -1;

    public static final <T extends adp> T zza(T t, byte[] bArr) throws ado {
        return zza(t, bArr, 0, bArr.length);
    }

    private static <T extends adp> T zza(T t, byte[] bArr, int i, int i2) throws ado {
        try {
            adg zzb = adg.zzb(bArr, 0, i2);
            t.zza(zzb);
            zzb.zzcl(0);
            return t;
        } catch (ado e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzc(adp com_google_android_gms_internal_adp) {
        byte[] bArr = new byte[com_google_android_gms_internal_adp.zzLW()];
        try {
            adh zzc = adh.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_adp.zza(zzc);
            zzc.zzLN();
            return bArr;
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLP();
    }

    public String toString() {
        return adq.zzd(this);
    }

    public adp zzLP() throws CloneNotSupportedException {
        return (adp) super.clone();
    }

    public final int zzLV() {
        if (this.zzcsm < 0) {
            zzLW();
        }
        return this.zzcsm;
    }

    public final int zzLW() {
        int zzn = zzn();
        this.zzcsm = zzn;
        return zzn;
    }

    public abstract adp zza(adg com_google_android_gms_internal_adg) throws IOException;

    public void zza(adh com_google_android_gms_internal_adh) throws IOException {
    }

    protected int zzn() {
        return 0;
    }
}
