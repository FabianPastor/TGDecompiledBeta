package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import org.telegram.messenger.SecretChatHelper;

public final class aen extends adp {
    public long zzaLt;
    public String zzcuc;
    public String zzcud;
    public long zzcue;
    public String zzcuf;
    public long zzcug;
    public String zzcuh;
    public String zzcui;
    public String zzcuj;
    public String zzcuk;
    public String zzcul;
    public int zzcum;
    public aem[] zzcun;

    public aen() {
        this.zzcuc = "";
        this.zzcud = "";
        this.zzcue = 0;
        this.zzcuf = "";
        this.zzcug = 0;
        this.zzaLt = 0;
        this.zzcuh = "";
        this.zzcui = "";
        this.zzcuj = "";
        this.zzcuk = "";
        this.zzcul = "";
        this.zzcum = 0;
        this.zzcun = aem.zzMi();
        this.zzcsm = -1;
    }

    public static aen zzL(byte[] bArr) throws ado {
        return (aen) adp.zza(new aen(), bArr);
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
                case 18:
                    this.zzcud = com_google_android_gms_internal_adg.readString();
                    continue;
                case 24:
                    this.zzcue = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 34:
                    this.zzcuf = com_google_android_gms_internal_adg.readString();
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzcug = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 48:
                    this.zzaLt = com_google_android_gms_internal_adg.zzLC();
                    continue;
                case 58:
                    this.zzcuh = com_google_android_gms_internal_adg.readString();
                    continue;
                case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*66*/:
                    this.zzcui = com_google_android_gms_internal_adg.readString();
                    continue;
                case 74:
                    this.zzcuj = com_google_android_gms_internal_adg.readString();
                    continue;
                case 82:
                    this.zzcuk = com_google_android_gms_internal_adg.readString();
                    continue;
                case 90:
                    this.zzcul = com_google_android_gms_internal_adg.readString();
                    continue;
                case 96:
                    this.zzcum = com_google_android_gms_internal_adg.zzLD();
                    continue;
                case 106:
                    int zzb = ads.zzb(com_google_android_gms_internal_adg, 106);
                    zzLB = this.zzcun == null ? 0 : this.zzcun.length;
                    Object obj = new aem[(zzb + zzLB)];
                    if (zzLB != 0) {
                        System.arraycopy(this.zzcun, 0, obj, 0, zzLB);
                    }
                    while (zzLB < obj.length - 1) {
                        obj[zzLB] = new aem();
                        com_google_android_gms_internal_adg.zza(obj[zzLB]);
                        com_google_android_gms_internal_adg.zzLB();
                        zzLB++;
                    }
                    obj[zzLB] = new aem();
                    com_google_android_gms_internal_adg.zza(obj[zzLB]);
                    this.zzcun = obj;
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
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            com_google_android_gms_internal_adh.zzl(2, this.zzcud);
        }
        if (this.zzcue != 0) {
            com_google_android_gms_internal_adh.zzb(3, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            com_google_android_gms_internal_adh.zzl(4, this.zzcuf);
        }
        if (this.zzcug != 0) {
            com_google_android_gms_internal_adh.zzb(5, this.zzcug);
        }
        if (this.zzaLt != 0) {
            com_google_android_gms_internal_adh.zzb(6, this.zzaLt);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            com_google_android_gms_internal_adh.zzl(7, this.zzcuh);
        }
        if (!(this.zzcui == null || this.zzcui.equals(""))) {
            com_google_android_gms_internal_adh.zzl(8, this.zzcui);
        }
        if (!(this.zzcuj == null || this.zzcuj.equals(""))) {
            com_google_android_gms_internal_adh.zzl(9, this.zzcuj);
        }
        if (!(this.zzcuk == null || this.zzcuk.equals(""))) {
            com_google_android_gms_internal_adh.zzl(10, this.zzcuk);
        }
        if (!(this.zzcul == null || this.zzcul.equals(""))) {
            com_google_android_gms_internal_adh.zzl(11, this.zzcul);
        }
        if (this.zzcum != 0) {
            com_google_android_gms_internal_adh.zzr(12, this.zzcum);
        }
        if (this.zzcun != null && this.zzcun.length > 0) {
            for (adp com_google_android_gms_internal_adp : this.zzcun) {
                if (com_google_android_gms_internal_adp != null) {
                    com_google_android_gms_internal_adh.zza(13, com_google_android_gms_internal_adp);
                }
            }
        }
        super.zza(com_google_android_gms_internal_adh);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.zzcuc == null || this.zzcuc.equals(""))) {
            zzn += adh.zzm(1, this.zzcuc);
        }
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            zzn += adh.zzm(2, this.zzcud);
        }
        if (this.zzcue != 0) {
            zzn += adh.zze(3, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            zzn += adh.zzm(4, this.zzcuf);
        }
        if (this.zzcug != 0) {
            zzn += adh.zze(5, this.zzcug);
        }
        if (this.zzaLt != 0) {
            zzn += adh.zze(6, this.zzaLt);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            zzn += adh.zzm(7, this.zzcuh);
        }
        if (!(this.zzcui == null || this.zzcui.equals(""))) {
            zzn += adh.zzm(8, this.zzcui);
        }
        if (!(this.zzcuj == null || this.zzcuj.equals(""))) {
            zzn += adh.zzm(9, this.zzcuj);
        }
        if (!(this.zzcuk == null || this.zzcuk.equals(""))) {
            zzn += adh.zzm(10, this.zzcuk);
        }
        if (!(this.zzcul == null || this.zzcul.equals(""))) {
            zzn += adh.zzm(11, this.zzcul);
        }
        if (this.zzcum != 0) {
            zzn += adh.zzs(12, this.zzcum);
        }
        if (this.zzcun == null || this.zzcun.length <= 0) {
            return zzn;
        }
        int i = zzn;
        for (adp com_google_android_gms_internal_adp : this.zzcun) {
            if (com_google_android_gms_internal_adp != null) {
                i += adh.zzb(13, com_google_android_gms_internal_adp);
            }
        }
        return i;
    }
}
