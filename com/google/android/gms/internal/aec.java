package com.google.android.gms.internal;

import java.io.IOException;

public final class aec extends ada<aec> implements Cloneable {
    private int zzctV;
    private int zzctW;

    public aec() {
        this.zzctV = -1;
        this.zzctW = 0;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    private aec zzMe() {
        try {
            return (aec) super.zzLL();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMe();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aec)) {
            return false;
        }
        aec com_google_android_gms_internal_aec = (aec) obj;
        return this.zzctV != com_google_android_gms_internal_aec.zzctV ? false : this.zzctW != com_google_android_gms_internal_aec.zzctW ? false : (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_aec.zzcrZ == null || com_google_android_gms_internal_aec.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_aec.zzcrZ);
    }

    public final int hashCode() {
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + this.zzctV) * 31) + this.zzctW) * 31;
        int hashCode2 = (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? 0 : this.zzcrZ.hashCode();
        return hashCode2 + hashCode;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (aec) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (aec) clone();
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            int position;
            int zzLA;
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    position = com_google_android_gms_internal_acx.getPosition();
                    zzLA = com_google_android_gms_internal_acx.zzLA();
                    switch (zzLA) {
                        case -1:
                        case 0:
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
                        case 16:
                        case 17:
                            this.zzctV = zzLA;
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(position);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
                case 16:
                    position = com_google_android_gms_internal_acx.getPosition();
                    zzLA = com_google_android_gms_internal_acx.zzLA();
                    switch (zzLA) {
                        case 0:
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
                        case 16:
                        case 100:
                            this.zzctW = zzLA;
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(position);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
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
        if (this.zzctV != -1) {
            com_google_android_gms_internal_acy.zzr(1, this.zzctV);
        }
        if (this.zzctW != 0) {
            com_google_android_gms_internal_acy.zzr(2, this.zzctW);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzctV != -1) {
            zzn += acy.zzs(1, this.zzctV);
        }
        return this.zzctW != 0 ? zzn + acy.zzs(2, this.zzctW) : zzn;
    }
}
