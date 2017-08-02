package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjy extends adj<zzcjy> {
    public zzcjz[] zzbvB;

    public zzcjy() {
        this.zzbvB = zzcjz.zzzD();
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjy)) {
            return false;
        }
        zzcjy com_google_android_gms_internal_zzcjy = (zzcjy) obj;
        return !adn.equals(this.zzbvB, com_google_android_gms_internal_zzcjy.zzbvB) ? false : (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjy.zzcso == null || com_google_android_gms_internal_zzcjy.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjy.zzcso);
    }

    public final int hashCode() {
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbvB)) * 31;
        int hashCode2 = (this.zzcso == null || this.zzcso.isEmpty()) ? 0 : this.zzcso.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 10:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLA = this.zzbvB == null ? 0 : this.zzbvB.length;
                    Object obj = new zzcjz[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvB, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjz();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjz();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbvB = obj;
                    continue;
                default:
                    if (!super.zza(com_google_android_gms_internal_adg, zzLA)) {
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
