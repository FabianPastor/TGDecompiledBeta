package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjy extends adj<zzcjy> {
    public zzcjz[] zzbvB;

    public zzcjy() {
        this.zzbvB = zzcjz.zzzD();
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjy)) {
            return false;
        }
        zzcjy com_google_android_gms_internal_zzcjy = (zzcjy) obj;
        return !adn.equals(this.zzbvB, com_google_android_gms_internal_zzcjy.zzbvB) ? false : (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_zzcjy.zzcsd == null || com_google_android_gms_internal_zzcjy.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_zzcjy.zzcsd);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbvB)) * 31;
        int hashCode2 = (this.zzcsd == null || this.zzcsd.isEmpty()) ? 0 : this.zzcsd.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLB = this.zzbvB == null ? 0 : this.zzbvB.length;
                    Object obj = new zzcjz[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzbvB, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new zzcjz();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new zzcjz();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzbvB = obj;
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (this.zzbvB != null && this.zzbvB.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvB) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(1, com_google_android_gms_internal_adp);
                }
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbvB != null && this.zzbvB.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvB) {
                if (com_google_android_gms_internal_adp != null) {
                    zzn += adh.zzb(1, com_google_android_gms_internal_adp);
                }
            }
        }
        return zzn;
    }
}
