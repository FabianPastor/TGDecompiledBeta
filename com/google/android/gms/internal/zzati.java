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
    static final String zzbrg = String.valueOf(zze.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean zzaeZ;

    zzati(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
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

    String zzKK() {
        return (String) zzats.zzbrP.get();
    }

    public int zzKL() {
        return 25;
    }

    public int zzKM() {
        return 40;
    }

    public int zzKN() {
        return 24;
    }

    int zzKO() {
        return 40;
    }

    int zzKP() {
        return 100;
    }

    int zzKQ() {
        return 256;
    }

    int zzKR() {
        return 1000;
    }

    public int zzKS() {
        return 36;
    }

    public int zzKT() {
        return 2048;
    }

    int zzKU() {
        return 500;
    }

    public long zzKV() {
        return (long) ((Integer) zzats.zzbrZ.get()).intValue();
    }

    public long zzKW() {
        return (long) ((Integer) zzats.zzbsb.get()).intValue();
    }

    int zzKX() {
        return 25;
    }

    int zzKY() {
        return 1000;
    }

    int zzKZ() {
        return 25;
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

    public long zzKv() {
        return 10260;
    }

    public long zzLA() {
        return Math.max(0, ((Long) zzats.zzbsp.get()).longValue());
    }

    public long zzLB() {
        return Math.max(0, ((Long) zzats.zzbsq.get()).longValue());
    }

    public int zzLC() {
        return Math.min(20, Math.max(0, ((Integer) zzats.zzbsr.get()).intValue()));
    }

    public String zzLD() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"debug.firebase.analytics.app", ""});
        } catch (ClassNotFoundException e) {
            zzKl().zzLZ().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzKl().zzLZ().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzKl().zzLZ().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzKl().zzLZ().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
    }

    int zzLa() {
        return 1000;
    }

    long zzLb() {
        return 15552000000L;
    }

    long zzLc() {
        return 15552000000L;
    }

    long zzLd() {
        return 3600000;
    }

    long zzLe() {
        return ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
    }

    long zzLf() {
        return 61000;
    }

    String zzLg() {
        return "google_app_measurement_local.db";
    }

    public boolean zzLh() {
        return false;
    }

    public boolean zzLi() {
        Boolean zzfp = zzfp("firebase_analytics_collection_deactivated");
        return zzfp != null && zzfp.booleanValue();
    }

    public Boolean zzLj() {
        return zzfp("firebase_analytics_collection_enabled");
    }

    public long zzLk() {
        return ((Long) zzats.zzbss.get()).longValue();
    }

    public long zzLl() {
        return ((Long) zzats.zzbsn.get()).longValue();
    }

    public long zzLm() {
        return ((Long) zzats.zzbso.get()).longValue();
    }

    public long zzLn() {
        return 1000;
    }

    public int zzLo() {
        return Math.max(0, ((Integer) zzats.zzbrX.get()).intValue());
    }

    public int zzLp() {
        return Math.max(1, ((Integer) zzats.zzbrY.get()).intValue());
    }

    public int zzLq() {
        return DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public String zzLr() {
        return (String) zzats.zzbsf.get();
    }

    public long zzLs() {
        return ((Long) zzats.zzbrS.get()).longValue();
    }

    public long zzLt() {
        return Math.max(0, ((Long) zzats.zzbsg.get()).longValue());
    }

    public long zzLu() {
        return Math.max(0, ((Long) zzats.zzbsi.get()).longValue());
    }

    public long zzLv() {
        return Math.max(0, ((Long) zzats.zzbsj.get()).longValue());
    }

    public long zzLw() {
        return Math.max(0, ((Long) zzats.zzbsk.get()).longValue());
    }

    public long zzLx() {
        return Math.max(0, ((Long) zzats.zzbsl.get()).longValue());
    }

    public long zzLy() {
        return Math.max(0, ((Long) zzats.zzbsm.get()).longValue());
    }

    public long zzLz() {
        return ((Long) zzats.zzbsh.get()).longValue();
    }

    public String zzP(String str, String str2) {
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzats.zzbrT.get()).encodedAuthority((String) zzats.zzbrU.get());
        String str3 = "config/app/";
        String valueOf = String.valueOf(str);
        encodedAuthority.path(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3)).appendQueryParameter("app_instance_id", str2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10260));
        return builder.build().toString();
    }

    public long zza(String str, zza<Long> com_google_android_gms_internal_zzats_zza_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_internal_zzats_zza_java_lang_Long.get()).longValue();
        }
        Object zzZ = zzKi().zzZ(str, com_google_android_gms_internal_zzats_zza_java_lang_Long.getKey());
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
        Object zzZ = zzKi().zzZ(str, com_google_android_gms_internal_zzats_zza_java_lang_Integer.getKey());
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
        return Math.max(0, Math.min(1000000, zzb(str, zzats.zzbsa)));
    }

    public int zzfk(@Size(min = 1) String str) {
        return zzb(str, zzats.zzbsc);
    }

    public int zzfl(@Size(min = 1) String str) {
        return zzb(str, zzats.zzbsd);
    }

    long zzfm(String str) {
        return zza(str, zzats.zzbrQ);
    }

    int zzfn(String str) {
        return zzb(str, zzats.zzbst);
    }

    int zzfo(String str) {
        return Math.max(0, Math.min(2000, zzb(str, zzats.zzbsu)));
    }

    @Nullable
    Boolean zzfp(@Size(min = 1) String str) {
        Boolean bool = null;
        zzac.zzdr(str);
        try {
            if (getContext().getPackageManager() == null) {
                zzKl().zzLZ().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzKl().zzLZ().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzKl().zzLZ().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzKl().zzLZ().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public int zzfq(String str) {
        return zzb(str, zzats.zzbrV);
    }

    public int zzfr(String str) {
        return Math.max(0, zzb(str, zzats.zzbrW));
    }

    public int zzfs(String str) {
        return Math.max(0, Math.min(1000000, zzb(str, zzats.zzbse)));
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
                    String zzzr = zzu.zzzr();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzzr);
                        this.zzaeZ = Boolean.valueOf(z);
                    }
                    if (this.zzaeZ == null) {
                        this.zzaeZ = Boolean.TRUE;
                        zzKl().zzLZ().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.zzaeZ.booleanValue();
    }

    long zzpq() {
        return ((Long) zzats.zzbsv.get()).longValue();
    }

    public String zzpv() {
        return "google_app_measurement.db";
    }

    public long zzpz() {
        return Math.max(0, ((Long) zzats.zzbrR.get()).longValue());
    }

    public boolean zzwR() {
        return zzaba.zzwR();
    }
}
