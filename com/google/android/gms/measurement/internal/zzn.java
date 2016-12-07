package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.WorkerThread;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzrk;

public class zzn extends zzaa {
    private String aqF;
    private String aqM;
    private int asv;
    private long asw;
    private String bN;
    private String bO;
    private String zzctj;

    zzn(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    String zzaaf() {
        zzacj();
        return this.bO;
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    String zzbth() {
        zzacj();
        return this.aqF;
    }

    String zzbtn() {
        zzacj();
        return this.aqM;
    }

    long zzbto() {
        return zzbwd().zzbto();
    }

    @WorkerThread
    long zzbtp() {
        zzacj();
        zzzx();
        if (this.asw == 0) {
            this.asw = this.aqw.zzbvx().zzad(getContext(), getContext().getPackageName());
        }
        return this.asw;
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    int zzbwx() {
        zzacj();
        return this.asv;
    }

    protected void zzdw(Status status) {
        if (status == null) {
            zzbwb().zzbwy().log("GoogleService failed to initialize (no status)");
        } else {
            zzbwb().zzbwy().zze("GoogleService failed to initialize, status", Integer.valueOf(status.getStatusCode()), status.getStatusMessage());
        }
    }

    @WorkerThread
    AppMetadata zzmi(String str) {
        zzzx();
        return new AppMetadata(zzup(), zzbth(), zzaaf(), (long) zzbwx(), zzbtn(), zzbto(), zzbtp(), str, this.aqw.isEnabled(), !zzbwc().atn, zzbwc().zzbtj());
    }

    String zzup() {
        zzacj();
        return this.zzctj;
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
        String str = EnvironmentCompat.MEDIA_UNKNOWN;
        String str2 = "Unknown";
        int i = Integer.MIN_VALUE;
        String str3 = "Unknown";
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzbwb().zzbwy().log("PackageManager is null, app identity information might be inaccurate");
        } else {
            try {
                str = packageManager.getInstallerPackageName(packageName);
            } catch (IllegalArgumentException e) {
                zzbwb().zzbwy().zzj("Error retrieving app installer package name", packageName);
            }
            if (str == null) {
                str = "manual_install";
            } else if ("com.android.vending".equals(str)) {
                str = "";
            }
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
                if (packageInfo != null) {
                    CharSequence applicationLabel = packageManager.getApplicationLabel(packageInfo.applicationInfo);
                    if (!TextUtils.isEmpty(applicationLabel)) {
                        str3 = applicationLabel.toString();
                    }
                    str2 = packageInfo.versionName;
                    i = packageInfo.versionCode;
                }
            } catch (NameNotFoundException e2) {
                zzbwb().zzbwy().zzj("Error retrieving package info: appName", str3);
            }
        }
        this.zzctj = packageName;
        this.aqM = str;
        this.bO = str2;
        this.asv = i;
        this.bN = str3;
        zzbwd().zzayi();
        Status zzby = zzrk.zzby(getContext());
        Object obj = (zzby == null || !zzby.isSuccess()) ? null : 1;
        if (obj == null) {
            zzdw(zzby);
        }
        if (obj != null) {
            Boolean zzbuu = zzbwd().zzbuu();
            if (zzbwd().zzbut()) {
                zzbwb().zzbxc().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                obj = null;
            } else if (zzbuu != null && !zzbuu.booleanValue()) {
                zzbwb().zzbxc().log("Collection disabled with firebase_analytics_collection_enabled=0");
                obj = null;
            } else if (zzbuu == null && zzbwd().zzatu()) {
                zzbwb().zzbxc().log("Collection disabled with google_app_measurement_enable=0");
                obj = null;
            } else {
                zzbwb().zzbxe().log("Collection enabled");
                int i2 = 1;
            }
        } else {
            obj = null;
        }
        this.aqF = "";
        if (!zzbwd().zzayi()) {
            try {
                String zzatt = zzrk.zzatt();
                if (TextUtils.isEmpty(zzatt)) {
                    zzatt = "";
                }
                this.aqF = zzatt;
                if (obj != null) {
                    zzbwb().zzbxe().zze("App package, google app id", this.zzctj, this.aqF);
                }
            } catch (IllegalStateException e3) {
                zzbwb().zzbwy().zzj("getGoogleAppId or isMeasurementEnabled failed with exception", e3);
            }
        }
    }
}
