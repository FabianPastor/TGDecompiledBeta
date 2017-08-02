package com.google.android.gms.internal;

import java.io.IOException;

public final class aem extends adp {
    private static volatile aem[] zzcum;
    public String zzcun;

    public aem() {
        this.zzcun = "";
        this.zzcsx = -1;
    }

    public static aem[] zzMh() {
        if (zzcum == null) {
            synchronized (adn.zzcsw) {
                if (zzcum == null) {
                    zzcum = new aem[0];
                }
            }
        }
        return zzcum;
    }

    public final /* synthetic */ adp zza(adg com_google_android_gms_internal_adg) throws IOException {
        while (true) {
            int zzLA = com_google_android_gms_internal_adg.zzLA();
            switch (zzLA) {
                case 0:
                    break;
                case 10:
                    this.zzcun = com_google_android_gms_internal_adg.readString();
                    continue;
                default:
                    if (!com_google_android_gms_internal_adg.zzcm(zzLA)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(adh com_google_android_gms_internal_adh) throws IOException {
        if (!(this.zzcun == null || this.zzcun.equals(""))) {
            com_google_android_gms_internal_adh.zzl(1, this.zzcun);
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        return (this.zzcun == null || this.zzcun.equals("")) ? zzn : zzn + adh.zzm(1, this.zzcun);
    }
}
