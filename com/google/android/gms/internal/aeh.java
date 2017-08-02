package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class aeh extends adj<aeh> implements Cloneable {
    private String version;
    private int zzbpb;
    private String zzctL;

    public aeh() {
        this.zzbpb = 0;
        this.zzctL = "";
        this.version = "";
        this.zzcso = null;
        this.zzcsx = -1;
    }

    private aeh zzMb() {
        try {
            return (aeh) super.zzLN();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMb();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof aeh)) {
            return false;
        }
        aeh com_google_android_gms_internal_aeh = (aeh) obj;
        if (this.zzbpb != com_google_android_gms_internal_aeh.zzbpb) {
            return false;
        }
        if (this.zzctL == null) {
            if (com_google_android_gms_internal_aeh.zzctL != null) {
                return false;
            }
        } else if (!this.zzctL.equals(com_google_android_gms_internal_aeh.zzctL)) {
            return false;
        }
        if (this.version == null) {
            if (com_google_android_gms_internal_aeh.version != null) {
                return false;
            }
        } else if (!this.version.equals(com_google_android_gms_internal_aeh.version)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_aeh.zzcso == null || com_google_android_gms_internal_aeh.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_aeh.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.version == null ? 0 : this.version.hashCode()) + (((this.zzctL == null ? 0 : this.zzctL.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.zzbpb) * 31)) * 31)) * 31;
        if (!(this.zzcso == null || this.zzcso.isEmpty())) {
            i = this.zzcso.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLN() throws CloneNotSupportedException {
        return (aeh) clone();
    }

    public final /* synthetic */ adp zzLO() throws CloneNotSupportedException {
        return (aeh) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 8:
                    this.zzbpb = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 18:
                    this.zzctL = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.version = com_google_android_gms_internal_adg.readString();
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
        if (this.zzbpb != 0) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbpb);
        }
        if (!(this.zzctL == null || this.zzctL.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.zzctL);
        }
        if (!(this.version == null || this.version.equals(""))) {
            com_google_android_gms_internal_adh.zzl(3, this.version);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbpb != 0) {
            zzn += adh.zzs(1, this.zzbpb);
        }
        if (!(this.zzctL == null || this.zzctL.equals(""))) {
            zzn += adh.zzm(2, this.zzctL);
        }
        return (this.version == null || this.version.equals("")) ? zzn : zzn + adh.zzm(3, this.version);
    }
}
