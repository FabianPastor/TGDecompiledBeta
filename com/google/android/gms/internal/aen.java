package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;

public final class aen extends adp {
    public long zzaLt;
    public String zzcun;
    public String zzcuo;
    public long zzcup;
    public String zzcuq;
    public long zzcur;
    public String zzcus;
    public String zzcut;
    public String zzcuu;
    public String zzcuv;
    public String zzcuw;
    public int zzcux;
    public aem[] zzcuy;

    public aen() {
        this.zzcun = "";
        this.zzcuo = "";
        this.zzcup = 0;
        this.zzcuq = "";
        this.zzcur = 0;
        this.zzaLt = 0;
        this.zzcus = "";
        this.zzcut = "";
        this.zzcuu = "";
        this.zzcuv = "";
        this.zzcuw = "";
        this.zzcux = 0;
        this.zzcuy = aem.zzMh();
        this.zzcsx = -1;
    }

    public static aen zzL(byte[] bArr) throws ado {
        return (aen) adp.zza(new aen(), bArr);
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
                case 18:
                    this.zzcuo = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzcup = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 34:
                    this.zzcuq = com_google_android_gms_internal_adg.readString();
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzcur = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 48:
                    this.zzaLt = com_google_android_gms_internal_adg.zzLB();
                    continue;
                case 58:
                    this.zzcus = com_google_android_gms_internal_adg.readString();
                    continue;
                case 66:
                    this.zzcut = com_google_android_gms_internal_adg.readString();
                    continue;
                case 74:
                    this.zzcuu = com_google_android_gms_internal_adg.readString();
                    continue;
                case 82:
                    this.zzcuv = com_google_android_gms_internal_adg.readString();
                    continue;
                case 90:
                    this.zzcuw = com_google_android_gms_internal_adg.readString();
                    continue;
                case 96:
                    this.zzcux = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 106:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 106);
                    zzLA = this.zzcuy == null ? 0 : this.zzcuy.length;
                    Object obj = new aem[(zzb + zzLA)];
                    if (zzLA != 0) {
                        System.arraycopy(this.zzcuy, 0, obj, 0, zzLA);
                    }
                    while (zzLA < obj.length - 1) {
                        obj[zzLA] = new aem();
                        com_google_android_gms_internal_adg.zza(obj[zzLA]);
                        com_google_android_gms_internal_adg.zzLA();
                        zzLA++;
                    }
                    obj[zzLA] = new aem();
                    com_google_android_gms_internal_adg.zza(obj[zzLA]);
                    this.zzcuy = obj;
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
        if (!(this.zzcuo == null || this.zzcuo.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.zzcuo);
        }
        if (this.zzcup != 0) {
            com_google_android_gms_internal_adh.zzb(3, this.zzcup);
        }
        if (!(this.zzcuq == null || this.zzcuq.equals(""))) {
            com_google_android_gms_internal_adh.zzl(4, this.zzcuq);
        }
        if (this.zzcur != 0) {
            com_google_android_gms_internal_adh.zzb(5, this.zzcur);
        }
        if (this.zzaLt != 0) {
            com_google_android_gms_internal_adh.zzb(6, this.zzaLt);
        }
        if (!(this.zzcus == null || this.zzcus.equals(""))) {
            com_google_android_gms_internal_adh.zzl(7, this.zzcus);
        }
        if (!(this.zzcut == null || this.zzcut.equals(""))) {
            com_google_android_gms_internal_adh.zzl(8, this.zzcut);
        }
        if (!(this.zzcuu == null || this.zzcuu.equals(""))) {
            com_google_android_gms_internal_adh.zzl(9, this.zzcuu);
        }
        if (!(this.zzcuv == null || this.zzcuv.equals(""))) {
            com_google_android_gms_internal_adh.zzl(10, this.zzcuv);
        }
        if (!(this.zzcuw == null || this.zzcuw.equals(""))) {
            com_google_android_gms_internal_adh.zzl(11, this.zzcuw);
        }
        if (this.zzcux != 0) {
            com_google_android_gms_internal_adh.zzr(12, this.zzcux);
        }
        if (this.zzcuy != null && this.zzcuy.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzcuy) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(13, com_google_android_gms_internal_adp);
                }
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.zzcun == null || this.zzcun.equals(""))) {
            zzn += adh.zzm(1, this.zzcun);
        }
        if (!(this.zzcuo == null || this.zzcuo.equals(""))) {
            zzn += adh.zzm(2, this.zzcuo);
        }
        if (this.zzcup != 0) {
            zzn += adh.zze(3, this.zzcup);
        }
        if (!(this.zzcuq == null || this.zzcuq.equals(""))) {
            zzn += adh.zzm(4, this.zzcuq);
        }
        if (this.zzcur != 0) {
            zzn += adh.zze(5, this.zzcur);
        }
        if (this.zzaLt != 0) {
            zzn += adh.zze(6, this.zzaLt);
        }
        if (!(this.zzcus == null || this.zzcus.equals(""))) {
            zzn += adh.zzm(7, this.zzcus);
        }
        if (!(this.zzcut == null || this.zzcut.equals(""))) {
            zzn += adh.zzm(8, this.zzcut);
        }
        if (!(this.zzcuu == null || this.zzcuu.equals(""))) {
            zzn += adh.zzm(9, this.zzcuu);
        }
        if (!(this.zzcuv == null || this.zzcuv.equals(""))) {
            zzn += adh.zzm(10, this.zzcuv);
        }
        if (!(this.zzcuw == null || this.zzcuw.equals(""))) {
            zzn += adh.zzm(11, this.zzcuw);
        }
        if (this.zzcux != 0) {
            zzn += adh.zzs(12, this.zzcux);
        }
        if (this.zzcuy == null || this.zzcuy.length <= 0) {
            return zzn;
        }
        int i = zzn;
        for (adp com_google_android_gms_internal_adp : this.zzcuy) {
            if (com_google_android_gms_internal_adp != null) {
                i += adh.zzb(13, com_google_android_gms_internal_adp);
            }
        }
        return i;
    }
}
