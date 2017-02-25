package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zze;
import com.stripe.android.model.Card;

public class zzatu extends zzauh {
    private String mAppId;
    private String zzVX;
    private String zzacL;
    private String zzacM;
    private String zzbqA;
    private long zzbqE;
    private long zzbsA;
    private int zzbsz;

    zzatu(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    String getGmpAppId() {
        zzob();
        return this.zzVX;
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    String zzKt() {
        zzob();
        return this.zzbqA;
    }

    long zzKu() {
        return zzKm().zzKu();
    }

    @WorkerThread
    long zzKv() {
        zzob();
        zzmR();
        if (this.zzbsA == 0) {
            this.zzbsA = this.zzbqg.zzKg().zzK(getContext(), getContext().getPackageName());
        }
        return this.zzbsA;
    }

    int zzLW() {
        zzob();
        return this.zzbsz;
    }

    protected void zzbw(Status status) {
        if (status == null) {
            zzKk().zzLX().log("GoogleService failed to initialize (no status)");
        } else {
            zzKk().zzLX().zze("GoogleService failed to initialize, status", Integer.valueOf(status.getStatusCode()), status.getStatusMessage());
        }
    }

    @WorkerThread
    zzatd zzfD(String str) {
        zzmR();
        return new zzatd(zzke(), getGmpAppId(), zzmZ(), (long) zzLW(), zzKt(), zzKu(), zzKv(), str, this.zzbqg.isEnabled(), !zzKl().zzbtu, zzKl().zzKp(), zzuW());
    }

    String zzke() {
        zzob();
        return this.mAppId;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
        String str = "unknown";
        String str2 = Card.UNKNOWN;
        int i = Integer.MIN_VALUE;
        String str3 = Card.UNKNOWN;
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzKk().zzLX().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzatx.zzfE(packageName));
        } else {
            try {
                str = packageManager.getInstallerPackageName(packageName);
            } catch (IllegalArgumentException e) {
                zzKk().zzLX().zzj("Error retrieving app installer package name. appId", zzatx.zzfE(packageName));
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
                zzKk().zzLX().zze("Error retrieving package info. appId, appName", zzatx.zzfE(packageName), str3);
            }
        }
        this.mAppId = packageName;
        this.zzbqA = str;
        this.zzacM = str2;
        this.zzbsz = i;
        this.zzacL = str3;
        this.zzbsA = 0;
        zzKm().zzLf();
        Status zzaQ = zzaba.zzaQ(getContext());
        Object obj = (zzaQ == null || !zzaQ.isSuccess()) ? null : 1;
        if (obj == null) {
            zzbw(zzaQ);
        }
        if (obj != null) {
            Boolean zzLh = zzKm().zzLh();
            if (zzKm().zzLg()) {
                zzKk().zzMb().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                obj = null;
            } else if (zzLh != null && !zzLh.booleanValue()) {
                zzKk().zzMb().log("Collection disabled with firebase_analytics_collection_enabled=0");
                obj = null;
            } else if (zzLh == null && zzKm().zzwR()) {
                zzKk().zzMb().log("Collection disabled with google_app_measurement_enable=0");
                obj = null;
            } else {
                zzKk().zzMd().log("Collection enabled");
                int i2 = 1;
            }
        } else {
            obj = null;
        }
        this.zzVX = "";
        this.zzbqE = 0;
        zzKm().zzLf();
        try {
            String zzwQ = zzaba.zzwQ();
            if (TextUtils.isEmpty(zzwQ)) {
                zzwQ = "";
            }
            this.zzVX = zzwQ;
            if (obj != null) {
                zzKk().zzMd().zze("App package, google app id", this.mAppId, this.zzVX);
            }
        } catch (IllegalStateException e3) {
            zzKk().zzLX().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzatx.zzfE(packageName), e3);
        }
    }

    String zzmZ() {
        zzob();
        return this.zzacM;
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }

    long zzuW() {
        zzob();
        return 0;
    }
}
