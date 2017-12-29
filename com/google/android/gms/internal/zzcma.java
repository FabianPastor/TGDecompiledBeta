package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcma extends zzfjm<zzcma> {
    private static volatile zzcma[] zzjlc;
    public Integer zzjjs;
    public zzcmf zzjld;
    public zzcmf zzjle;
    public Boolean zzjlf;

    public zzcma() {
        this.zzjjs = null;
        this.zzjld = null;
        this.zzjle = null;
        this.zzjlf = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzcma[] zzbbg() {
        if (zzjlc == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjlc == null) {
                    zzjlc = new zzcma[0];
                }
            }
        }
        return zzjlc;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcma)) {
            return false;
        }
        zzcma com_google_android_gms_internal_zzcma = (zzcma) obj;
        if (this.zzjjs == null) {
            if (com_google_android_gms_internal_zzcma.zzjjs != null) {
                return false;
            }
        } else if (!this.zzjjs.equals(com_google_android_gms_internal_zzcma.zzjjs)) {
            return false;
        }
        if (this.zzjld == null) {
            if (com_google_android_gms_internal_zzcma.zzjld != null) {
                return false;
            }
        } else if (!this.zzjld.equals(com_google_android_gms_internal_zzcma.zzjld)) {
            return false;
        }
        if (this.zzjle == null) {
            if (com_google_android_gms_internal_zzcma.zzjle != null) {
                return false;
            }
        } else if (!this.zzjle.equals(com_google_android_gms_internal_zzcma.zzjle)) {
            return false;
        }
        if (this.zzjlf == null) {
            if (com_google_android_gms_internal_zzcma.zzjlf != null) {
                return false;
            }
        } else if (!this.zzjlf.equals(com_google_android_gms_internal_zzcma.zzjlf)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcma.zzpnc == null || com_google_android_gms_internal_zzcma.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcma.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (this.zzjjs == null ? 0 : this.zzjjs.hashCode()) + ((getClass().getName().hashCode() + 527) * 31);
        zzcmf com_google_android_gms_internal_zzcmf = this.zzjld;
        hashCode = (com_google_android_gms_internal_zzcmf == null ? 0 : com_google_android_gms_internal_zzcmf.hashCode()) + (hashCode * 31);
        com_google_android_gms_internal_zzcmf = this.zzjle;
        hashCode = ((this.zzjlf == null ? 0 : this.zzjlf.hashCode()) + (((com_google_android_gms_internal_zzcmf == null ? 0 : com_google_android_gms_internal_zzcmf.hashCode()) + (hashCode * 31)) * 31)) * 31;
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
                    this.zzjjs = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 18:
                    if (this.zzjld == null) {
                        this.zzjld = new zzcmf();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjld);
                    continue;
                case 26:
                    if (this.zzjle == null) {
                        this.zzjle = new zzcmf();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjle);
                    continue;
                case 32:
                    this.zzjlf = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
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
        if (this.zzjjs != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjjs.intValue());
        }
        if (this.zzjld != null) {
            com_google_android_gms_internal_zzfjk.zza(2, this.zzjld);
        }
        if (this.zzjle != null) {
            com_google_android_gms_internal_zzfjk.zza(3, this.zzjle);
        }
        if (this.zzjlf != null) {
            com_google_android_gms_internal_zzfjk.zzl(4, this.zzjlf.booleanValue());
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjjs != null) {
            zzq += zzfjk.zzad(1, this.zzjjs.intValue());
        }
        if (this.zzjld != null) {
            zzq += zzfjk.zzb(2, this.zzjld);
        }
        if (this.zzjle != null) {
            zzq += zzfjk.zzb(3, this.zzjle);
        }
        if (this.zzjlf == null) {
            return zzq;
        }
        this.zzjlf.booleanValue();
        return zzq + (zzfjk.zzlg(4) + 1);
    }
}
