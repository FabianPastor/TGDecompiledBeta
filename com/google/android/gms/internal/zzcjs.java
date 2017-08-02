package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjs extends adj<zzcjs> {
    private static volatile zzcjs[] zzbvi;
    public String name;
    public Boolean zzbvj;
    public Boolean zzbvk;

    public zzcjs() {
        this.name = null;
        this.zzbvj = null;
        this.zzbvk = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjs[] zzzy() {
        if (zzbvi == null) {
            synchronized (adn.zzcsw) {
                if (zzbvi == null) {
                    zzbvi = new zzcjs[0];
                }
            }
        }
        return zzbvi;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjs)) {
            return false;
        }
        zzcjs com_google_android_gms_internal_zzcjs = (zzcjs) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjs.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjs.name)) {
            return false;
        }
        if (this.zzbvj == null) {
            if (com_google_android_gms_internal_zzcjs.zzbvj != null) {
                return false;
            }
        } else if (!this.zzbvj.equals(com_google_android_gms_internal_zzcjs.zzbvj)) {
            return false;
        }
        if (this.zzbvk == null) {
            if (com_google_android_gms_internal_zzcjs.zzbvk != null) {
                return false;
            }
        } else if (!this.zzbvk.equals(com_google_android_gms_internal_zzcjs.zzbvk)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjs.zzcso == null || com_google_android_gms_internal_zzcjs.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjs.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbvk == null ? 0 : this.zzbvk.hashCode()) + (((this.zzbvj == null ? 0 : this.zzbvj.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31;
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
                    this.name = com_google_android_gms_internal_adg.readString();
                    continue;
                case 16:
                    this.zzbvj = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
                    continue;
                case 24:
                    this.zzbvk = Boolean.valueOf(com_google_android_gms_internal_adg.zzLD());
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
        if (this.name != null) {
            com_google_android_gms_internal_adh.zzl(1, this.name);
        }
        if (this.zzbvj != null) {
            com_google_android_gms_internal_adh.zzk(2, this.zzbvj.booleanValue());
        }
        if (this.zzbvk != null) {
            com_google_android_gms_internal_adh.zzk(3, this.zzbvk.booleanValue());
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.name != null) {
            zzn += adh.zzm(1, this.name);
        }
        if (this.zzbvj != null) {
            this.zzbvj.booleanValue();
            zzn += adh.zzct(2) + 1;
        }
        if (this.zzbvk == null) {
            return zzn;
        }
        this.zzbvk.booleanValue();
        return zzn + (adh.zzct(3) + 1);
    }
}
