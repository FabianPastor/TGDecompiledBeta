package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbun<M extends zzbun<M>> extends zzbut {
    protected zzbup zzcrX;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzacN();
    }

    public final <T> T zza(zzbuo<M, T> com_google_android_gms_internal_zzbuo_M__T) {
        if (this.zzcrX == null) {
            return null;
        }
        zzbuq zzqx = this.zzcrX.zzqx(zzbuw.zzqB(com_google_android_gms_internal_zzbuo_M__T.tag));
        return zzqx != null ? zzqx.zzb(com_google_android_gms_internal_zzbuo_M__T) : null;
    }

    public void zza(zzbum com_google_android_gms_internal_zzbum) throws IOException {
        if (this.zzcrX != null) {
            for (int i = 0; i < this.zzcrX.size(); i++) {
                this.zzcrX.zzqy(i).zza(com_google_android_gms_internal_zzbum);
            }
        }
    }

    protected final boolean zza(zzbul com_google_android_gms_internal_zzbul, int i) throws IOException {
        int position = com_google_android_gms_internal_zzbul.getPosition();
        if (!com_google_android_gms_internal_zzbul.zzqh(i)) {
            return false;
        }
        int zzqB = zzbuw.zzqB(i);
        zzbuv com_google_android_gms_internal_zzbuv = new zzbuv(i, com_google_android_gms_internal_zzbul.zzE(position, com_google_android_gms_internal_zzbul.getPosition() - position));
        zzbuq com_google_android_gms_internal_zzbuq = null;
        if (this.zzcrX == null) {
            this.zzcrX = new zzbup();
        } else {
            com_google_android_gms_internal_zzbuq = this.zzcrX.zzqx(zzqB);
        }
        if (com_google_android_gms_internal_zzbuq == null) {
            com_google_android_gms_internal_zzbuq = new zzbuq();
            this.zzcrX.zza(zzqB, com_google_android_gms_internal_zzbuq);
        }
        com_google_android_gms_internal_zzbuq.zza(com_google_android_gms_internal_zzbuv);
        return true;
    }

    public M zzacN() throws CloneNotSupportedException {
        zzbun com_google_android_gms_internal_zzbun = (zzbun) super.zzacO();
        zzbur.zza(this, com_google_android_gms_internal_zzbun);
        return com_google_android_gms_internal_zzbun;
    }

    public /* synthetic */ zzbut zzacO() throws CloneNotSupportedException {
        return (zzbun) clone();
    }

    protected int zzv() {
        int i = 0;
        if (this.zzcrX == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcrX.size()) {
            i2 += this.zzcrX.zzqy(i).zzv();
            i++;
        }
        return i2;
    }
}
