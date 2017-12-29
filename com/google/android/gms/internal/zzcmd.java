package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmd extends zzfjm<zzcmd> {
    public zzcme[] zzjlm;

    public zzcmd() {
        this.zzjlm = zzcme.zzbbj();
        this.zzpnc = null;
        this.zzpfd = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcmd)) {
            return false;
        }
        zzcmd com_google_android_gms_internal_zzcmd = (zzcmd) obj;
        return !zzfjq.equals(this.zzjlm, com_google_android_gms_internal_zzcmd.zzjlm) ? false : (this.zzpnc == null || this.zzpnc.isEmpty()) ? com_google_android_gms_internal_zzcmd.zzpnc == null || com_google_android_gms_internal_zzcmd.zzpnc.isEmpty() : this.zzpnc.equals(com_google_android_gms_internal_zzcmd.zzpnc);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + zzfjq.hashCode(this.zzjlm)) * 31;
        int hashCode2 = (this.zzpnc == null || this.zzpnc.isEmpty()) ? 0 : this.zzpnc.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException {
        while (true) {
            int zzcvt = com_google_android_gms_internal_zzfjj.zzcvt();
            switch (zzcvt) {
                case 0:
                    break;
                case 10:
                    int zzb = zzfjv.zzb(com_google_android_gms_internal_zzfjj, 10);
                    zzcvt = this.zzjlm == null ? 0 : this.zzjlm.length;
                    Object obj = new zzcme[(zzb + zzcvt)];
                    if (zzcvt != 0) {
                        System.arraycopy(this.zzjlm, 0, obj, 0, zzcvt);
                    }
                    while (zzcvt < obj.length - 1) {
                        obj[zzcvt] = new zzcme();
                        com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                        com_google_android_gms_internal_zzfjj.zzcvt();
                        zzcvt++;
                    }
                    obj[zzcvt] = new zzcme();
                    com_google_android_gms_internal_zzfjj.zza(obj[zzcvt]);
                    this.zzjlm = obj;
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
        if (this.zzjlm != null && this.zzjlm.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlm) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    com_google_android_gms_internal_zzfjk.zza(1, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        super.zza(com_google_android_gms_internal_zzfjk);
    }

    protected final int zzq() {
        int zzq = super.zzq();
        if (this.zzjlm != null && this.zzjlm.length > 0) {
            for (zzfjs com_google_android_gms_internal_zzfjs : this.zzjlm) {
                if (com_google_android_gms_internal_zzfjs != null) {
                    zzq += zzfjk.zzb(1, com_google_android_gms_internal_zzfjs);
                }
            }
        }
        return zzq;
    }
}
