package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;

public final class zzcjw extends adj<zzcjw> {
    private static volatile zzcjw[] zzbvv;
    public Integer count;
    public String name;
    public zzcjx[] zzbvw;
    public Long zzbvx;
    public Long zzbvy;

    public zzcjw() {
        this.zzbvw = zzcjx.zzzC();
        this.name = null;
        this.zzbvx = null;
        this.zzbvy = null;
        this.count = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjw[] zzzB() {
        if (zzbvv == null) {
            synchronized (adn.zzcsw) {
                if (zzbvv == null) {
                    zzbvv = new zzcjw[0];
                }
            }
        }
        return zzbvv;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjw)) {
            return false;
        }
        zzcjw com_google_android_gms_internal_zzcjw = (zzcjw) obj;
        if (!adn.equals(this.zzbvw, com_google_android_gms_internal_zzcjw.zzbvw)) {
            return false;
        }
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjw.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjw.name)) {
            return false;
        }
        if (this.zzbvx == null) {
            if (com_google_android_gms_internal_zzcjw.zzbvx != null) {
                return false;
            }
        } else if (!this.zzbvx.equals(com_google_android_gms_internal_zzcjw.zzbvx)) {
            return false;
        }
        if (this.zzbvy == null) {
            if (com_google_android_gms_internal_zzcjw.zzbvy != null) {
                return false;
            }
        } else if (!this.zzbvy.equals(com_google_android_gms_internal_zzcjw.zzbvy)) {
            return false;
        }
        if (this.count == null) {
            if (com_google_android_gms_internal_zzcjw.count != null) {
                return false;
            }
        } else if (!this.count.equals(com_google_android_gms_internal_zzcjw.count)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjw.zzcso == null || com_google_android_gms_internal_zzcjw.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjw.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzbvy == null ? 0 : this.zzbvy.hashCode()) + (((this.zzbvx == null ? 0 : this.zzbvx.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + adn.hashCode(this.zzbvw)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 10);
                    zzLA = this.zzbvw == null ? 0 : this.zzbvw.length;
                    Object obj = new zzcjx[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzbvw, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new zzcjx();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new zzcjx();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzbvw = obj;
                    continue;
                case 18:
                    this.name = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzbvx = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 32:
                    this.zzbvy = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.count = Integer.valueOf(com_google_android_gms_internal_adg.zzLF());
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
        if (this.zzbvw != null && this.zzbvw.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvw) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(1, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.name != null) {
            com_google_android_gms_internal_adh.zzl(2, this.name);
        }
        if (this.zzbvx != null) {
            com_google_android_gms_internal_adh.zzb(3, this.zzbvx.longValue());
        }
        if (this.zzbvy != null) {
            com_google_android_gms_internal_adh.zzb(4, this.zzbvy.longValue());
        }
        if (this.count != null) {
            com_google_android_gms_internal_adh.zzr(5, this.count.intValue());
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbvw != null && this.zzbvw.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzbvw) {
                if (com_google_android_gms_internal_adp != null) {
                    zzn += adh.zzb(1, com_google_android_gms_internal_adp);
                }
            }
        }
        if (this.name != null) {
            zzn += adh.zzm(2, this.name);
        }
        if (this.zzbvx != null) {
            zzn += adh.zze(3, this.zzbvx.longValue());
        }
        if (this.zzbvy != null) {
            zzn += adh.zze(4, this.zzbvy.longValue());
        }
        return this.count != null ? zzn + adh.zzs(5, this.count.intValue()) : zzn;
    }
}
