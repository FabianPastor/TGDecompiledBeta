package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzqw;
import com.google.android.gms.measurement.internal.zzl.zza;
import java.lang.reflect.InvocationTargetException;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public class zzd extends zzz {
    static final String anZ = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean bU;

    zzd(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public long zza(String str, zza<Long> com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get()).longValue();
        }
        Object zzaw = zzbvd().zzaw(str, com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzaw)) {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get(Long.valueOf(Long.valueOf(zzaw).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get()).longValue();
        }
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    public boolean zzact() {
        return false;
    }

    public boolean zzacu() {
        if (this.bU == null) {
            synchronized (this) {
                if (this.bU == null) {
                    ApplicationInfo applicationInfo = getContext().getApplicationInfo();
                    String zzaxy = zzt.zzaxy();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzaxy);
                        this.bU = Boolean.valueOf(z);
                    }
                    if (this.bU == null) {
                        this.bU = Boolean.TRUE;
                        zzbvg().zzbwc().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.bU.booleanValue();
    }

    long zzado() {
        return ((Long) zzl.api.get()).longValue();
    }

    public String zzadt() {
        return "google_app_measurement.db";
    }

    public String zzadu() {
        return "google_app_measurement2.db";
    }

    public long zzadz() {
        return Math.max(0, ((Long) zzl.aoG.get()).longValue());
    }

    public String zzap(String str, String str2) {
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzl.aoI.get()).encodedAuthority((String) zzl.aoJ.get());
        String str3 = "config/app/";
        String valueOf = String.valueOf(str);
        encodedAuthority.path(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3)).appendQueryParameter("app_instance_id", str2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(zzbsy()));
        return builder.build().toString();
    }

    public boolean zzasm() {
        return zzqw.zzasm();
    }

    public int zzb(String str, zza<Integer> com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
        Object zzaw = zzbvd().zzaw(str, com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzaw)) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzaw).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
    }

    public long zzbsy() {
        return 9683;
    }

    String zzbtl() {
        return (String) zzl.aoE.get();
    }

    public int zzbtm() {
        return 25;
    }

    public int zzbtn() {
        return 32;
    }

    public int zzbto() {
        return 24;
    }

    int zzbtp() {
        return 24;
    }

    int zzbtq() {
        return 36;
    }

    int zzbtr() {
        return 256;
    }

    public int zzbts() {
        return 36;
    }

    public int zzbtt() {
        return 2048;
    }

    int zzbtu() {
        return 500;
    }

    public long zzbtv() {
        return (long) ((Integer) zzl.aoO.get()).intValue();
    }

    public long zzbtw() {
        return (long) ((Integer) zzl.aoQ.get()).intValue();
    }

    int zzbtx() {
        return 25;
    }

    int zzbty() {
        return 50;
    }

    long zzbtz() {
        return 3600000;
    }

    long zzbua() {
        return HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS;
    }

    long zzbub() {
        return 61000;
    }

    public boolean zzbuc() {
        if (zzact()) {
            return false;
        }
        Boolean zzlu = zzlu("firebase_analytics_collection_deactivated");
        return zzlu != null && zzlu.booleanValue();
    }

    public Boolean zzbud() {
        return zzact() ? null : zzlu("firebase_analytics_collection_enabled");
    }

    public long zzbue() {
        return ((Long) zzl.apf.get()).longValue();
    }

    public long zzbuf() {
        return ((Long) zzl.apb.get()).longValue();
    }

    public long zzbug() {
        return 1000;
    }

    public int zzbuh() {
        return Math.max(0, ((Integer) zzl.aoM.get()).intValue());
    }

    public int zzbui() {
        return Math.max(1, ((Integer) zzl.aoN.get()).intValue());
    }

    public String zzbuj() {
        return (String) zzl.aoU.get();
    }

    public long zzbuk() {
        return ((Long) zzl.aoH.get()).longValue();
    }

    public long zzbul() {
        return Math.max(0, ((Long) zzl.aoV.get()).longValue());
    }

    public long zzbum() {
        return Math.max(0, ((Long) zzl.aoX.get()).longValue());
    }

    public long zzbun() {
        return Math.max(0, ((Long) zzl.aoY.get()).longValue());
    }

    public long zzbuo() {
        return Math.max(0, ((Long) zzl.aoZ.get()).longValue());
    }

    public long zzbup() {
        return Math.max(0, ((Long) zzl.apa.get()).longValue());
    }

    public long zzbuq() {
        return ((Long) zzl.aoW.get()).longValue();
    }

    public long zzbur() {
        return Math.max(0, ((Long) zzl.apc.get()).longValue());
    }

    public long zzbus() {
        return Math.max(0, ((Long) zzl.apd.get()).longValue());
    }

    public int zzbut() {
        return Math.min(20, Math.max(0, ((Integer) zzl.ape.get()).intValue()));
    }

    public String zzbuu() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"firebase.analytics.debug-mode", ""});
        } catch (ClassNotFoundException e) {
            zzbvg().zzbwc().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzbvg().zzbwc().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzbvg().zzbwc().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzbvg().zzbwc().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
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

    public int zzlo(@Size(min = 1) String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzl.aoP)));
    }

    public int zzlp(@Size(min = 1) String str) {
        return zzb(str, zzl.aoR);
    }

    public int zzlq(@Size(min = 1) String str) {
        return zzb(str, zzl.aoS);
    }

    long zzlr(String str) {
        return zza(str, zzl.aoF);
    }

    int zzls(String str) {
        return zzb(str, zzl.apg);
    }

    int zzlt(String str) {
        return Math.max(0, Math.min(2000, zzb(str, zzl.aph)));
    }

    @Nullable
    Boolean zzlu(@Size(min = 1) String str) {
        Boolean bool = null;
        zzac.zzhz(str);
        try {
            PackageManager packageManager = getContext().getPackageManager();
            if (packageManager == null) {
                zzbvg().zzbwc().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzbvg().zzbwc().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzbvg().zzbwc().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzbvg().zzbwc().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public int zzlv(String str) {
        return zzb(str, zzl.aoK);
    }

    public int zzlw(String str) {
        return Math.max(0, zzb(str, zzl.aoL));
    }

    public int zzlx(String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzl.aoT)));
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }
}
