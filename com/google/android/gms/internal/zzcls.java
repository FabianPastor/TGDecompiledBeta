package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcls extends zzfjm<zzcls> {
    private static volatile zzcls[] zzjjv;
    public Integer zzjjw;
    public String zzjjx;
    public zzclt[] zzjjy;
    private Boolean zzjjz;
    public zzclu zzjka;

    public zzcls() {
        this.zzjjw = null;
        this.zzjjx = null;
        this.zzjjy = zzclt.zzbbc();
        this.zzjjz = null;
        this.zzjka = null;
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzcls[] zzbbb() {
        if (zzjjv == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjjv == null) {
                    zzjjv = new zzcls[0];
                }
            }
        }
        return zzjjv;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcls)) {
            return false;
        }
        zzcls com_google_android_gms_internal_zzcls = (zzcls) obj;
        if (this.zzjjw == null) {
            if (com_google_android_gms_internal_zzcls.zzjjw != null) {
                return false;
            }
        } else if (!this.zzjjw.equals(com_google_android_gms_internal_zzcls.zzjjw)) {
            return false;
        }
        if (this.zzjjx == null) {
            if (com_google_android_gms_internal_zzcls.zzjjx != null) {
                return false;
            }
        } else if (!this.zzjjx.equals(com_google_android_gms_internal_zzcls.zzjjx)) {
            return false;
        }
        if (!zzfjq.equals(this.zzjjy, com_google_android_gms_internal_zzcls.zzjjy)) {
            return false;
        }
        if (this.zzjjz == null) {
            if (com_google_android_gms_internal_zzcls.zzjjz != null) {
                return false;
            }
        } else if (!this.zzjjz.equals(com_google_android_gms_internal_zzcls.zzjjz)) {
            return false;
        }
        if (this.zzjka == null) {
            if (com_google_android_gms_internal_zzcls.zzjka != null) {
                return false;
            }
        } else if (!this.zzjka.equals(com_google_android_gms_internal_zzcls.zzjka)) {
            return false;
        }
        return (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcls.zzpnc == null || com_google_android_gms_internal_zzcls.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcls.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (this.zzjjz == null ? 0 : this.zzjjz.hashCode()) + (((((this.zzjjx == null ? 0 : this.zzjjx.hashCode()) + (((this.zzjjw == null ? 0 : this.zzjjw.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31) + zzfjq.hashCode(this.zzjjy)) * 31);
        zzclu com_google_android_gms_internal_zzclu = this.zzjka;
        hashCode = ((com_google_android_gms_internal_zzclu == null ? 0 : com_google_android_gms_internal_zzclu.hashCode()) + (hashCode * 31)) * 31;
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
                    this.zzjjx = com_google_android_gms_internal_zzfjj.readString();
                    continue;
                case 26:
                    int zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 26);
                    zzcvt = this.zzjjy == null ? 0 : this.zzjjy.length;
                    Object obj = new zzclt[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjjy, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzclt();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzclt();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjjy = obj;
                    continue;
                case 32:
                    this.zzjjz = Boolean.valueOf(com_google_android_gms_internal_zzfjj.zzcvz());
                    continue;
                case 42:
                    if (this.zzjka == null) {
                        this.zzjka = new zzclu();
                    }
                    com_google_android_gms_internal_zzfjj.zza(this.zzjka);
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
        if (this.zzjjx != null) {
            com_google_android_gms_internal_zzfjk.zzn(2, this.zzjjx);
        }
        if (this.zzjjy != null && this.zzjjy.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjjy) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(3, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.zzjjz != null) {
            com_google_android_gms_internal_zzfjk.zzl(4, this.zzjjz.booleanValue());
        }
        if (this.zzjka != null) {
            com_google_android_gms_internal_zzfjk.zza(5, this.zzjka);
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjjw != null) {
            zzq += zzfjk.zzad(1, this.zzjjw.intValue());
        }
        if (this.zzjjx != null) {
            zzq += zzfjk.zzo(2, this.zzjjx);
        }
        if (this.zzjjy != null && this.zzjjy.length > 0) {
            int i = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjjy) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    i += zzfjk.zzb(3, com_google_android_gms_internal_zzfjs);
                }
            }
            zzq = i;
        }
        if (this.zzjjz != null) {
            this.zzjjz.booleanValue();
            zzq += zzfjk.zzlg(4) + 1;
        }
        return this.zzjka != null ? zzq + zzfjk.zzb(5, this.zzjka) : zzq;
    }
}
