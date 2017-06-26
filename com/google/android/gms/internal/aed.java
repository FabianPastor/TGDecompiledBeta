package com.google.android.gms.internal;

import java.io.IOException;

public final class aed extends adg {
    private static volatile aed[] zzctX;
    public String zzctY;

    public aed() {
        this.zzctY = "";
        this.zzcsi = -1;
    }

    public static aed[] zzMf() {
        if (zzctX == null) {
            synchronized (ade.zzcsh) {
                if (zzctX == null) {
                    zzctX = new aed[0];
                }
            }
        }
        return zzctX;
    }

    public final /* synthetic */ adg zza(acx com_google_android_gms_internal_acx) throws IOException {
        while (true) {
            int zzLy = com_google_android_gms_internal_acx.zzLy();
            switch (zzLy) {
                case 0:
                    break;
                case 10:
                    this.zzctY = com_google_android_gms_internal_acx.readString();
                    continue;
                default:
                    if (!com_google_android_gms_internal_acx.zzcm(zzLy)) {
                        break;
                    }
                    continue;
            }
            return this;
        }
    }

    public final void zza(acy com_google_android_gms_internal_acy) throws IOException {
        if (!(this.zzctY == null || this.zzctY.equals(""))) {
            com_google_android_gms_internal_acy.zzl(1, this.zzctY);
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        return (this.zzctY == null || this.zzctY.equals("")) ? zzn : zzn + acy.zzm(1, this.zzctY);
    }
}
