package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbxn<M extends zzbxn<M>> extends zzbxt {
    protected zzbxp zzcuA;

    private void zza(int i, zzbxv com_google_android_gms_internal_zzbxv) {
        zzbxq com_google_android_gms_internal_zzbxq = null;
        if (this.zzcuA == null) {
            this.zzcuA = new zzbxp();
        } else {
            com_google_android_gms_internal_zzbxq = this.zzcuA.zzrn(i);
        }
        if (com_google_android_gms_internal_zzbxq == null) {
            com_google_android_gms_internal_zzbxq = new zzbxq();
            this.zzcuA.zza(i, com_google_android_gms_internal_zzbxq);
        }
        com_google_android_gms_internal_zzbxq.zza(com_google_android_gms_internal_zzbxv);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzaeG();
    }

    public final <T> T zza(zzbxo<M, T> com_google_android_gms_internal_zzbxo_M__T) {
        if (this.zzcuA == null) {
            return null;
        }
        zzbxq zzrn = this.zzcuA.zzrn(zzbxw.zzrr(com_google_android_gms_internal_zzbxo_M__T.tag));
        return zzrn != null ? zzrn.zzb(com_google_android_gms_internal_zzbxo_M__T) : null;
    }

    public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        if (this.zzcuA != null) {
            for (int i = 0; i < this.zzcuA.size(); i++) {
                this.zzcuA.zzro(i).zza(com_google_android_gms_internal_zzbxm);
            }
        }
    }

    protected final boolean zza(zzbxl com_google_android_gms_internal_zzbxl, int i) throws IOException {
        int position = com_google_android_gms_internal_zzbxl.getPosition();
        if (!com_google_android_gms_internal_zzbxl.zzqX(i)) {
            return false;
        }
        zza(zzbxw.zzrr(i), new zzbxv(i, com_google_android_gms_internal_zzbxl.zzI(position, com_google_android_gms_internal_zzbxl.getPosition() - position)));
        return true;
    }

    public M zzaeG() throws CloneNotSupportedException {
        zzbxn com_google_android_gms_internal_zzbxn = (zzbxn) super.zzaeH();
        zzbxr.zza(this, com_google_android_gms_internal_zzbxn);
        return com_google_android_gms_internal_zzbxn;
    }

    public /* synthetic */ zzbxt zzaeH() throws CloneNotSupportedException {
        return (zzbxn) clone();
    }

    protected int zzu() {
        int i = 0;
        if (this.zzcuA == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcuA.size()) {
            i2 += this.zzcuA.zzro(i).zzu();
            i++;
        }
        return i2;
    }
}
