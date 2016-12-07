package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzqw;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;

public class zzn extends zzaa {
    private static final X500Principal apj = new X500Principal("CN=Android Debug,O=Android,C=US");
    private String F;
    private String G;
    private String anD;
    private String anw;
    private int apk;
    private long apl;
    private String zzcpe;

    zzn(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    String zzbsr() {
        zzaax();
        return this.anw;
    }

    String zzbsx() {
        zzaax();
        return this.anD;
    }

    long zzbsy() {
        return zzbvi().zzbsy();
    }

    long zzbsz() {
        zzaax();
        return this.apl;
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    int zzbwa() {
        zzaax();
        return this.apk;
    }

    boolean zzbwb() {
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0)) {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()))).getSubjectX500Principal().equals(apj);
            }
        } catch (CertificateException e) {
            zzbvg().zzbwc().zzj("Error obtaining certificate", e);
        } catch (NameNotFoundException e2) {
            zzbvg().zzbwc().zzj("Package name not found", e2);
        }
        return true;
    }

    protected void zzdr(Status status) {
        if (status == null) {
            zzbvg().zzbwc().log("GoogleService failed to initialize (no status)");
        } else {
            zzbvg().zzbwc().zze("GoogleService failed to initialize, status", Integer.valueOf(status.getStatusCode()), status.getStatusMessage());
        }
    }

    AppMetadata zzmi(String str) {
        return new AppMetadata(zzti(), zzbsr(), zzyt(), (long) zzbwa(), zzbsx(), zzbsy(), zzbsz(), str, this.anq.isEnabled(), !zzbvh().aqe, zzbvh().zzbst());
    }

    String zzti() {
        zzaax();
        return this.zzcpe;
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
        String str = EnvironmentCompat.MEDIA_UNKNOWN;
        String str2 = "Unknown";
        int i = Integer.MIN_VALUE;
        String str3 = "Unknown";
        String packageName = getContext().getPackageName();
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzbvg().zzbwc().log("PackageManager is null, app identity information might be inaccurate");
        } else {
            str = packageManager.getInstallerPackageName(packageName);
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
            } catch (NameNotFoundException e) {
                zzbvg().zzbwc().zzj("Error retrieving package info: appName", str3);
            }
        }
        this.zzcpe = packageName;
        this.anD = str;
        this.G = str2;
        this.apk = i;
        this.F = str3;
        this.apl = 0;
        MessageDigest zzfi = zzal.zzfi("MD5");
        if (zzfi == null) {
            zzbvg().zzbwc().log("Could not get MD5 instance");
            this.apl = -1;
        } else if (packageManager != null) {
            try {
                if (!zzbwb()) {
                    PackageInfo packageInfo2 = packageManager.getPackageInfo(getContext().getPackageName(), 64);
                    if (packageInfo2.signatures != null && packageInfo2.signatures.length > 0) {
                        this.apl = zzal.zzx(zzfi.digest(packageInfo2.signatures[0].toByteArray()));
                    }
                }
            } catch (NameNotFoundException e2) {
                zzbvg().zzbwc().zzj("Package name not found", e2);
            }
        }
        Status zzb = zzbvi().zzact() ? zzqw.zzb(getContext(), "-", true) : zzqw.zzcb(getContext());
        boolean z = zzb != null && zzb.isSuccess();
        if (!z) {
            zzdr(zzb);
        }
        if (z) {
            Boolean zzbud = zzbvi().zzbud();
            if (zzbvi().zzbuc()) {
                zzbvg().zzbwh().log("Collection disabled with firebase_analytics_collection_deactivated=1");
                z = false;
            } else if (zzbud != null && !zzbud.booleanValue()) {
                zzbvg().zzbwh().log("Collection disabled with firebase_analytics_collection_enabled=0");
                z = false;
            } else if (zzbud == null && zzbvi().zzasm()) {
                zzbvg().zzbwh().log("Collection disabled with google_app_measurement_enable=0");
                z = false;
            } else {
                zzbvg().zzbwj().log("Collection enabled");
                z = true;
            }
        } else {
            z = false;
        }
        this.anw = "";
        if (!zzbvi().zzact()) {
            try {
                String zzasl = zzqw.zzasl();
                if (TextUtils.isEmpty(zzasl)) {
                    zzasl = "";
                }
                this.anw = zzasl;
                if (z) {
                    zzbvg().zzbwj().zze("App package, google app id", this.zzcpe, this.anw);
                }
            } catch (IllegalStateException e3) {
                zzbvg().zzbwc().zzj("getGoogleAppId or isMeasurementEnabled failed with exception", e3);
            }
        }
    }

    String zzyt() {
        zzaax();
        return this.G;
    }
}
