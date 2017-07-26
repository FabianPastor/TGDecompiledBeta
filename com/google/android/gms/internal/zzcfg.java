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
import com.google.firebase.iid.FirebaseInstanceId;
import com.stripe.android.model.Card;
import java.math.BigInteger;
import java.util.Locale;

public final class zzcfg extends zzchj {
    private String mAppId;
    private String zzXB;
    private String zzaeH;
    private String zzaeI;
    private String zzboB;
    private long zzboF;
    private int zzbqC;
    private long zzbqD;
    private int zzbqE;

    zzcfg(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    @WorkerThread
    private final String zzwK() {
        super.zzjC();
        try {
            return FirebaseInstanceId.getInstance().getId();
        } catch (IllegalStateException e) {
            super.zzwF().zzyz().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    final String getGmpAppId() {
        zzkD();
        return this.zzXB;
    }

    @WorkerThread
    final zzceh zzdV(String str) {
        super.zzjC();
        String zzhl = zzhl();
        String gmpAppId = getGmpAppId();
        zzkD();
        String str2 = this.zzaeI;
        long zzyv = (long) zzyv();
        zzkD();
        String str3 = this.zzboB;
        long zzwP = zzcem.zzwP();
        zzkD();
        super.zzjC();
        if (this.zzbqD == 0) {
            this.zzbqD = this.zzboe.zzwB().zzI(super.getContext(), super.getContext().getPackageName());
        }
        long j = this.zzbqD;
        boolean isEnabled = this.zzboe.isEnabled();
        boolean z = !super.zzwG().zzbrC;
        String zzwK = zzwK();
        zzkD();
        long zzyY = this.zzboe.zzyY();
        zzkD();
        return new zzceh(zzhl, gmpAppId, str2, zzyv, str3, zzwP, j, str, isEnabled, z, zzwK, 0, zzyY, this.zzbqE);
    }

    final String zzhl() {
        zzkD();
        return this.mAppId;
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
        int i = 1;
        String str = "unknown";
        String str2 = Card.UNKNOWN;
        int i2 = Integer.MIN_VALUE;
        String str3 = Card.UNKNOWN;
        String packageName = super.getContext().getPackageName();
        PackageManager packageManager = super.getContext().getPackageManager();
        if (packageManager == null) {
            super.zzwF().zzyx().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzcfl.zzdZ(packageName));
        } else {
            try {
                str = packageManager.getInstallerPackageName(packageName);
            } catch (IllegalArgumentException e) {
                super.zzwF().zzyx().zzj("Error retrieving app installer package name. appId", zzcfl.zzdZ(packageName));
            }
            if (str == null) {
                str = "manual_install";
            } else if ("com.android.vending".equals(str)) {
                str = "";
            }
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(super.getContext().getPackageName(), 0);
                if (packageInfo != null) {
                    CharSequence applicationLabel = packageManager.getApplicationLabel(packageInfo.applicationInfo);
                    if (!TextUtils.isEmpty(applicationLabel)) {
                        str3 = applicationLabel.toString();
                    }
                    str2 = packageInfo.versionName;
                    i2 = packageInfo.versionCode;
                }
            } catch (NameNotFoundException e2) {
                super.zzwF().zzyx().zze("Error retrieving package info. appId, appName", zzcfl.zzdZ(packageName), str3);
            }
        }
        this.mAppId = packageName;
        this.zzboB = str;
        this.zzaeI = str2;
        this.zzbqC = i2;
        this.zzaeH = str3;
        this.zzbqD = 0;
        zzcem.zzxE();
        Status zzaz = zzbdm.zzaz(super.getContext());
        int i3 = (zzaz == null || !zzaz.isSuccess()) ? 0 : 1;
        if (i3 == 0) {
            if (zzaz == null) {
                super.zzwF().zzyx().log("GoogleService failed to initialize (no status)");
            } else {
                super.zzwF().zzyx().zze("GoogleService failed to initialize, status", Integer.valueOf(zzaz.getStatusCode()), zzaz.getStatusMessage());
            }
        }
        if (i3 != 0) {
            Boolean zzdN = super.zzwH().zzdN("firebase_analytics_collection_enabled");
            if (super.zzwH().zzxF()) {
                super.zzwF().zzyB().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                i3 = 0;
            } else if (zzdN != null && !zzdN.booleanValue()) {
                super.zzwF().zzyB().log("Collection disabled with firebase_analytics_collection_enabled=0");
                i3 = 0;
            } else if (zzdN == null && zzcem.zzqB()) {
                super.zzwF().zzyB().log("Collection disabled with google_app_measurement_enable=0");
                i3 = 0;
            } else {
                super.zzwF().zzyD().log("Collection enabled");
                i3 = 1;
            }
        } else {
            i3 = 0;
        }
        this.zzXB = "";
        this.zzboF = 0;
        zzcem.zzxE();
        try {
            String zzqA = zzbdm.zzqA();
            if (TextUtils.isEmpty(zzqA)) {
                zzqA = "";
            }
            this.zzXB = zzqA;
            if (i3 != 0) {
                super.zzwF().zzyD().zze("App package, google app id", this.mAppId, this.zzXB);
            }
        } catch (IllegalStateException e3) {
            super.zzwF().zzyx().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzcfl.zzdZ(packageName), e3);
        }
        if (VERSION.SDK_INT >= 16) {
            if (!zzbgy.zzaN(super.getContext())) {
                i = 0;
            }
            this.zzbqE = i;
            return;
        }
        this.zzbqE = 0;
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }

    @WorkerThread
    final String zzyu() {
        super.zzwB().zzzt().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    final int zzyv() {
        zzkD();
        return this.zzbqC;
    }
}
