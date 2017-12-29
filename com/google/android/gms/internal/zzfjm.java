package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzfjm<M extends zzfjm<M>> extends zzfjs {
    protected zzfjo zzpnc;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzdaf();
    }

    public void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
        if (this.zzpnc != null) {
            for (int i = 0; i < this.zzpnc.size(); i++) {
                this.zzpnc.zzmk(i).zza(com_google_android_gms_internal_zzfjk);
            }
        }
    }

    protected final boolean zza(zzfjj com_google_android_gms_internal_zzfjj, int i) throws IOException {
        int position = com_google_android_gms_internal_zzfjj.getPosition();
        if (!com_google_android_gms_internal_zzfjj.zzkq(i)) {
            return false;
        }
        int i2 = i >>> 3;
        zzfju com_google_android_gms_internal_zzfju = new zzfju(i, com_google_android_gms_internal_zzfjj.zzal(position, com_google_android_gms_internal_zzfjj.getPosition() - position));
        zzfjp com_google_android_gms_internal_zzfjp = null;
        if (this.zzpnc == null) {
            this.zzpnc = new zzfjo();
        } else {
            com_google_android_gms_internal_zzfjp = this.zzpnc.zzmj(i2);
        }
        if (com_google_android_gms_internal_zzfjp == null) {
            com_google_android_gms_internal_zzfjp = new zzfjp();
            this.zzpnc.zza(i2, com_google_android_gms_internal_zzfjp);
        }
        com_google_android_gms_internal_zzfjp.zza(com_google_android_gms_internal_zzfju);
        return true;
    }

    public M zzdaf() throws CloneNotSupportedException {
        zzfjm com_google_android_gms_internal_zzfjm = (zzfjm) super.zzdag();
        zzfjq.zza(this, com_google_android_gms_internal_zzfjm);
        return com_google_android_gms_internal_zzfjm;
    }

    public /* synthetic */ zzfjs zzdag() throws CloneNotSupportedException {
        return (zzfjm) clone();
    }

    protected int zzq() {
        int i = 0;
        if (this.zzpnc == null) {
            return 0;
        }
        int i2 = 0;
        while (i < this.zzpnc.size()) {
            i2 += this.zzpnc.zzmk(i).zzq();
            i++;
        }
        return i2;
    }
}
