package com.google.android.gms.internal;

import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;

public final class ady extends ada<ady> implements Cloneable {
    private String version;
    private int zzbpb;
    private String zzctw;

    public ady() {
        this.zzbpb = 0;
        this.zzctw = "";
        this.version = "";
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    private ady zzLZ() {
        try {
            return (ady) super.zzLL();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLZ();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ady)) {
            return false;
        }
        ady com_google_android_gms_internal_ady = (ady) obj;
        if (this.zzbpb != com_google_android_gms_internal_ady.zzbpb) {
            return false;
        }
        if (this.zzctw == null) {
            if (com_google_android_gms_internal_ady.zzctw != null) {
                return false;
            }
        } else if (!this.zzctw.equals(com_google_android_gms_internal_ady.zzctw)) {
            return false;
        }
        if (this.version == null) {
            if (com_google_android_gms_internal_ady.version != null) {
                return false;
            }
        } else if (!this.version.equals(com_google_android_gms_internal_ady.version)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_ady.zzcrZ == null || com_google_android_gms_internal_ady.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_ady.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.version == null ? 0 : this.version.hashCode()) + (((this.zzctw == null ? 0 : this.zzctw.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + this.zzbpb) * 31)) * 31)) * 31;
        if (!(this.zzcrZ == null || this.zzcrZ.isEmpty())) {
            i = this.zzcrZ.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ ada zzLL() throws CloneNotSupportedException {
        return (ady) clone();
    }

    public final /* synthetic */ adg zzLM() throws CloneNotSupportedException {
        return (ady) clone();
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 8:
                    this.zzbpb = com_google_android_gms_internal_acx.zzLA();
                    continue;
                case 18:
                    this.zzctw = com_google_android_gms_internal_acx.readString();
                    continue;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    this.version = com_google_android_gms_internal_acx.readString();
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
        if (this.zzbpb != 0) {
            com_google_android_gms_internal_acy.zzr(1, this.zzbpb);
        }
        if (!(this.zzctw == null || this.zzctw.equals(""))) {
            com_google_android_gms_internal_acy.zzl(2, this.zzctw);
        }
        if (!(this.version == null || this.version.equals(""))) {
            com_google_android_gms_internal_acy.zzl(3, this.version);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbpb != 0) {
            zzn += acy.zzs(1, this.zzbpb);
        }
        if (!(this.zzctw == null || this.zzctw.equals(""))) {
            zzn += acy.zzm(2, this.zzctw);
        }
        return (this.version == null || this.version.equals("")) ? zzn : zzn + acy.zzm(3, this.version);
    }
}
