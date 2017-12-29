package com.google.android.gms.internal;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;

public final class zzcmb extends zzfjm<zzcmb> {
    private static volatile zzcmb[] zzjlg;
    public Integer count;
    public String name;
    public zzcmc[] zzjlh;
    public Long zzjli;
    public Long zzjlj;

    public zzcmb() {
        this.zzjlh = zzcmc.zzbbi();
        this.name = null;
        this.zzjli = null;
        this.zzjlj = null;
        this.count = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzcmb[] zzbbh() {
        if (zzjlg == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjlg == null) {
                    zzjlg = new zzcmb[0];
                }
            }
        }
        return zzjlg;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcmb)) {
            return false;
        }
        zzcmb com_google_android_gms_internal_zzcmb = (zzcmb) obj;
        if (!zzfjq.equals(this.zzjlh, com_google_android_gms_internal_zzcmb.zzjlh)) {
            return false;
        }
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcmb.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcmb.name)) {
            return false;
        }
        if (this.zzjli == null) {
            if (com_google_android_gms_internal_zzcmb.zzjli != null) {
                return false;
            }
        } else if (!this.zzjli.equals(com_google_android_gms_internal_zzcmb.zzjli)) {
            return false;
        }
        if (this.zzjlj == null) {
            if (com_google_android_gms_internal_zzcmb.zzjlj != null) {
                return false;
            }
        } else if (!this.zzjlj.equals(com_google_android_gms_internal_zzcmb.zzjlj)) {
            return false;
        }
        if (this.count == null) {
            if (com_google_android_gms_internal_zzcmb.count != null) {
                return false;
            }
        } else if (!this.count.equals(com_google_android_gms_internal_zzcmb.count)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcmb.zzpnc == null || com_google_android_gms_internal_zzcmb.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcmb.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzjlj == null ? 0 : this.zzjlj.hashCode()) + (((this.zzjli == null ? 0 : this.zzjli.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + zzfjq.hashCode(this.zzjlh)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    int zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 10);
                    zzcvt = this.zzjlh == null ? 0 : this.zzjlh.length;
                    Object obj = new zzcmc[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjlh, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcmc();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcmc();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjlh = obj;
                    continue;
                case 18:
                    this.name = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                    this.zzjli = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 32:
                    this.zzjlj = Long.valueOf(com_google_android_gms_internal_zzfjj.zzcwn());
                    continue;
                case 40:
                    this.count = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
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
        if (this.zzjlh != null && this.zzjlh.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlh) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(1, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.name != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.name);
        }
        if (this.zzjli != null) {
            com_google_android_gms_internal_zzfjk.zzf(3, this.zzjli.longValue());
        }
        if (this.zzjlj != null) {
            com_google_android_gms_internal_zzfjk.zzf(4, this.zzjlj.longValue());
        }
        if (this.count != null) {
            com_google_android_gms_internal_zzfjk.zzaa(5, this.count.intValue());
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjlh != null && this.zzjlh.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlh) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    zzq += zzfjk.zzb(1, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.name != null) {
            zzq += zzfjk.zzo(2, this.name);
        }
        if (this.zzjli != null) {
            zzq += zzfjk.zzc(3, this.zzjli.longValue());
        }
        if (this.zzjlj != null) {
            zzq += zzfjk.zzc(4, this.zzjlj.longValue());
        }
        return this.count != null ? zzq + zzfjk.zzad(5, this.count.intValue()) : zzq;
    }
}
