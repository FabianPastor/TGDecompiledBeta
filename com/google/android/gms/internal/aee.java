package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.IOException;
import org.telegram.messenger.SecretChatHelper;

public final class aee extends adg {
    public long zzaLt;
    public String zzctY;
    public String zzctZ;
    public long zzcua;
    public String zzcub;
    public long zzcuc;
    public String zzcud;
    public String zzcue;
    public String zzcuf;
    public String zzcug;
    public String zzcuh;
    public int zzcui;
    public aed[] zzcuj;

    public aee() {
        this.zzctY = "";
        this.zzctZ = "";
        this.zzcua = 0;
        this.zzcub = "";
        this.zzcuc = 0;
        this.zzaLt = 0;
        this.zzcud = "";
        this.zzcue = "";
        this.zzcuf = "";
        this.zzcug = "";
        this.zzcuh = "";
        this.zzcui = 0;
        this.zzcuj = aed.zzMf();
        this.zzcsi = -1;
    }

    public static aee zzL(byte[] bArr) throws adf {
        return (aee) adg.zza(new aee(), bArr);
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
                case 18:
                    this.zzctZ = com_google_android_gms_internal_acx.readString();
                    continue;
                case 24:
                    this.zzcua = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 34:
                    this.zzcub = com_google_android_gms_internal_acx.readString();
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    this.zzcuc = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 48:
                    this.zzaLt = com_google_android_gms_internal_acx.zzLz();
                    continue;
                case 58:
                    this.zzcud = com_google_android_gms_internal_acx.readString();
                    continue;
                case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*66*/:
                    this.zzcue = com_google_android_gms_internal_acx.readString();
                    continue;
                case 74:
                    this.zzcuf = com_google_android_gms_internal_acx.readString();
                    continue;
                case 82:
                    this.zzcug = com_google_android_gms_internal_acx.readString();
                    continue;
                case 90:
                    this.zzcuh = com_google_android_gms_internal_acx.readString();
                    continue;
                case 96:
                    this.zzcui = com_google_android_gms_internal_acx.zzLA();
                    continue;
                case 106:
                    int zzb = adj.zzb(com_google_android_gms_internal_acx, 106);
                    zzLy = this.zzcuj == null ? 0 : this.zzcuj.length;
                    Object obj = new aed[(zzb + zzLy)];
                    if (zzLy != 0) {
                        System.arraycopy(this.zzcuj, 0, obj, 0, zzLy);
                    }
                    while (zzLy < obj.length - 1) {
                        obj[zzLy] = new aed();
                        com_google_android_gms_internal_acx.zza(obj[zzLy]);
                        com_google_android_gms_internal_acx.zzLy();
                        zzLy++;
                    }
                    obj[zzLy] = new aed();
                    com_google_android_gms_internal_acx.zza(obj[zzLy]);
                    this.zzcuj = obj;
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
        if (!(this.zzctZ == null || this.zzctZ.equals(""))) {
            com_google_android_gms_internal_acy.zzl(2, this.zzctZ);
        }
        if (this.zzcua != 0) {
            com_google_android_gms_internal_acy.zzb(3, this.zzcua);
        }
        if (!(this.zzcub == null || this.zzcub.equals(""))) {
            com_google_android_gms_internal_acy.zzl(4, this.zzcub);
        }
        if (this.zzcuc != 0) {
            com_google_android_gms_internal_acy.zzb(5, this.zzcuc);
        }
        if (this.zzaLt != 0) {
            com_google_android_gms_internal_acy.zzb(6, this.zzaLt);
        }
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            com_google_android_gms_internal_acy.zzl(7, this.zzcud);
        }
        if (!(this.zzcue == null || this.zzcue.equals(""))) {
            com_google_android_gms_internal_acy.zzl(8, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            com_google_android_gms_internal_acy.zzl(9, this.zzcuf);
        }
        if (!(this.zzcug == null || this.zzcug.equals(""))) {
            com_google_android_gms_internal_acy.zzl(10, this.zzcug);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            com_google_android_gms_internal_acy.zzl(11, this.zzcuh);
        }
        if (this.zzcui != 0) {
            com_google_android_gms_internal_acy.zzr(12, this.zzcui);
        }
        if (this.zzcuj != null && this.zzcuj.length > 0) {
            for (adg com_google_android_gms_internal_adg : this.zzcuj) {
                if (com_google_android_gms_internal_adg != null) {
                    com_google_android_gms_internal_acy.zza(13, com_google_android_gms_internal_adg);
                }
            }
        }
        super.zza(com_google_android_gms_internal_acy);
    }

    protected final int zzn() {
        int zzn = super.zzn();
        if (!(this.zzctY == null || this.zzctY.equals(""))) {
            zzn += acy.zzm(1, this.zzctY);
        }
        if (!(this.zzctZ == null || this.zzctZ.equals(""))) {
            zzn += acy.zzm(2, this.zzctZ);
        }
        if (this.zzcua != 0) {
            zzn += acy.zze(3, this.zzcua);
        }
        if (!(this.zzcub == null || this.zzcub.equals(""))) {
            zzn += acy.zzm(4, this.zzcub);
        }
        if (this.zzcuc != 0) {
            zzn += acy.zze(5, this.zzcuc);
        }
        if (this.zzaLt != 0) {
            zzn += acy.zze(6, this.zzaLt);
        }
        if (!(this.zzcud == null || this.zzcud.equals(""))) {
            zzn += acy.zzm(7, this.zzcud);
        }
        if (!(this.zzcue == null || this.zzcue.equals(""))) {
            zzn += acy.zzm(8, this.zzcue);
        }
        if (!(this.zzcuf == null || this.zzcuf.equals(""))) {
            zzn += acy.zzm(9, this.zzcuf);
        }
        if (!(this.zzcug == null || this.zzcug.equals(""))) {
            zzn += acy.zzm(10, this.zzcug);
        }
        if (!(this.zzcuh == null || this.zzcuh.equals(""))) {
            zzn += acy.zzm(11, this.zzcuh);
        }
        if (this.zzcui != 0) {
            zzn += acy.zzs(12, this.zzcui);
        }
        if (this.zzcuj == null || this.zzcuj.length <= 0) {
            return zzn;
        }
        int i = zzn;
        for (adg com_google_android_gms_internal_adg : this.zzcuj) {
            if (com_google_android_gms_internal_adg != null) {
                i += acy.zzb(13, com_google_android_gms_internal_adg);
            }
        }
        return i;
    }
}
