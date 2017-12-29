package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzbz;
import com.google.android.gms.common.util.zzd;
import com.google.firebase.iid.FirebaseInstanceId;
import java.math.BigInteger;
import java.util.Locale;

public final class zzchh extends zzcjl {
    private String mAppId;
    private String zzcwz;
    private String zzdqz;
    private String zzdra;
    private String zzixc;
    private long zzixg;
    private int zzjbk;
    private long zzjbl;
    private int zzjbm;

    zzchh(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final String zzaxd() {
        zzve();
        try {
            return FirebaseInstanceId.getInstance().getId();
        } catch (IllegalStateException e) {
            zzawy().zzazf().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    final String getAppId() {
        zzxf();
        return this.mAppId;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    final String getGmpAppId() {
        zzxf();
        return this.zzcwz;
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        return true;
    }

    protected final void zzayy() {
        int i = 1;
        String str = "unknown";
        String str2 = "Unknown";
        int i2 = Integer.MIN_VALUE;
        String str3 = "Unknown";
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzawy().zzazd().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzchm.zzjk(packageName));
        } else {
            try {
                str = packageManager.getInstallerPackageName(packageName);
            } catch (IllegalArgumentException e) {
                zzawy().zzazd().zzj("Error retrieving app installer package name. appId", zzchm.zzjk(packageName));
            }
            if (str == null) {
                str = "manual_install";
            } else if ("com.android.vending".equals(str)) {
                str = TtmlNode.ANONYMOUS_REGION_ID;
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
                zzawy().zzazd().zze("Error retrieving package info. appId, appName", zzchm.zzjk(packageName), str3);
            }
        }
        this.mAppId = packageName;
        this.zzixc = str;
        this.zzdra = str2;
        this.zzjbk = i2;
        this.zzdqz = str3;
        this.zzjbl = 0;
        Status zzck = zzbz.zzck(getContext());
        int i3 = (zzck == null || !zzck.isSuccess()) ? 0 : 1;
        if (i3 == 0) {
            if (zzck == null) {
                zzawy().zzazd().log("GoogleService failed to initialize (no status)");
            } else {
                zzawy().zzazd().zze("GoogleService failed to initialize, status", Integer.valueOf(zzck.getStatusCode()), zzck.getStatusMessage());
            }
        }
        if (i3 != 0) {
            Boolean zziy = zzaxa().zziy("firebase_analytics_collection_enabled");
            if (zzaxa().zzaya()) {
                zzawy().zzazh().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                i3 = 0;
            } else if (zziy != null && !zziy.booleanValue()) {
                zzawy().zzazh().log("Collection disabled with firebase_analytics_collection_enabled=0");
                i3 = 0;
            } else if (zziy == null && zzbz.zzaji()) {
                zzawy().zzazh().log("Collection disabled with google_app_measurement_enable=0");
                i3 = 0;
            } else {
                zzawy().zzazj().log("Collection enabled");
                i3 = 1;
            }
        } else {
            i3 = 0;
        }
        this.zzcwz = TtmlNode.ANONYMOUS_REGION_ID;
        this.zzixg = 0;
        try {
            String zzajh = zzbz.zzajh();
            if (TextUtils.isEmpty(zzajh)) {
                zzajh = TtmlNode.ANONYMOUS_REGION_ID;
            }
            this.zzcwz = zzajh;
            if (i3 != 0) {
                zzawy().zzazj().zze("App package, google app id", this.mAppId, this.zzcwz);
            }
        } catch (IllegalStateException e3) {
            zzawy().zzazd().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzchm.zzjk(packageName), e3);
        }
        if (VERSION.SDK_INT >= 16) {
            if (!zzbhd.zzcz(getContext())) {
                i = 0;
            }
            this.zzjbm = i;
            return;
        }
        this.zzjbm = 0;
    }

    final String zzayz() {
        zzawu().zzbaz().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    final int zzaza() {
        zzxf();
        return this.zzjbk;
    }

    final int zzazb() {
        zzxf();
        return this.zzjbm;
    }

    final zzcgi zzjg(String str) {
        zzve();
        String appId = getAppId();
        String gmpAppId = getGmpAppId();
        zzxf();
        String str2 = this.zzdra;
        long zzaza = (long) zzaza();
        zzxf();
        String str3 = this.zzixc;
        zzxf();
        zzve();
        if (this.zzjbl == 0) {
            this.zzjbl = this.zziwf.zzawu().zzaf(getContext(), getContext().getPackageName());
        }
        long j = this.zzjbl;
        boolean isEnabled = this.zziwf.isEnabled();
        boolean z = !zzawz().zzjdj;
        String zzaxd = zzaxd();
        zzxf();
        long zzbaf = this.zziwf.zzbaf();
        int zzazb = zzazb();
        Boolean zziy = zzaxa().zziy("google_analytics_adid_collection_enabled");
        boolean z2 = zziy == null || zziy.booleanValue();
        return new zzcgi(appId, gmpAppId, str2, zzaza, str3, 11910, j, str, isEnabled, z, zzaxd, 0, zzbaf, zzazb, Boolean.valueOf(z2).booleanValue());
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
