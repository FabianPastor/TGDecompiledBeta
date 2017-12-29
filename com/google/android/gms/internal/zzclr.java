package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclr extends zzfjm<zzclr> {
    private static volatile zzclr[] zzjjr;
    public Integer zzjjs;
    public zzclv[] zzjjt;
    public zzcls[] zzjju;

    public zzclr() {
        this.zzjjs = null;
        this.zzjjt = zzclv.zzbbd();
        this.zzjju = zzcls.zzbbb();
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public static zzclr[] zzbba() {
        if (zzjjr == null) {
            synchronized (zzfjq.zzpnk) {
                if (zzjjr == null) {
                    zzjjr = new zzclr[0];
                }
            }
        }
        return zzjjr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzclr)) {
            return false;
        }
        zzclr com_google_android_gms_internal_zzclr = (zzclr) obj;
        if (this.zzjjs == null) {
            if (com_google_android_gms_internal_zzclr.zzjjs != null) {
                return false;
            }
        } else if (!this.zzjjs.equals(com_google_android_gms_internal_zzclr.zzjjs)) {
            return false;
        }
        return !zzfjq.equals(this.zzjjt, com_google_android_gms_internal_zzclr.zzjjt) ? false : !zzfjq.equals(this.zzjju, com_google_android_gms_internal_zzclr.zzjju) ? false : (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzclr.zzpnc == null || com_google_android_gms_internal_zzclr.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzclr.zzpnc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((((((this.zzjjs == null ? 0 : this.zzjjs.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31) + zzfjq.hashCode(this.zzjjt)) * 31) + zzfjq.hashCode(this.zzjju)) * 31;
        if (!(this.zzpnc == null || this.zzpnc.isEmpty())) {
            i = this.zzpnc.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            int zzb;
            Object obj;
            switch (zzcvt) {
                case 0:
                    break;
                case 8:
                    this.zzjjs = Integer.valueOf(com_google_android_gms_internal_zzfjj.zzcwi());
                    continue;
                case 18:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 18);
                    zzcvt = this.zzjjt == null ? 0 : this.zzjjt.length;
                    obj = new zzclv[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjjt, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzclv();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzclv();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjjt = obj;
                    continue;
                case 26:
                    zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 26);
                    zzcvt = this.zzjju == null ? 0 : this.zzjju.length;
                    obj = new zzcls[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjju, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcls();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcls();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjju = obj;
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
        int i = 0;
        if (this.zzjjs != null) {
            com_google_android_gms_internal_zzfjk.zzaa(1, this.zzjjs.intValue());
        }
        if (this.zzjjt != null && this.zzjjt.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjjt) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(2, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        if (this.zzjju != null && this.zzjju.length > 0) {
            while (i < this.zzjju.length) {
                zzfjs com_google_android_gms_internal_zzfjs2 = this.zzjju[i];
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    com_google_android_gms_internal_zzfjk.zza(3, com_google_android_gms_internal_zzfjs2);
                }
                i++;
            }
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int i = 0;
        int zzq = super.zzq();
        if (this.zzjjs != null) {
            zzq += zzfjk.zzad(1, this.zzjjs.intValue());
        }
        if (this.zzjjt != null && this.zzjjt.length > 0) {
            int i2 = zzq;
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjjt) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    i2 += zzfjk.zzb(2, com_google_android_gms_internal_zzfjs);
                }
            }
            zzq = i2;
        }
        if (this.zzjju != null && this.zzjju.length > 0) {
            while (i < this.zzjju.length) {
                zzfjs com_google_android_gms_internal_zzfjs2 = this.zzjju[i];
                if (com_google_android_gms_internal_zzfjs2 != null) {
                    zzq += zzfjk.zzb(3, com_google_android_gms_internal_zzfjs2);
                }
                i++;
            }
        }
        return zzq;
    }
}
