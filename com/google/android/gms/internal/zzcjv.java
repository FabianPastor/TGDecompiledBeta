package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;

public final class zzcjv extends ada<zzcjv> {
    private static volatile zzcjv[] zzbvv;
    public Integer count;
    public String name;
    public zzcjw[] zzbvw;
    public Long zzbvx;
    public Long zzbvy;

    public zzcjv() {
        this.zzbvw = zzcjw.zzzC();
        this.name = null;
        this.zzbvx = null;
        this.zzbvy = null;
        this.count = null;
        this.zzcrZ = null;
        this.zzcsi = -1;
    }

    public static zzcjv[] zzzB() {
        if (zzbvv == null) {
            synchronized (ade.zzcsh) {
                if (zzbvv == null) {
                    zzbvv = new zzcjv[0];
                }
            }
        }
        return zzbvv;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjv)) {
            return false;
        }
        zzcjv com_google_android_gms_internal_zzcjv = (zzcjv) obj;
        if (!ade.equals(this.zzbvw, com_google_android_gms_internal_zzcjv.zzbvw)) {
            return false;
        }
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjv.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjv.name)) {
            return false;
        }
        if (this.zzbvx == null) {
            if (com_google_android_gms_internal_zzcjv.zzbvx != null) {
                return false;
            }
        } else if (!this.zzbvx.equals(com_google_android_gms_internal_zzcjv.zzbvx)) {
            return false;
        }
        if (this.zzbvy == null) {
            if (com_google_android_gms_internal_zzcjv.zzbvy != null) {
                return false;
            }
        } else if (!this.zzbvy.equals(com_google_android_gms_internal_zzcjv.zzbvy)) {
            return false;
        }
        if (this.count == null) {
            if (com_google_android_gms_internal_zzcjv.count != null) {
                return false;
            }
        } else if (!this.count.equals(com_google_android_gms_internal_zzcjv.count)) {
            return false;
        }
        return (this.zzcrZ == null || this.zzcrZ.isEmpty()) ? com_google_android_gms_internal_zzcjv.zzcrZ == null || com_google_android_gms_internal_zzcjv.zzcrZ.isEmpty() : this.zzcrZ.equals(com_google_android_gms_internal_zzcjv.zzcrZ);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.count == null ? 0 : this.count.hashCode()) + (((this.zzbvy == null ? 0 : this.zzbvy.hashCode()) + (((this.zzbvx == null ? 0 : this.zzbvx.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((((getClass().getName().hashCode() + 527) * 31) + ade.hashCode(this.zzbvw)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 10);
                    zzLy = this.zzbvw == null ? 0 : this.zzbvw.length;
                    Object obj = new zzcjw[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzbvw, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new zzcjw();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new zzcjw();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzbvw = obj;
                    continue;
                case 18:
                    this.name = com_google_android_gms_internal_acx.readString();
                    continue;
                case 24:
                    this.zzbvx = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case 32:
                    this.zzbvy = Long.valueOf(com_google_android_gms_internal_acx.zzLE());
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.count = Integer.valueOf(com_google_android_gms_internal_acx.zzLD());
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
        if (this.zzbvw != null && this.zzbvw.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvw) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(1, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.name != null) {
            com_google_android_gms_internal_acy.zzl(2, this.name);
        }
        if (this.zzbvx != null) {
            com_google_android_gms_internal_acy.zzb(3, this.zzbvx.longValue());
        }
        if (this.zzbvy != null) {
            com_google_android_gms_internal_acy.zzb(4, this.zzbvy.longValue());
        }
        if (this.count != null) {
            com_google_android_gms_internal_acy.zzr(5, this.count.intValue());
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.zzbvw != null && this.zzbvw.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzbvw) {
                if (com_google_android_gms_internal_adg != null) {
                    zzn += acy.zzb(1, com_google_android_gms_internal_adg);
                }
            }
        }
        if (this.name != null) {
            zzn += acy.zzm(2, this.name);
        }
        if (this.zzbvx != null) {
            zzn += acy.zze(3, this.zzbvx.longValue());
        }
        if (this.zzbvy != null) {
            zzn += acy.zze(4, this.zzbvy.longValue());
        }
        return this.count != null ? zzn + acy.zzs(5, this.count.intValue()) : zzn;
    }
}
