package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclv extends zzfjm<zzclv> {
    private static volatile zzclv[] zzjkl;
    public Integer zzjjw;
    public String zzjkm;
    public zzclt zzjkn;

    public zzclv() {
        this.zzjjw = null;
        this.zzjkm = null;
        this.zzjkn = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzclv[] zzbbd() {
        if (zzjkl == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjkl == null) {
                    zzjkl = new zzclv[0];
                }
            }
        }
        return zzjkl;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzclv)) {
            return false;
        }
        zzclv com_google_android_gms_internal_zzclv = (zzclv) obj;
        if (this.zzjjw == null) {
            if (com_google_android_gms_internal_zzclv.zzjjw != null) {
                return false;
            }
        } else if (!this.zzjjw.equals(com_google_android_gms_internal_zzclv.zzjjw)) {
            return false;
        }
        if (this.zzjkm == null) {
            if (com_google_android_gms_internal_zzclv.zzjkm != null) {
                return false;
            }
        } else if (!this.zzjkm.equals(com_google_android_gms_internal_zzclv.zzjkm)) {
            return false;
        }
        if (this.zzjkn == null) {
            if (com_google_android_gms_internal_zzclv.zzjkn != null) {
                return false;
            }
        } else if (!this.zzjkn.equals(com_google_android_gms_internal_zzclv.zzjkn)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclv.zzpnc == null || com_google_android_gms_internal_zzclv.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclv.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (this.zzjkm == null ? 0 : this.zzjkm.hashCode()) + (((this.zzjjw == null ? 0 : this.zzjjw.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31);
        zzclt com_google_android_gms_internal_zzclt = this.zzjkn;
        hashCode = ((com_google_android_gms_internal_zzclt == null ? 0 : com_google_android_gms_internal_zzclt.hashCode()) + (hashCode * 31)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    this.zzjjw = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 18:
                    this.zzjkm = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 26:
                    if (this.zzjkn == null) {
                        this.zzjkn = new zzclt();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjkn);
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_zzfjj, zzcvt)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
        if (this.zzjjw != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjjw.intValue());
        }
        if (this.zzjkm != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.zzjkm);
        }
        if (this.zzjkn != null) {
            com_google_android_gms_internal_zzfjk.zza(3, this.zzjkn);
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjjw != null) {
            zzq += zzfjk.zzad(1, this.zzjjw.intValue());
        }
        if (this.zzjkm != null) {
            zzq += zzfjk.zzo(2, this.zzjkm);
        }
        return this.zzjkn != null ? zzq + zzfjk.zzb(3, this.zzjkn) : zzq;
    }
}
