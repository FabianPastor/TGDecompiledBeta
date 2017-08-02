package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjo extends adj<zzcjo> {
    private static volatile zzcjo[] zzbuR;
    public zzcjr zzbuS;
    public zzcjp zzbuT;
    public Boolean zzbuU;
    public String zzbuV;

    public zzcjo() {
        this.zzbuS = null;
        this.zzbuT = null;
        this.zzbuU = null;
        this.zzbuV = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjo[] zzzw() {
        if (zzbuR == null) {
            synchronized (adn.zzcsw) {
                if (zzbuR == null) {
                    zzbuR = new zzcjo[0];
                }
            }
        }
        return zzbuR;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjo)) {
            return false;
        }
        zzcjo com_google_android_gms_internal_zzcjo = (zzcjo) obj;
        if (this.zzbuS == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuS != null) {
                return false;
            }
        } else if (!this.zzbuS.equals(com_google_android_gms_internal_zzcjo.zzbuS)) {
            return false;
        }
        if (this.zzbuT == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuT != null) {
                return false;
            }
        } else if (!this.zzbuT.equals(com_google_android_gms_internal_zzcjo.zzbuT)) {
            return false;
        }
        if (this.zzbuU == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuU != null) {
                return false;
            }
        } else if (!this.zzbuU.equals(com_google_android_gms_internal_zzcjo.zzbuU)) {
            return false;
        }
        if (this.zzbuV == null) {
            if (com_google_android_gms_internal_zzcjo.zzbuV != null) {
                return false;
            }
        } else if (!this.zzbuV.equals(com_google_android_gms_internal_zzcjo.zzbuV)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjo.zzcso == null || com_google_android_gms_internal_zzcjo.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjo.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuV == null ? 0 : this.zzbuV.hashCode()) + (((this.zzbuU == null ? 0 : this.zzbuU.hashCode()) + (((this.zzbuT == null ? 0 : this.zzbuT.hashCode()) + (((this.zzbuS == null ? 0 : this.zzbuS.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31;
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
                case 10:
                    if (this.zzbuS == null) {
                        this.zzbuS = new zzcjr();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbuS);
                    continue;
                case 18:
                    if (this.zzbuT == null) {
                        this.zzbuT = new zzcjp();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbuT);
                    continue;
                case 24:
                    this.zzbuU = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 34:
                    this.zzbuV = com_google_android_gms_internal_adg.readString();
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
        if (this.zzbuS != null) {
            com_google_android_gms_internal_adh.zza(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            com_google_android_gms_internal_adh.zza(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            com_google_android_gms_internal_adh.zzk(3, this.zzbuU.booleanValue());
        }
        if (this.zzbuV != null) {
            com_google_android_gms_internal_adh.zzl(4, this.zzbuV);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbuS != null) {
            zzn += adh.zzb(1, this.zzbuS);
        }
        if (this.zzbuT != null) {
            zzn += adh.zzb(2, this.zzbuT);
        }
        if (this.zzbuU != null) {
            this.zzbuU.booleanValue();
            zzn += adh.zzct(3) + 1;
        }
        return this.zzbuV != null ? zzn + adh.zzm(4, this.zzbuV) : zzn;
    }
}
