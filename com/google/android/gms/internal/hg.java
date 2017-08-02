package com.google.android.gms.internal;

import java.io.IOException;

public final class hg extends adj<hg> {
    private static volatile hg[] zzbTK;
    public int type;
    public hh zzbTL;

    public hg() {
        this.type = 1;
        this.zzbTL = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static hg[] zzEb() {
        if (zzbTK == null) {
            synchronized (adn.zzcsw) {
                if (zzbTK == null) {
                    zzbTK = new hg[0];
                }
            }
        }
        return zzbTK;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hg)) {
            return false;
        }
        hg hgVar = (hg) obj;
        if (this.type != hgVar.type) {
            return false;
        }
        if (this.zzbTL == null) {
            if (hgVar.zzbTL != null) {
                return false;
            }
        } else if (!this.zzbTL.equals(hgVar.zzbTL)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? hgVar.zzcso == null || hgVar.zzcso.isEmpty() : this.zzcso.equals(hgVar.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbTL == null ? 0 : this.zzbTL.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.type) * 31)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    int position = com_google_android_gms_internal_adg.getPosition();
                    int zzLF = com_google_android_gms_internal_adg.zzLF();
                    switch (zzLF) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                            this.type = zzLF;
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(position);
                            zza(com_google_android_gms_internal_adg, zzLA);
                            continue;
                    }
                case 18:
                    if (this.zzbTL == null) {
                        this.zzbTL = new hh();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbTL);
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
        com_google_android_gms_internal_adh.zzr(1, this.type);
        if (this.zzbTL != null) {
            com_google_android_gms_internal_adh.zza(2, this.zzbTL);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn() + adh.zzs(1, this.type);
        return this.zzbTL != null ? zzn + adh.zzb(2, this.zzbTL) : zzn;
    }
}
