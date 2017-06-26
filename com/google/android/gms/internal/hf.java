package com.google.android.gms.internal;

import java.io.IOException;

public final class hf extends ada<hf> {
    private static volatile hf[] zzbTI;
    public int type;
    public hg zzbTJ;

    public hf() {
        this.type = 1;
        this.zzbTJ = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static hf[] zzEa() {
        if (zzbTI == null) {
            synchronized (ade.zzcsh) {
                if (zzbTI == null) {
                    zzbTI = new hf[0];
                }
            }
        }
        return zzbTI;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hf)) {
            return false;
        }
        hf hfVar = (hf) obj;
        if (this.type != hfVar.type) {
            return false;
        }
        if (this.zzbTJ == null) {
            if (hfVar.zzbTJ != null) {
                return false;
            }
        } else if (!this.zzbTJ.equals(hfVar.zzbTJ)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? hfVar.zzcrZ == null || hfVar.zzcrZ.isEmpty() : this.zzcrZ.equals(hfVar.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbTJ == null ? 0 : this.zzbTJ.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.type) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    int position = com_google_android_gms_internal_acx.getPosition();
                    int zzLD = com_google_android_gms_internal_acx.zzLD();
                    switch (zzLD) {
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
                            this.type = zzLD;
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(position);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
                case 18:
                    if (this.zzbTJ == null) {
                        this.zzbTJ = new hg();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbTJ);
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
        com_google_android_gms_internal_acy.zzr(1, this.type);
        if (this.zzbTJ != null) {
            com_google_android_gms_internal_acy.zza(2, this.zzbTJ);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn() + acy.zzs(1, this.type);
        return this.zzbTJ != null ? zzn + acy.zzb(2, this.zzbTJ) : zzn;
    }
}
