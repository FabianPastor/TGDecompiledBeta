package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjo extends ada<zzcjo> {
    public Integer zzbuW;
    public Boolean zzbuX;
    public String zzbuY;
    public String zzbuZ;
    public String zzbva;

    public zzcjo() {
        this.zzbuW = null;
        this.zzbuX = null;
        this.zzbuY = null;
        this.zzbuZ = null;
        this.zzbva = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjo)) {
            return false;
        }
        zzcjo com_google_android_gms_internal_zzcjo = (zzcjo) obj;
        if (this.zzbuW == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuW != null) {
                return false;
            }
        } else if (!this.zzbuW.equals(com_google_android_gms_internal_zzcjo.zzbuW)) {
            return false;
        }
        if (this.zzbuX == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuX != null) {
                return false;
            }
        } else if (!this.zzbuX.equals(com_google_android_gms_internal_zzcjo.zzbuX)) {
            return false;
        }
        if (this.zzbuY == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuY != null) {
                return false;
            }
        } else if (!this.zzbuY.equals(com_google_android_gms_internal_zzcjo.zzbuY)) {
            return false;
        }
        if (this.zzbuZ == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuZ != null) {
                return false;
            }
        } else if (!this.zzbuZ.equals(com_google_android_gms_internal_zzcjo.zzbuZ)) {
            return false;
        }
        if (this.zzbva == null) {
            if (com_google_android_gms_internal_zzcjo.zzbva != null) {
                return false;
            }
        } else if (!this.zzbva.equals(com_google_android_gms_internal_zzcjo.zzbva)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjo.zzcrZ == null || com_google_android_gms_internal_zzcjo.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjo.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbva == null ? 0 : this.zzbva.hashCode()) + (((this.zzbuZ == null ? 0 : this.zzbuZ.hashCode()) + (((this.zzbuY == null ? 0 : this.zzbuY.hashCode()) + (((this.zzbuX == null ? 0 : this.zzbuX.hashCode()) + (((this.zzbuW == null ? 0 : this.zzbuW.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            this.zzbuW = Integer.valueOf(zzLD);
                            break;
                        default:
                            com_google_android_gms_internal_acx.zzcp(position);
                            zza(com_google_android_gms_internal_acx, zzLy);
                            continue;
                    }
                case 16:
                    this.zzbuX = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.zzbuY = com_google_android_gms_internal_acx.readString();
                    continue;
                case 34:
                    this.zzbuZ = com_google_android_gms_internal_acx.readString();
                    continue;
                case 42:
                    this.zzbva = com_google_android_gms_internal_acx.readString();
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
        if (this.zzbuW != null) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            com_google_android_gms_internal_acy.zzk(2, this.zzbuX.booleanValue());
        }
        if (this.zzbuY != null) {
            com_google_android_gms_internal_acy.zzl(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            com_google_android_gms_internal_acy.zzl(4, this.zzbuZ);
        }
        if (this.zzbva != null) {
            com_google_android_gms_internal_acy.zzl(5, this.zzbva);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuW != null) {
            zzn += acy.zzs(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            this.zzbuX.booleanValue();
            zzn += acy.zzct(2) + 1;
        }
        if (this.zzbuY != null) {
            zzn += acy.zzm(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            zzn += acy.zzm(4, this.zzbuZ);
        }
        return this.zzbva != null ? zzn + acy.zzm(5, this.zzbva) : zzn;
    }
}
