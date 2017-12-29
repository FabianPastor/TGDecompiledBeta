package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzcmc extends zzfjm<zzcmc> {
    private static volatile zzcmc[] zzjlk;
    public String name;
    public String zzgcc;
    private Float zzjjk;
    public Double zzjjl;
    public Long zzjll;

    public zzcmc() {
        this.name = null;
        this.zzgcc = null;
        this.zzjll = null;
        this.zzjjk = null;
        this.zzjjl = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzcmc[] zzbbi() {
        if (zzjlk == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjlk == null) {
                    zzjlk = new zzcmc[0];
                }
            }
        }
        return zzjlk;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcmc)) {
            return false;
        }
        zzcmc com_google_android_gms_internal_zzcmc = (zzcmc) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcmc.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcmc.name)) {
            return false;
        }
        if (this.zzgcc == null) {
            if (com_google_android_gms_internal_zzcmc.zzgcc != null) {
                return false;
            }
        } else if (!this.zzgcc.equals(com_google_android_gms_internal_zzcmc.zzgcc)) {
            return false;
        }
        if (this.zzjll == null) {
            if (com_google_android_gms_internal_zzcmc.zzjll != null) {
                return false;
            }
        } else if (!this.zzjll.equals(com_google_android_gms_internal_zzcmc.zzjll)) {
            return false;
        }
        if (this.zzjjk == null) {
            if (com_google_android_gms_internal_zzcmc.zzjjk != null) {
                return false;
            }
        } else if (!this.zzjjk.equals(com_google_android_gms_internal_zzcmc.zzjjk)) {
            return false;
        }
        if (this.zzjjl == null) {
            if (com_google_android_gms_internal_zzcmc.zzjjl != null) {
                return false;
            }
        } else if (!this.zzjjl.equals(com_google_android_gms_internal_zzcmc.zzjjl)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcmc.zzpnc == null || com_google_android_gms_internal_zzcmc.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcmc.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzjjl == null ? 0 : this.zzjjl.hashCode()) + (((this.zzjjk == null ? 0 : this.zzjjk.hashCode()) + (((this.zzjll == null ? 0 : this.zzjll.hashCode()) + (((this.zzgcc == null ? 0 : this.zzgcc.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    this.name = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 18:
                    this.zzgcc = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjll = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 37:
                    this.zzjjk = Float.valueOf(Float.intBitsToFloat(com_google_android_gms_internal_zzfjj.zzcwo()));
                    continue;
                case 41:
                    this.zzjjl = Double.valueOf(Double.longBitsToDouble(com_google_android_gms_internal_zzfjj.zzcwp()));
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
        if (this.name != null) {
            com_google_android_gms_internal_zzfjk.zzn(1, this.name);
        }
        if (this.zzgcc != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.zzgcc);
        }
        if (this.zzjll != null) {
            com_google_android_gms_internal_zzfjk.zzf(3, this.zzjll.longValue());
        }
        if (this.zzjjk != null) {
            com_google_android_gms_internal_zzfjk.zzc(4, this.zzjjk.floatValue());
        }
        if (this.zzjjl != null) {
            com_google_android_gms_internal_zzfjk.zza(5, this.zzjjl.doubleValue());
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.name != null) {
            zzq += zzfjk.zzo(1, this.name);
        }
        if (this.zzgcc != null) {
            zzq += zzfjk.zzo(2, this.zzgcc);
        }
        if (this.zzjll != null) {
            zzq += zzfjk.zzc(3, this.zzjll.longValue());
        }
        if (this.zzjjk != null) {
            this.zzjjk.floatValue();
            zzq += zzfjk.zzlg(4) + 4;
        }
        if (this.zzjjl == null) {
            return zzq;
        }
        this.zzjjl.doubleValue();
        return zzq + (zzfjk.zzlg(5) + 8);
    }
}
