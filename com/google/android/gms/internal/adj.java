package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adj<M extends adj<M>> extends adp {
    protected adl zzcso;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzLN();
    }

    public M zzLN() throws CloneNotSupportedException {
        adj com_google_android_gms_internal_adj = (adj) super.zzLO();
        adn.zza(this, com_google_android_gms_internal_adj);
        return com_google_android_gms_internal_adj;
    }

    public /* synthetic */ adp zzLO() throws CloneNotSupportedException {
        return (adj) clone();
    }

    public final <T> T zza(adk<M, T> com_google_android_gms_internal_adk_M__T) {
        if (this.zzcso == null) {
            return null;
        }
        adm zzcx = this.zzcso.zzcx(com_google_android_gms_internal_adk_M__T.tag >>> 3);
        return zzcx != null ? zzcx.zzb(com_google_android_gms_internal_adk_M__T) : null;
    }

    public void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (this.zzcso != null) {
            for (int i = 0; i < this.zzcso.size(); i++) {
                this.zzcso.zzcy(i).zza(com_google_android_gms_internal_adh);
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
        if (this.zzcso == null) {
            this.zzcso = new adl();
        } else {
            com_google_android_gms_internal_adm = this.zzcso.zzcx(i2);
        }
        if (com_google_android_gms_internal_adm == null) {
            com_google_android_gms_internal_adm = new adm();
            this.zzcso.zza(i2, com_google_android_gms_internal_adm);
        }
        com_google_android_gms_internal_adm.zza(com_google_android_gms_internal_adr);
        return true;
    }

    protected int zzn() {
        int i = 0;
        if (this.zzcso == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzcso.size()) {
            i2 += this.zzcso.zzcy(i).zzn();
            i++;
        }
        return i2;
    }
}
