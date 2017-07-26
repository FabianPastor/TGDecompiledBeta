package com.google.android.gms.internal;

import java.io.IOException;

public final class hf extends adj<hf> {
    private static volatile hf[] zzbTI;
    public String name;
    public hg zzbTJ;

    public hf() {
        this.name = "";
        this.zzbTJ = null;
        this.zzcsd = null;
        this.zzcsm = -1;
    }

    public static hf[] zzEa() {
        if (zzbTI == null) {
            synchronized (adn.zzcsl) {
                if (zzbTI == null) {
                    zzbTI = new hf[0];
                }
            }
        }
        return zzbTI;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof hf)) {
            return false;
        }
        hf hfVar = (hf) obj;
        if (this.name == null) {
            if (hfVar.name != null) {
                return false;
            }
        } else if (!this.name.equals(hfVar.name)) {
            return false;
        }
        if (this.zzbTJ == null) {
            if (hfVar.zzbTJ != null) {
                return false;
            }
        } else if (!this.zzbTJ.equals(hfVar.zzbTJ)) {
            return false;
        }
        return (this.zzcsd == null || this.zzcsd.isEmpty()) ? hfVar.zzcsd == null || hfVar.zzcsd.isEmpty() : this.zzcsd.equals(hfVar.zzcsd);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.zzbTJ == null ? 0 : this.zzbTJ.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + ((getClass().getName().hashCode() + 527) * 31)) * 31)) * 31;
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
                    this.name = com_google_android_gms_internal_adg.readString();
                    continue;
                case 18:
                    if (this.zzbTJ == null) {
                        this.zzbTJ = new hg();
                    }
                    com_google_android_gms_internal_adg.zza(this.zzbTJ);
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
        com_google_android_gms_internal_adh.zzl(1, this.name);
        if (this.zzbTJ != null) {
            com_google_android_gms_internal_adh.zza(2, this.zzbTJ);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn() + adh.zzm(1, this.name);
        return this.zzbTJ != null ? zzn + adh.zzb(2, this.zzbTJ) : zzn;
    }
}
