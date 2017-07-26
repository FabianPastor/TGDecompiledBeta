package com.google.android.gms.internal;

import java.io.IOException;

public final class aem extends adp {
    private static volatile aem[] zzcub;
    public String zzcuc;

    public aem() {
        this.zzcuc = "";
        this.zzcsm = -1;
    }

    public static aem[] zzMi() {
        if (zzcub == null) {
            synchronized (adn.zzcsl) {
                if (zzcub == null) {
                    zzcub = new aem[0];
                }
            }
        }
        return zzcub;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLB = com_google_android_gms_internal_adg.zzLB();
            switch (zzLB) {
                case 0:
                    break;
                case 10:
                    this.zzcuc = com_google_android_gms_internal_adg.readString();
                    continue;
                default:
                    if (!com_google_android_gms_internal_adg.zzcm(zzLB)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (!(this.zzcuc == null || this.zzcuc.equals(""))) {
            com_google_android_gms_internal_adh.zzl(1, this.zzcuc);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        return (this.zzcuc == null || this.zzcuc.equals("")) ? zzn : zzn + adh.zzm(1, this.zzcuc);
    }
}
