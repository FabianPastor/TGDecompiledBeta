package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjn extends ada<zzcjn> {
    private static volatile zzcjn[] zzbuR;
    public zzcjq zzbuS;
    public zzcjo zzbuT;
    public Boolean zzbuU;
    public String zzbuV;

    public zzcjn() {
        this.zzbuS = null;
        this.zzbuT = null;
        this.zzbuU = null;
        this.zzbuV = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjn[] zzzw() {
        if (zzbuR == null) {
            synchronized (ade.zzcsh) {
                if (zzbuR == null) {
                    zzbuR = new zzcjn[0];
                }
            }
        }
        return zzbuR;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjn)) {
            return false;
        }
        zzcjn com_google_android_gms_internal_zzcjn = (zzcjn) obj;
        if (this.zzbuS == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuS != null) {
                return false;
            }
        } else if (!this.zzbuS.equals(com_google_android_gms_internal_zzcjn.zzbuS)) {
            return false;
        }
        if (this.zzbuT == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuT != null) {
                return false;
            }
        } else if (!this.zzbuT.equals(com_google_android_gms_internal_zzcjn.zzbuT)) {
            return false;
        }
        if (this.zzbuU == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuU != null) {
                return false;
            }
        } else if (!this.zzbuU.equals(com_google_android_gms_internal_zzcjn.zzbuU)) {
            return false;
        }
        if (this.zzbuV == null) {
            if (com_google_android_gms_internal_zzcjn.zzbuV != null) {
                return false;
            }
        } else if (!this.zzbuV.equals(com_google_android_gms_internal_zzcjn.zzbuV)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjn.zzcrZ == null || com_google_android_gms_internal_zzcjn.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjn.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuV == null ? 0 : this.zzbuV.hashCode()) + (((this.zzbuU == null ? 0 : this.zzbuU.hashCode()) + (((this.zzbuT == null ? 0 : this.zzbuT.hashCode()) + (((this.zzbuS == null ? 0 : this.zzbuS.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
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
                case 10:
                    if (this.zzbuS == null) {
                        this.zzbuS = new zzcjq();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbuS);
                    continue;
                case 18:
                    if (this.zzbuT == null) {
                        this.zzbuT = new zzcjo();
                    }
                    com_google_android_gms_internal_acx.zza(this.zzbuT);
                    continue;
                case 24:
                    this.zzbuU = Boolean.valueOf(com_google_android_gms_internal_acx.zzLB());
                    continue;
                case 34:
                    this.zzbuV = com_google_android_gms_internal_acx.readString();
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
        if (this.zzbuS != null) {
            com_google_android_gms_internal_acy.zza(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            com_google_android_gms_internal_acy.zza(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            com_google_android_gms_internal_acy.zzk(3, this.zzbuU.booleanValue());
        }
        if (this.zzbuV != null) {
            com_google_android_gms_internal_acy.zzl(4, this.zzbuV);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuS != null) {
            zzn += acy.zzb(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            zzn += acy.zzb(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            this.zzbuU.booleanValue();
            zzn += acy.zzct(3) + 1;
        }
        return this.zzbuV != null ? zzn + acy.zzm(4, this.zzbuV) : zzn;
    }
}
