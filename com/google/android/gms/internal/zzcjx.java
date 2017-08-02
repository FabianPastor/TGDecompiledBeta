package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcjx extends adj<zzcjx> {
    private static volatile zzcjx[] zzbvz;
    public String name;
    public String zzaIF;
    private Float zzbuA;
    public Double zzbuB;
    public Long zzbvA;

    public zzcjx() {
        this.name = null;
        this.zzaIF = null;
        this.zzbvA = null;
        this.zzbuA = null;
        this.zzbuB = null;
        this.zzcso = null;
        this.zzcsx = -1;
    }

    public static zzcjx[] zzzC() {
        if (zzbvz == null) {
            synchronized (adn.zzcsw) {
                if (zzbvz == null) {
                    zzbvz = new zzcjx[0];
                }
            }
        }
        return zzbvz;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcjx)) {
            return false;
        }
        zzcjx com_google_android_gms_internal_zzcjx = (zzcjx) obj;
        if (this.name == null) {
            if (com_google_android_gms_internal_zzcjx.name != null) {
                return false;
            }
        } else if (!this.name.equals(com_google_android_gms_internal_zzcjx.name)) {
            return false;
        }
        if (this.zzaIF == null) {
            if (com_google_android_gms_internal_zzcjx.zzaIF != null) {
                return false;
            }
        } else if (!this.zzaIF.equals(com_google_android_gms_internal_zzcjx.zzaIF)) {
            return false;
        }
        if (this.zzbvA == null) {
            if (com_google_android_gms_internal_zzcjx.zzbvA != null) {
                return false;
            }
        } else if (!this.zzbvA.equals(com_google_android_gms_internal_zzcjx.zzbvA)) {
            return false;
        }
        if (this.zzbuA == null) {
            if (com_google_android_gms_internal_zzcjx.zzbuA != null) {
                return false;
            }
        } else if (!this.zzbuA.equals(com_google_android_gms_internal_zzcjx.zzbuA)) {
            return false;
        }
        if (this.zzbuB == null) {
            if (com_google_android_gms_internal_zzcjx.zzbuB != null) {
                return false;
            }
        } else if (!this.zzbuB.equals(com_google_android_gms_internal_zzcjx.zzbuB)) {
            return false;
        }
        return (this.zzcso == null || this.zzcso.isEmpty()) ? com_google_android_gms_internal_zzcjx.zzcso == null || com_google_android_gms_internal_zzcjx.zzcso.isEmpty() : this.zzcso.equals(com_google_android_gms_internal_zzcjx.zzcso);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbuB == null ? 0 : this.zzbuB.hashCode()) + (((this.zzbuA == null ? 0 : this.zzbuA.hashCode()) + (((this.zzbvA == null ? 0 : this.zzbvA.hashCode()) + (((this.zzaIF == null ? 0 : this.zzaIF.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31)) * 31)) * 31)) * 31;
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
                case 18:
                    this.zzaIF = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzbvA = Long.valueOf(com_google_android_gms_internal_adg.zzLG());
                    continue;
                case 37:
                    this.zzbuA = Float.valueOf(Float.intBitsToFloat(com_google_android_gms_internal_adg.zzLH()));
                    continue;
                case 41:
                    this.zzbuB = Double.valueOf(Double.longBitsToDouble(com_google_android_gms_internal_adg.zzLI()));
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
        if (this.zzaIF != null) {
            com_google_android_gms_internal_adh.zzl(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            com_google_android_gms_internal_adh.zzb(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            com_google_android_gms_internal_adh.zzc(4, this.zzbuA.floatValue());
        }
        if (this.zzbuB != null) {
            com_google_android_gms_internal_adh.zza(5, this.zzbuB.doubleValue());
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (this.name != null) {
            zzn += adh.zzm(1, this.name);
        }
        if (this.zzaIF != null) {
            zzn += adh.zzm(2, this.zzaIF);
        }
        if (this.zzbvA != null) {
            zzn += adh.zze(3, this.zzbvA.longValue());
        }
        if (this.zzbuA != null) {
            this.zzbuA.floatValue();
            zzn += adh.zzct(4) + 4;
        }
        if (this.zzbuB == null) {
            return zzn;
        }
        this.zzbuB.doubleValue();
        return zzn + (adh.zzct(5) + 8);
    }
}
