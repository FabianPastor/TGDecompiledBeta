package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class aeh extends adj<aeh> implements Cloneable {
    private String version;
    private int zzbpb;
    private String zzctA;

    public aeh() {
        this.zzbpb = 0;
        this.zzctA = "";
        this.version = "";
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    private aeh zzMc() {
        try {
            return (aeh) super.zzLO();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzMc();
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
        if (this.zzctA == null) {
            if (com_google_android_gms_internal_aeh.zzctA != null) {
                return false;
            }
        } else if (!this.zzctA.equals(com_google_android_gms_internal_aeh.zzctA)) {
            return false;
        }
        if (this.version == null) {
            if (com_google_android_gms_internal_aeh.version != null) {
                return false;
            }
        } else if (!this.version.equals(com_google_android_gms_internal_aeh.version)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_aeh.zzcsd == null || com_google_android_gms_internal_aeh.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_aeh.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.version == null ? 0 : this.version.hashCode()) + (((this.zzctA == null ? 0 : this.zzctA.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.zzbpb) * 31)) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adj zzLO() throws CloneNotSupportedException {
        return (aeh) clone();
    }

    public final /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (aeh) clone();
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 8:
                    this.zzbpb = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 18:
                    this.zzctA = com_google_android_gms_internal_adg.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.version = com_google_android_gms_internal_adg.readString();
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
        if (this.zzbpb != 0) {
            com_google_android_gms_internal_adh.zzr(1, this.zzbpb);
        }
        if (!(this.zzctA == null || this.zzctA.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.zzctA);
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
        if (!(this.zzctA == null || this.zzctA.equals(""))) {
            zzn += adh.zzm(2, this.zzctA);
        }
        return (this.version == null || this.version.equals("")) ? zzn : zzn + adh.zzm(3, this.version);
    }
}
