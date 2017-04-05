package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbxn<M extends zzbxn<M>> extends zzbxt {
    protected zzbxp zzcuI;

    private void zza(int i, zzbxv com_google_android_gms_internal_zzbxv) {
        zzbxq com_google_android_gms_internal_zzbxq = null;
        if (this.zzcuI == null) {
            this.zzcuI = new zzbxp();
        } else {
            com_google_android_gms_internal_zzbxq = this.zzcuI.zzro(i);
        }
        if (com_google_android_gms_internal_zzbxq == null) {
            com_google_android_gms_internal_zzbxq = new zzbxq();
            this.zzcuI.zza(i, com_google_android_gms_internal_zzbxq);
        }
        com_google_android_gms_internal_zzbxq.zza(com_google_android_gms_internal_zzbxv);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzaeH();
    }

    public final <T> T zza(zzbxo<M, T> com_google_android_gms_internal_zzbxo_M__T) {
        if (this.zzcuI == null) {
            return null;
        }
        zzbxq zzro = this.zzcuI.zzro(zzbxw.zzrs(com_google_android_gms_internal_zzbxo_M__T.tag));
        return zzro != null ? zzro.zzb(com_google_android_gms_internal_zzbxo_M__T) : null;
    }

    public void zza(zzbxm com_google_android_gms_internal_zzbxm) throws IOException {
        if (this.zzcuI != null) {
            for (int i = 0; i < this.zzcuI.size(); i++) {
                this.zzcuI.zzrp(i).zza(com_google_android_gms_internal_zzbxm);
            }
        }
    }

    protected final boolean zza(zzbxl com_google_android_gms_internal_zzbxl, int i) throws IOException {
        int position = com_google_android_gms_internal_zzbxl.getPosition();
        if (!com_google_android_gms_internal_zzbxl.zzqY(i)) {
            return false;
        }
        zza(zzbxw.zzrs(i), new zzbxv(i, com_google_android_gms_internal_zzbxl.zzI(position, com_google_android_gms_internal_zzbxl.getPosition() - position)));
        return true;
    }

    public M zzaeH() throws CloneNotSupportedException {
        zzbxn com_google_android_gms_internal_zzbxn = (zzbxn) super.zzaeI();
        zzbxr.zza(this, com_google_android_gms_internal_zzbxn);
        return com_google_android_gms_internal_zzbxn;
    }

    public /* synthetic */ zzbxt zzaeI() throws CloneNotSupportedException {
        return (zzbxn) clone();
    }

    protected int zzu() {
        int i = 0;
        if (this.zzcuI == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcuI.size()) {
            i2 += this.zzcuI.zzrp(i).zzu();
            i++;
        }
        return i2;
    }
}
