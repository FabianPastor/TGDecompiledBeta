package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adj<M extends adj<M>> extends adp {
    protected adl zzcsd;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLO();
    }

    public M zzLO() throws CloneNotSupportedException {
        adj com_google_android_gms_internal_adj = (adj) super.zzLP();
        adn.zza(this, com_google_android_gms_internal_adj);
        return com_google_android_gms_internal_adj;
    }

    public /* synthetic */ adp zzLP() throws CloneNotSupportedException {
        return (adj) clone();
    }

    public final <T> T zza(adk<M, T> com_google_android_gms_internal_adk_M__T) {
        if (this.zzcsd == null) {
            return null;
        }
        adm zzcx = this.zzcsd.zzcx(com_google_android_gms_internal_adk_M__T.tag >>> 3);
        return zzcx != null ? zzcx.zzb(com_google_android_gms_internal_adk_M__T) : null;
    }

    public void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (this.zzcsd != null) {
            for (int i = 0; i < this.zzcsd.size(); i++) {
                this.zzcsd.zzcy(i).zza(com_google_android_gms_internal_adh);
            }
        }
    }

    protected final boolean zza(adg com_google_android_gms_internal_adg, int i) throws IOException {
        int position = com_google_android_gms_internal_adg.getPosition();
        if (!com_google_android_gms_internal_adg.zzcm(i)) {
            return false;
        }
        int i2 = i >>> 3;
        adr com_google_android_gms_internal_adr = new adr(i, com_google_android_gms_internal_adg.zzp(position, com_google_android_gms_internal_adg.getPosition() - position));
        adm com_google_android_gms_internal_adm = null;
        if (this.zzcsd == null) {
            this.zzcsd = new adl();
        } else {
            com_google_android_gms_internal_adm = this.zzcsd.zzcx(i2);
        }
        if (com_google_android_gms_internal_adm == null) {
            com_google_android_gms_internal_adm = new adm();
            this.zzcsd.zza(i2, com_google_android_gms_internal_adm);
        }
        com_google_android_gms_internal_adm.zza(com_google_android_gms_internal_adr);
        return true;
    }

    protected int zzn() {
        int i = 0;
        if (this.zzcsd == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcsd.size()) {
            i2 += this.zzcsd.zzcy(i).zzn();
            i++;
        }
        return i2;
    }
}
