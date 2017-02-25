package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzu;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzats.zza;
import java.lang.reflect.InvocationTargetException;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public class zzati extends zzaug {
    static final String zzbrj = String.valueOf(zze.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean zzaeZ;

    zzati(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
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

    String zzKJ() {
        return (String) zzats.zzbrS.get();
    }

    public int zzKK() {
        return 25;
    }

    public int zzKL() {
        return 40;
    }

    public int zzKM() {
        return 24;
    }

    int zzKN() {
        return 40;
    }

    int zzKO() {
        return 100;
    }

    int zzKP() {
        return 256;
    }

    public int zzKQ() {
        return 36;
    }

    public int zzKR() {
        return 2048;
    }

    int zzKS() {
        return 500;
    }

    public long zzKT() {
        return (long) ((Integer) zzats.zzbsc.get()).intValue();
    }

    public long zzKU() {
        return (long) ((Integer) zzats.zzbse.get()).intValue();
    }

    int zzKV() {
        return 25;
    }

    int zzKW() {
        return 1000;
    }

    int zzKX() {
        return 25;
    }

    int zzKY() {
        return 1000;
    }

    long zzKZ() {
        return 15552000000L;
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

    public long zzKu() {
        return 10298;
    }

    public int zzLA() {
        return Math.min(20, Math.max(0, ((Integer) zzats.zzbsu.get()).intValue()));
    }

    public String zzLB() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"debug.firebase.analytics.app", ""});
        } catch (ClassNotFoundException e) {
            zzKk().zzLX().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzKk().zzLX().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzKk().zzLX().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzKk().zzLX().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
    }

    long zzLa() {
        return 15552000000L;
    }

    long zzLb() {
        return 3600000;
    }

    long zzLc() {
        return ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
    }

    long zzLd() {
        return 61000;
    }

    String zzLe() {
        return "google_app_measurement_local.db";
    }

    public boolean zzLf() {
        return false;
    }

    public boolean zzLg() {
        Boolean zzfp = zzfp("firebase_analytics_collection_deactivated");
        return zzfp != null && zzfp.booleanValue();
    }

    public Boolean zzLh() {
        return zzfp("firebase_analytics_collection_enabled");
    }

    public long zzLi() {
        return ((Long) zzats.zzbsv.get()).longValue();
    }

    public long zzLj() {
        return ((Long) zzats.zzbsq.get()).longValue();
    }

    public long zzLk() {
        return ((Long) zzats.zzbsr.get()).longValue();
    }

    public long zzLl() {
        return 1000;
    }

    public int zzLm() {
        return Math.max(0, ((Integer) zzats.zzbsa.get()).intValue());
    }

    public int zzLn() {
        return Math.max(1, ((Integer) zzats.zzbsb.get()).intValue());
    }

    public int zzLo() {
        return DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public String zzLp() {
        return (String) zzats.zzbsi.get();
    }

    public long zzLq() {
        return ((Long) zzats.zzbrV.get()).longValue();
    }

    public long zzLr() {
        return Math.max(0, ((Long) zzats.zzbsj.get()).longValue());
    }

    public long zzLs() {
        return Math.max(0, ((Long) zzats.zzbsl.get()).longValue());
    }

    public long zzLt() {
        return Math.max(0, ((Long) zzats.zzbsm.get()).longValue());
    }

    public long zzLu() {
        return Math.max(0, ((Long) zzats.zzbsn.get()).longValue());
    }

    public long zzLv() {
        return Math.max(0, ((Long) zzats.zzbso.get()).longValue());
    }

    public long zzLw() {
        return Math.max(0, ((Long) zzats.zzbsp.get()).longValue());
    }

    public long zzLx() {
        return ((Long) zzats.zzbsk.get()).longValue();
    }

    public long zzLy() {
        return Math.max(0, ((Long) zzats.zzbss.get()).longValue());
    }

    public long zzLz() {
        return Math.max(0, ((Long) zzats.zzbst.get()).longValue());
    }

    public String zzP(String str, String str2) {
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzats.zzbrW.get()).encodedAuthority((String) zzats.zzbrX.get());
        String str3 = "config/app/";
        String valueOf = String.valueOf(str);
        encodedAuthority.path(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3)).appendQueryParameter("app_instance_id", str2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10298));
        return builder.build().toString();
    }

    public long zza(String str, zza<Long> com_google_android_gms_internal_zzats_zza_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_internal_zzats_zza_java_lang_Long.get()).longValue();
        }
        Object zzZ = zzKh().zzZ(str, com_google_android_gms_internal_zzats_zza_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzZ)) {
            return ((Long) com_google_android_gms_internal_zzats_zza_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_internal_zzats_zza_java_lang_Long.get(Long.valueOf(Long.valueOf(zzZ).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_internal_zzats_zza_java_lang_Long.get()).longValue();
        }
    }

    public int zzb(String str, zza<Integer> com_google_android_gms_internal_zzats_zza_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_internal_zzats_zza_java_lang_Integer.get()).intValue();
        }
        Object zzZ = zzKh().zzZ(str, com_google_android_gms_internal_zzats_zza_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzZ)) {
            return ((Integer) com_google_android_gms_internal_zzats_zza_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_internal_zzats_zza_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzZ).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_internal_zzats_zza_java_lang_Integer.get()).intValue();
        }
    }

    public int zzfj(@Size(min = 1) String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzats.zzbsd)));
    }

    public int zzfk(@Size(min = 1) String str) {
        return zzb(str, zzats.zzbsf);
    }

    public int zzfl(@Size(min = 1) String str) {
        return zzb(str, zzats.zzbsg);
    }

    long zzfm(String str) {
        return zza(str, zzats.zzbrT);
    }

    int zzfn(String str) {
        return zzb(str, zzats.zzbsw);
    }

    int zzfo(String str) {
        return Math.max(0, Math.min(2000, zzb(str, zzats.zzbsx)));
    }

    @Nullable
    Boolean zzfp(@Size(min = 1) String str) {
        Boolean bool = null;
        zzac.zzdr(str);
        try {
            if (getContext().getPackageManager() == null) {
                zzKk().zzLX().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzKk().zzLX().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzKk().zzLX().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzKk().zzLX().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public int zzfq(String str) {
        return zzb(str, zzats.zzbrY);
    }

    public int zzfr(String str) {
        return Math.max(0, zzb(str, zzats.zzbrZ));
    }

    public int zzfs(String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzats.zzbsh)));
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zznR() {
        return super.zznR();
    }

    public boolean zzoW() {
        if (this.zzaeZ == null) {
            synchronized (this) {
                if (this.zzaeZ == null) {
                    ApplicationInfo applicationInfo = getContext().getApplicationInfo();
                    String zzzq = zzu.zzzq();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzzq);
                        this.zzaeZ = Boolean.valueOf(z);
                    }
                    if (this.zzaeZ == null) {
                        this.zzaeZ = Boolean.TRUE;
                        zzKk().zzLX().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.zzaeZ.booleanValue();
    }

    long zzpq() {
        return ((Long) zzats.zzbsy.get()).longValue();
    }

    public String zzpv() {
        return "google_app_measurement.db";
    }

    public long zzpz() {
        return Math.max(0, ((Long) zzats.zzbrU.get()).longValue());
    }

    public boolean zzwR() {
        return zzaba.zzwR();
    }
}
