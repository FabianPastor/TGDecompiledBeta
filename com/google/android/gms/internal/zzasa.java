package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzasa {
    protected volatile int btP = -1;

    public static final <T extends zzasa> T zza(T t, byte[] bArr) throws zzarz {
        return zzb(t, bArr, 0, bArr.length);
    }

    public static final void zza(zzasa com_google_android_gms_internal_zzasa, byte[] bArr, int i, int i2) {
        try {
            zzart zzc = zzart.zzc(bArr, i, i2);
            com_google_android_gms_internal_zzasa.zza(zzc);
            zzc.cm();
        } catch (Throwable e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static final <T extends zzasa> T zzb(T t, byte[] bArr, int i, int i2) throws zzarz {
        try {
            zzars zzb = zzars.zzb(bArr, i, i2);
            t.zzb(zzb);
            zzb.zzagq(0);
            return t;
        } catch (zzarz e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] zzf(zzasa com_google_android_gms_internal_zzasa) {
        byte[] bArr = new byte[com_google_android_gms_internal_zzasa.cz()];
        zza(com_google_android_gms_internal_zzasa, bArr, 0, bArr.length);
        return bArr;
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return co();
    }

    public zzasa co() throws CloneNotSupportedException {
        return (zzasa) super.clone();
    }

    public int cy() {
        if (this.btP < 0) {
            cz();
        }
        return this.btP;
    }

    public int cz() {
        int zzx = zzx();
        this.btP = zzx;
        return zzx;
    }

    public String toString() {
        return zzasb.zzg(this);
    }

    public void zza(zzart com_google_android_gms_internal_zzart) throws IOException {
    }

    public abstract zzasa zzb(zzars com_google_android_gms_internal_zzars) throws IOException;

    protected int zzx() {
        return 0;
    }
}
