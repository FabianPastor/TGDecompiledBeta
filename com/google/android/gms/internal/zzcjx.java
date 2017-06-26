package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjx extends ada<zzcjx> {
    public zzcjy[] zzbvB;

    public zzcjx() {
        this.zzbvB = zzcjy.zzzD();
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjx)) {
            return false;
        }
        zzcjx com_google_android_gms_internal_zzcjx = (zzcjx) obj;
        return !ade.equals(this.zzbvB, com_google_android_gms_internal_zzcjx.zzbvB) ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjx.zzcrZ == null || com_google_android_gms_internal_zzcjx.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjx.zzcrZ);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + ade.hashCode(this.zzbvB)) * 31;
        int hashCode2 = (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? 0 : this.zzcrZ.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 10);
                    zzLy = this.zzbvB == null ? 0 : this.zzbvB.length;
                    Object obj = new zzcjy[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvB, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjy();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjy();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvB = obj;
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_acx, zzLy)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(acy com_google_android_gms_internal_acy) throws IOException {
        if (this.zzbvB != null && this.zzbvB.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvB) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(1, com_google_android_gms_internal_adg);
                }
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbvB != null && this.zzbvB.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvB) {
                if (com_google_android_gms_internal_adg != null) {
                    zzn += acy.zzb(1, com_google_android_gms_internal_adg);
                }
            }
        }
        return zzn;
    }
}
