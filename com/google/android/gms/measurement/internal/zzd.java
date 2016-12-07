package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzrk;
import com.google.android.gms.measurement.internal.zzl.zza;
import java.lang.reflect.InvocationTargetException;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public class zzd extends zzz {
    static final String ari = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean eb;

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
        Object zzaw = zzbvy().zzaw(str, com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzaw)) {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get(Long.valueOf(Long.valueOf(zzaw).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Long.get()).longValue();
        }
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public boolean zzaef() {
        if (this.eb == null) {
            synchronized (this) {
                if (this.eb == null) {
                    ApplicationInfo applicationInfo = getContext().getApplicationInfo();
                    String zzayz = zzt.zzayz();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzayz);
                        this.eb = Boolean.valueOf(z);
                    }
                    if (this.eb == null) {
                        this.eb = Boolean.TRUE;
                        zzbwb().zzbwy().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.eb.booleanValue();
    }

    long zzaez() {
        return ((Long) zzl.asu.get()).longValue();
    }

    public String zzafe() {
        return "google_app_measurement.db";
    }

    public long zzafj() {
        return Math.max(0, ((Long) zzl.arR.get()).longValue());
    }

    public String zzao(String str, String str2) {
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzl.arT.get()).encodedAuthority((String) zzl.arU.get());
        String str3 = "config/app/";
        String valueOf = String.valueOf(str);
        encodedAuthority.path(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3)).appendQueryParameter("app_instance_id", str2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(9877));
        return builder.build().toString();
    }

    public boolean zzatu() {
        return zzrk.zzatu();
    }

    public boolean zzayi() {
        return false;
    }

    public int zzb(String str, zza<Integer> com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
        Object zzaw = zzbvy().zzaw(str, com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzaw)) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzaw).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_measurement_internal_zzl_zza_java_lang_Integer.get()).intValue();
        }
    }

    public long zzbto() {
        return 9877;
    }

    String zzbub() {
        return (String) zzl.arP.get();
    }

    public int zzbuc() {
        return 25;
    }

    public int zzbud() {
        return 32;
    }

    public int zzbue() {
        return 24;
    }

    int zzbuf() {
        return 24;
    }

    int zzbug() {
        return 36;
    }

    int zzbuh() {
        return 256;
    }

    public int zzbui() {
        return 36;
    }

    public int zzbuj() {
        return 2048;
    }

    int zzbuk() {
        return 500;
    }

    public long zzbul() {
        return (long) ((Integer) zzl.arZ.get()).intValue();
    }

    public long zzbum() {
        return (long) ((Integer) zzl.asb.get()).intValue();
    }

    int zzbun() {
        return 25;
    }

    int zzbuo() {
        return 50;
    }

    long zzbup() {
        return 3600000;
    }

    long zzbuq() {
        return ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
    }

    long zzbur() {
        return 61000;
    }

    String zzbus() {
        return "google_app_measurement_local.db";
    }

    public boolean zzbut() {
        Boolean zzlu = zzlu("firebase_analytics_collection_deactivated");
        return zzlu != null && zzlu.booleanValue();
    }

    public Boolean zzbuu() {
        return zzlu("firebase_analytics_collection_enabled");
    }

    public long zzbuv() {
        return ((Long) zzl.asr.get()).longValue();
    }

    public long zzbuw() {
        return ((Long) zzl.asm.get()).longValue();
    }

    public long zzbux() {
        return ((Long) zzl.asn.get()).longValue();
    }

    public long zzbuy() {
        return 1000;
    }

    public int zzbuz() {
        return Math.max(0, ((Integer) zzl.arX.get()).intValue());
    }

    public int zzbva() {
        return Math.max(1, ((Integer) zzl.arY.get()).intValue());
    }

    public int zzbvb() {
        return DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public String zzbvc() {
        return (String) zzl.asf.get();
    }

    public long zzbvd() {
        return ((Long) zzl.arS.get()).longValue();
    }

    public long zzbve() {
        return Math.max(0, ((Long) zzl.asg.get()).longValue());
    }

    public long zzbvf() {
        return Math.max(0, ((Long) zzl.asi.get()).longValue());
    }

    public long zzbvg() {
        return Math.max(0, ((Long) zzl.asj.get()).longValue());
    }

    public long zzbvh() {
        return Math.max(0, ((Long) zzl.ask.get()).longValue());
    }

    public long zzbvi() {
        return Math.max(0, ((Long) zzl.asl.get()).longValue());
    }

    public long zzbvj() {
        return ((Long) zzl.ash.get()).longValue();
    }

    public long zzbvk() {
        return Math.max(0, ((Long) zzl.aso.get()).longValue());
    }

    public long zzbvl() {
        return Math.max(0, ((Long) zzl.asp.get()).longValue());
    }

    public int zzbvm() {
        return Math.min(20, Math.max(0, ((Integer) zzl.asq.get()).intValue()));
    }

    public String zzbvn() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"firebase.analytics.debug-mode", ""});
        } catch (ClassNotFoundException e) {
            zzbwb().zzbwy().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzbwb().zzbwy().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzbwb().zzbwy().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzbwb().zzbwy().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
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

    public int zzlo(@Size(min = 1) String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzl.asa)));
    }

    public int zzlp(@Size(min = 1) String str) {
        return zzb(str, zzl.asc);
    }

    public int zzlq(@Size(min = 1) String str) {
        return zzb(str, zzl.asd);
    }

    long zzlr(String str) {
        return zza(str, zzl.arQ);
    }

    int zzls(String str) {
        return zzb(str, zzl.ass);
    }

    int zzlt(String str) {
        return Math.max(0, Math.min(2000, zzb(str, zzl.ast)));
    }

    @Nullable
    Boolean zzlu(@Size(min = 1) String str) {
        Boolean bool = null;
        zzaa.zzib(str);
        try {
            PackageManager packageManager = getContext().getPackageManager();
            if (packageManager == null) {
                zzbwb().zzbwy().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzbwb().zzbwy().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzbwb().zzbwy().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzbwb().zzbwy().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public int zzlv(String str) {
        return zzb(str, zzl.arV);
    }

    public int zzlw(String str) {
        return Math.max(0, zzb(str, zzl.arW));
    }

    public int zzlx(String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzl.ase)));
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }
}
