package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcju extends adj<zzcju> {
    private static volatile zzcju[] zzbvq;
    public String key;
    public String value;

    public zzcju() {
        this.key = null;
        this.value = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static zzcju[] zzzz() {
        if (zzbvq == null) {
            synchronized (adn.zzcsl) {
                if (zzbvq == null) {
                    zzbvq = new zzcju[0];
                }
            }
        }
        return zzbvq;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcju)) {
            return false;
        }
        zzcju com_google_android_gms_internal_zzcju = (zzcju) obj;
        if (this.key == null) {
            if (com_google_android_gms_internal_zzcju.key != null) {
                return false;
            }
        } else if (!this.key.equals(com_google_android_gms_internal_zzcju.key)) {
            return false;
        }
        if (this.value == null) {
            if (com_google_android_gms_internal_zzcju.value != null) {
                return false;
            }
        } else if (!this.value.equals(com_google_android_gms_internal_zzcju.value)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? com_google_android_gms_internal_zzcju.zzcsd == null || com_google_android_gms_internal_zzcju.zzcsd.isEmpty() : this.zzcsd.equals(com_google_android_gms_internal_zzcju.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.value == null ? 0 : this.value.hashCode()) + (((this.key == null ? 0 : this.key.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
        if (!(this.zzcsd == null || this.zzcsd.isEmpty())) {
            i = this.zzcsd.hashCode();
        }
        return hashCode + i;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.key = com_google_android_gms_internal_adg.readString();
                    continue;
                case 18:
                    this.value = com_google_android_gms_internal_adg.readString();
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
        if (this.key != null) {
            com_google_android_gms_internal_adh.zzl(1, this.key);
        }
        if (this.value != null) {
            com_google_android_gms_internal_adh.zzl(2, this.value);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.key != null) {
            zzn += adh.zzm(1, this.key);
        }
        return this.value != null ? zzn + adh.zzm(2, this.value) : zzn;
    }
}
