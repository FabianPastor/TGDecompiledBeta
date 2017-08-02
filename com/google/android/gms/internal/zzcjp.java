package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class zzcjp extends adj<zzcjp> {
    public Integer zzbuW;
    public Boolean zzbuX;
    public String zzbuY;
    public String zzbuZ;
    public String zzbva;

    public zzcjp() {
        this.zzbuW = null;
        this.zzbuX = null;
        this.zzbuY = null;
        this.zzbuZ = null;
        this.zzbva = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjp)) {
            return false;
        }
        zzcjp com_google_android_gms_internal_zzcjp = (zzcjp) obj;
        if (this.zzbuW == null) {
            if (com_google_android_gms_internal_zzcjp.zzbuW != null) {
                return false;
            }
        } else if (!this.zzbuW.equals(com_google_android_gms_internal_zzcjp.zzbuW)) {
            return false;
        }
        if (this.zzbuX == null) {
            if (com_google_android_gms_internal_zzcjp.zzbuX != null) {
                return false;
            }
        } else if (!this.zzbuX.equals(com_google_android_gms_internal_zzcjp.zzbuX)) {
            return false;
        }
        if (this.zzbuY == null) {
            if (com_google_android_gms_internal_zzcjp.zzbuY != null) {
                return false;
            }
        } else if (!this.zzbuY.equals(com_google_android_gms_internal_zzcjp.zzbuY)) {
            return false;
        }
        if (this.zzbuZ == null) {
            if (com_google_android_gms_internal_zzcjp.zzbuZ != null) {
                return false;
            }
        } else if (!this.zzbuZ.equals(com_google_android_gms_internal_zzcjp.zzbuZ)) {
            return false;
        }
        if (this.zzbva == null) {
            if (com_google_android_gms_internal_zzcjp.zzbva != null) {
                return false;
            }
        } else if (!this.zzbva.equals(com_google_android_gms_internal_zzcjp.zzbva)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjp.zzcso == null || com_google_android_gms_internal_zzcjp.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjp.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbva == null ? 0 : this.zzbva.hashCode()) + (((this.zzbuZ == null ? 0 : this.zzbuZ.hashCode()) + (((this.zzbuY == null ? 0 : this.zzbuY.hashCode()) + (((this.zzbuX == null ? 0 : this.zzbuX.hashCode()) + (((this.zzbuW == null ? 0 : this.zzbuW.intValue()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            this.zzbuW = Integer.valueOf(zzLF);
                            break;
                        default:
                            com_google_android_gms_internal_adg.zzcp(position);
                            zza(com_google_android_gms_internal_adg, zzLA);
                            continue;
                    }
                case 16:
                    this.zzbuX = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.zzbuY = com_google_android_gms_internal_adg.readString();
                    continue;
                case 34:
                    this.zzbuZ = com_google_android_gms_internal_adg.readString();
                    continue;
                case 42:
                    this.zzbva = com_google_android_gms_internal_adg.readString();
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
        if (this.zzbuW != null) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            com_google_android_gms_internal_adh.zzk(2, this.zzbuX.booleanValue());
        }
        if (this.zzbuY != null) {
            com_google_android_gms_internal_adh.zzl(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            com_google_android_gms_internal_adh.zzl(4, this.zzbuZ);
        }
        if (this.zzbva != null) {
            com_google_android_gms_internal_adh.zzl(5, this.zzbva);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuW != null) {
            zzn += adh.zzs(1, this.zzbuW.intValue());
        }
        if (this.zzbuX != null) {
            this.zzbuX.booleanValue();
            zzn += adh.zzct(2) + 1;
        }
        if (this.zzbuY != null) {
            zzn += adh.zzm(3, this.zzbuY);
        }
        if (this.zzbuZ != null) {
            zzn += adh.zzm(4, this.zzbuZ);
        }
        return this.zzbva != null ? zzn + adh.zzm(5, this.zzbva) : zzn;
    }
}
