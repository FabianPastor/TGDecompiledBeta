package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzatd.zza;
import java.lang.reflect.InvocationTargetException;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public class zzast extends zzatr {
    static final String zzbqo = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean zzadY;

    zzast(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public long zzJD() {
        return 10084;
    }

    String zzJS() {
        return (String) zzatd.zzbqV.get();
    }

    public int zzJT() {
        return 25;
    }

    public int zzJU() {
        return 40;
    }

    public int zzJV() {
        return 24;
    }

    int zzJW() {
        return 40;
    }

    int zzJX() {
        return 100;
    }

    int zzJY() {
        return 256;
    }

    public int zzJZ() {
        return 36;
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    public long zzKA() {
        return Math.max(0, ((Long) zzatd.zzbrr.get()).longValue());
    }

    public long zzKB() {
        return ((Long) zzatd.zzbrn.get()).longValue();
    }

    public long zzKC() {
        return Math.max(0, ((Long) zzatd.zzbru.get()).longValue());
    }

    public long zzKD() {
        return Math.max(0, ((Long) zzatd.zzbrv.get()).longValue());
    }

    public int zzKE() {
        return Math.min(20, Math.max(0, ((Integer) zzatd.zzbrw.get()).intValue()));
    }

    public String zzKF() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"debug.firebase.analytics.app", ""});
        } catch (ClassNotFoundException e) {
            zzJt().zzLa().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzJt().zzLa().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzJt().zzLa().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzJt().zzLa().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
    }

    public int zzKa() {
        return 2048;
    }

    int zzKb() {
        return 500;
    }

    public long zzKc() {
        return (long) ((Integer) zzatd.zzbrf.get()).intValue();
    }

    public long zzKd() {
        return (long) ((Integer) zzatd.zzbrh.get()).intValue();
    }

    int zzKe() {
        return 25;
    }

    int zzKf() {
        return 50;
    }

    long zzKg() {
        return 3600000;
    }

    long zzKh() {
        return ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
    }

    long zzKi() {
        return 61000;
    }

    String zzKj() {
        return "google_app_measurement_local.db";
    }

    public boolean zzKk() {
        return false;
    }

    public boolean zzKl() {
        Boolean zzft = zzft("firebase_analytics_collection_deactivated");
        return zzft != null && zzft.booleanValue();
    }

    public Boolean zzKm() {
        return zzft("firebase_analytics_collection_enabled");
    }

    public long zzKn() {
        return ((Long) zzatd.zzbrx.get()).longValue();
    }

    public long zzKo() {
        return ((Long) zzatd.zzbrs.get()).longValue();
    }

    public long zzKp() {
        return ((Long) zzatd.zzbrt.get()).longValue();
    }

    public long zzKq() {
        return 1000;
    }

    public int zzKr() {
        return Math.max(0, ((Integer) zzatd.zzbrd.get()).intValue());
    }

    public int zzKs() {
        return Math.max(1, ((Integer) zzatd.zzbre.get()).intValue());
    }

    public int zzKt() {
        return DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public String zzKu() {
        return (String) zzatd.zzbrl.get();
    }

    public long zzKv() {
        return ((Long) zzatd.zzbqY.get()).longValue();
    }

    public long zzKw() {
        return Math.max(0, ((Long) zzatd.zzbrm.get()).longValue());
    }

    public long zzKx() {
        return Math.max(0, ((Long) zzatd.zzbro.get()).longValue());
    }

    public long zzKy() {
        return Math.max(0, ((Long) zzatd.zzbrp.get()).longValue());
    }

    public long zzKz() {
        return Math.max(0, ((Long) zzatd.zzbrq.get()).longValue());
    }

    public String zzO(String str, String str2) {
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzatd.zzbqZ.get()).encodedAuthority((String) zzatd.zzbra.get());
        String str3 = "config/app/";
        String valueOf = String.valueOf(str);
        encodedAuthority.path(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3)).appendQueryParameter("app_instance_id", str2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10084));
        return builder.build().toString();
    }

    public long zza(String str, zza<Long> com_google_android_gms_internal_zzatd_zza_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_internal_zzatd_zza_java_lang_Long.get()).longValue();
        }
        Object zzW = zzJq().zzW(str, com_google_android_gms_internal_zzatd_zza_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzW)) {
            return ((Long) com_google_android_gms_internal_zzatd_zza_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_internal_zzatd_zza_java_lang_Long.get(Long.valueOf(Long.valueOf(zzW).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_internal_zzatd_zza_java_lang_Long.get()).longValue();
        }
    }

    public int zzb(String str, zza<Integer> com_google_android_gms_internal_zzatd_zza_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_internal_zzatd_zza_java_lang_Integer.get()).intValue();
        }
        Object zzW = zzJq().zzW(str, com_google_android_gms_internal_zzatd_zza_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzW)) {
            return ((Integer) com_google_android_gms_internal_zzatd_zza_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_internal_zzatd_zza_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzW).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_internal_zzatd_zza_java_lang_Integer.get()).intValue();
        }
    }

    public int zzfn(@Size(min = 1) String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzatd.zzbrg)));
    }

    public int zzfo(@Size(min = 1) String str) {
        return zzb(str, zzatd.zzbri);
    }

    public int zzfp(@Size(min = 1) String str) {
        return zzb(str, zzatd.zzbrj);
    }

    long zzfq(String str) {
        return zza(str, zzatd.zzbqW);
    }

    int zzfr(String str) {
        return zzb(str, zzatd.zzbry);
    }

    int zzfs(String str) {
        return Math.max(0, Math.min(2000, zzb(str, zzatd.zzbrz)));
    }

    @Nullable
    Boolean zzft(@Size(min = 1) String str) {
        Boolean bool = null;
        zzac.zzdv(str);
        try {
            if (getContext().getPackageManager() == null) {
                zzJt().zzLa().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = zzacx.zzaQ(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzJt().zzLa().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzJt().zzLa().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzJt().zzLa().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public int zzfu(String str) {
        return zzb(str, zzatd.zzbrb);
    }

    public int zzfv(String str) {
        return Math.max(0, zzb(str, zzatd.zzbrc));
    }

    public int zzfw(String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzatd.zzbrk)));
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }

    long zzoQ() {
        return ((Long) zzatd.zzbrA.get()).longValue();
    }

    public String zzoV() {
        return "google_app_measurement.db";
    }

    public long zzoZ() {
        return Math.max(0, ((Long) zzatd.zzbqX.get()).longValue());
    }

    public boolean zzow() {
        if (this.zzadY == null) {
            synchronized (this) {
                if (this.zzadY == null) {
                    ApplicationInfo applicationInfo = getContext().getApplicationInfo();
                    String zzyK = zzt.zzyK();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzyK);
                        this.zzadY = Boolean.valueOf(z);
                    }
                    if (this.zzadY == null) {
                        this.zzadY = Boolean.TRUE;
                        zzJt().zzLa().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.zzadY.booleanValue();
    }

    public boolean zzwk() {
        return zzaas.zzwk();
    }
}
