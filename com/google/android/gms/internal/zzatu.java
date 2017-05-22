package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
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
    private String zzbqv;
    private long zzbqz;
    private int zzbsw;
    private long zzbsx;
    private int zzbsy;

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

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    String zzKu() {
        zzob();
        return this.zzbqv;
    }

    long zzKv() {
        return zzKn().zzKv();
    }

    @WorkerThread
    long zzKw() {
        zzob();
        zzmR();
        if (this.zzbsx == 0) {
            this.zzbsx = this.zzbqb.zzKh().zzM(getContext(), getContext().getPackageName());
        }
        return this.zzbsx;
    }

    int zzLX() {
        zzob();
        return this.zzbsw;
    }

    int zzLY() {
        zzob();
        return this.zzbsy;
    }

    protected void zzbw(Status status) {
        if (status == null) {
            zzKl().zzLZ().log("GoogleService failed to initialize (no status)");
        } else {
            zzKl().zzLZ().zze("GoogleService failed to initialize, status", Integer.valueOf(status.getStatusCode()), status.getStatusMessage());
        }
    }

    @WorkerThread
    zzatd zzfD(String str) {
        zzmR();
        return new zzatd(zzke(), getGmpAppId(), zzmZ(), (long) zzLX(), zzKu(), zzKv(), zzKw(), str, this.zzbqb.isEnabled(), !zzKm().zzbts, zzKm().zzKq(), zzuW(), this.zzbqb.zzMF(), zzLY());
    }

    String zzke() {
        zzob();
        return this.mAppId;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
        int i = 1;
        String str = "unknown";
        String str2 = Card.UNKNOWN;
        int i2 = Integer.MIN_VALUE;
        String str3 = Card.UNKNOWN;
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzKl().zzLZ().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzatx.zzfE(packageName));
        } else {
            try {
                str = packageManager.getInstallerPackageName(packageName);
            } catch (IllegalArgumentException e) {
                zzKl().zzLZ().zzj("Error retrieving app installer package name. appId", zzatx.zzfE(packageName));
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
                    i2 = packageInfo.versionCode;
                }
            } catch (NameNotFoundException e2) {
                zzKl().zzLZ().zze("Error retrieving package info. appId, appName", zzatx.zzfE(packageName), str3);
            }
        }
        this.mAppId = packageName;
        this.zzbqv = str;
        this.zzacM = str2;
        this.zzbsw = i2;
        this.zzacL = str3;
        this.zzbsx = 0;
        zzKn().zzLh();
        Status zzaQ = zzaba.zzaQ(getContext());
        int i3 = (zzaQ == null || !zzaQ.isSuccess()) ? 0 : 1;
        if (i3 == 0) {
            zzbw(zzaQ);
        }
        if (i3 != 0) {
            Boolean zzLj = zzKn().zzLj();
            if (zzKn().zzLi()) {
                zzKl().zzMd().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                i3 = 0;
            } else if (zzLj != null && !zzLj.booleanValue()) {
                zzKl().zzMd().log("Collection disabled with firebase_analytics_collection_enabled=0");
                i3 = 0;
            } else if (zzLj == null && zzKn().zzwR()) {
                zzKl().zzMd().log("Collection disabled with google_app_measurement_enable=0");
                i3 = 0;
            } else {
                zzKl().zzMf().log("Collection enabled");
                i3 = 1;
            }
        } else {
            i3 = 0;
        }
        this.zzVX = "";
        this.zzbqz = 0;
        zzKn().zzLh();
        try {
            String zzwQ = zzaba.zzwQ();
            if (TextUtils.isEmpty(zzwQ)) {
                zzwQ = "";
            }
            this.zzVX = zzwQ;
            if (i3 != 0) {
                zzKl().zzMf().zze("App package, google app id", this.mAppId, this.zzVX);
            }
        } catch (IllegalStateException e3) {
            zzKl().zzLZ().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzatx.zzfE(packageName), e3);
        }
        if (VERSION.SDK_INT >= 16) {
            if (!zzade.zzbg(getContext())) {
                i = 0;
            }
            this.zzbsy = i;
            return;
        }
        this.zzbsy = 0;
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
