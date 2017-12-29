package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzclt extends zzfjm<zzclt> {
    private static volatile zzclt[] zzjkb;
    public zzclw zzjkc;
    public zzclu zzjkd;
    public Boolean zzjke;
    public String zzjkf;

    public zzclt() {
        this.zzjkc = null;
        this.zzjkd = null;
        this.zzjke = null;
        this.zzjkf = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzclt[] zzbbc() {
        if (zzjkb == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjkb == null) {
                    zzjkb = new zzclt[0];
                }
            }
        }
        return zzjkb;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzclt)) {
            return false;
        }
        zzclt com_google_android_gms_internal_zzclt = (zzclt) obj;
        if (this.zzjkc == null) {
            if (com_google_android_gms_internal_zzclt.zzjkc != null) {
                return false;
            }
        } else if (!this.zzjkc.equals(com_google_android_gms_internal_zzclt.zzjkc)) {
            return false;
        }
        if (this.zzjkd == null) {
            if (com_google_android_gms_internal_zzclt.zzjkd != null) {
                return false;
            }
        } else if (!this.zzjkd.equals(com_google_android_gms_internal_zzclt.zzjkd)) {
            return false;
        }
        if (this.zzjke == null) {
            if (com_google_android_gms_internal_zzclt.zzjke != null) {
                return false;
            }
        } else if (!this.zzjke.equals(com_google_android_gms_internal_zzclt.zzjke)) {
            return false;
        }
        if (this.zzjkf == null) {
            if (com_google_android_gms_internal_zzclt.zzjkf != null) {
                return false;
            }
        } else if (!this.zzjkf.equals(com_google_android_gms_internal_zzclt.zzjkf)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclt.zzpnc == null || com_google_android_gms_internal_zzclt.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclt.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = getClass().getName().hashCode() + 527;
        zzclw com_google_android_gms_internal_zzclw = this.zzjkc;
        hashCode = (com_google_android_gms_internal_zzclw == null ? 0 : com_google_android_gms_internal_zzclw.hashCode()) + (hashCode * 31);
        zzclu com_google_android_gms_internal_zzclu = this.zzjkd;
        hashCode = ((this.zzjkf == null ? 0 : this.zzjkf.hashCode()) + (((this.zzjke == null ? 0 : this.zzjke.hashCode()) + (((com_google_android_gms_internal_zzclu == null ? 0 : com_google_android_gms_internal_zzclu.hashCode()) + (hashCode * 31)) * 31)) * 31)) * 31;
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
                case 10:
                    if (this.zzjkc == null) {
                        this.zzjkc = new zzclw();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjkc);
                    continue;
                case 18:
                    if (this.zzjkd == null) {
                        this.zzjkd = new zzclu();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjkd);
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjke = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 34:
                    this.zzjkf = com_google_android_gms_internal_zzfjj.readString();
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
        if (this.zzjkc != null) {
            com_google_android_gms_internal_zzfjk.zza(1, this.zzjkc);
        }
        if (this.zzjkd != null) {
            com_google_android_gms_internal_zzfjk.zza(2, this.zzjkd);
        }
        if (this.zzjke != null) {
            com_google_android_gms_internal_zzfjk.zzl(3, this.zzjke.booleanValue());
        }
        if (this.zzjkf != null) {
            com_google_android_gms_internal_zzfjk.zzn(4, this.zzjkf);
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjkc != null) {
            zzq += zzfjk.zzb(1, this.zzjkc);
        }
        if (this.zzjkd != null) {
            zzq += zzfjk.zzb(2, this.zzjkd);
        }
        if (this.zzjke != null) {
            this.zzjke.booleanValue();
            zzq += zzfjk.zzlg(3) + 1;
        }
        return this.zzjkf != null ? zzq + zzfjk.zzo(4, this.zzjkf) : zzq;
    }
}
