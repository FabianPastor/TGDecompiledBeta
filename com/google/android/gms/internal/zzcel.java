package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzr;
import com.google.android.gms.common.zze;
import java.lang.reflect.InvocationTargetException;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public final class zzcel extends zzchh {
    private static String zzbpm = String.valueOf(zze.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
    private Boolean zzagU;

    zzcel(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    public static boolean zzqB() {
        return zzbdl.zzqB();
    }

    public static long zzwP() {
        return 11011;
    }

    static long zzxA() {
        return 61000;
    }

    static long zzxB() {
        return ((Long) zzcfa.zzbqB.get()).longValue();
    }

    public static String zzxC() {
        return "google_app_measurement.db";
    }

    static String zzxD() {
        return "google_app_measurement_local.db";
    }

    public static boolean zzxE() {
        return false;
    }

    public static long zzxG() {
        return ((Long) zzcfa.zzbqy.get()).longValue();
    }

    public static long zzxH() {
        return ((Long) zzcfa.zzbqt.get()).longValue();
    }

    public static long zzxI() {
        return ((Long) zzcfa.zzbqu.get()).longValue();
    }

    public static long zzxJ() {
        return 1000;
    }

    public static long zzxK() {
        return Math.max(0, ((Long) zzcfa.zzbpX.get()).longValue());
    }

    public static int zzxL() {
        return Math.max(0, ((Integer) zzcfa.zzbqd.get()).intValue());
    }

    public static int zzxM() {
        return Math.max(1, ((Integer) zzcfa.zzbqe.get()).intValue());
    }

    public static int zzxN() {
        return DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public static String zzxO() {
        return (String) zzcfa.zzbql.get();
    }

    public static long zzxP() {
        return ((Long) zzcfa.zzbpY.get()).longValue();
    }

    public static long zzxQ() {
        return Math.max(0, ((Long) zzcfa.zzbqm.get()).longValue());
    }

    public static long zzxR() {
        return Math.max(0, ((Long) zzcfa.zzbqo.get()).longValue());
    }

    public static long zzxS() {
        return Math.max(0, ((Long) zzcfa.zzbqp.get()).longValue());
    }

    public static long zzxT() {
        return Math.max(0, ((Long) zzcfa.zzbqq.get()).longValue());
    }

    public static long zzxU() {
        return Math.max(0, ((Long) zzcfa.zzbqr.get()).longValue());
    }

    public static long zzxV() {
        return Math.max(0, ((Long) zzcfa.zzbqs.get()).longValue());
    }

    public static long zzxW() {
        return ((Long) zzcfa.zzbqn.get()).longValue();
    }

    public static long zzxX() {
        return Math.max(0, ((Long) zzcfa.zzbqv.get()).longValue());
    }

    public static long zzxY() {
        return Math.max(0, ((Long) zzcfa.zzbqw.get()).longValue());
    }

    public static int zzxZ() {
        return Math.min(20, Math.max(0, ((Integer) zzcfa.zzbqx.get()).intValue()));
    }

    static String zzxf() {
        return (String) zzcfa.zzbpV.get();
    }

    public static int zzxg() {
        return 25;
    }

    public static int zzxh() {
        return 40;
    }

    public static int zzxi() {
        return 24;
    }

    static int zzxj() {
        return 40;
    }

    static int zzxk() {
        return 100;
    }

    static int zzxl() {
        return 256;
    }

    static int zzxm() {
        return 1000;
    }

    public static int zzxn() {
        return 36;
    }

    public static int zzxo() {
        return 2048;
    }

    static int zzxp() {
        return 500;
    }

    public static long zzxq() {
        return (long) ((Integer) zzcfa.zzbqf.get()).intValue();
    }

    public static long zzxr() {
        return (long) ((Integer) zzcfa.zzbqh.get()).intValue();
    }

    static int zzxs() {
        return 25;
    }

    static int zzxt() {
        return 1000;
    }

    static int zzxu() {
        return 25;
    }

    static int zzxv() {
        return 1000;
    }

    static long zzxw() {
        return 15552000000L;
    }

    static long zzxx() {
        return 15552000000L;
    }

    static long zzxy() {
        return 3600000;
    }

    static long zzxz() {
        return ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
    }

    public static boolean zzyb() {
        return ((Boolean) zzcfa.zzbpU.get()).booleanValue();
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final long zza(String str, zzcfb<Long> com_google_android_gms_internal_zzcfb_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_internal_zzcfb_java_lang_Long.get()).longValue();
        }
        Object zzM = super.zzwC().zzM(str, com_google_android_gms_internal_zzcfb_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzM)) {
            return ((Long) com_google_android_gms_internal_zzcfb_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_internal_zzcfb_java_lang_Long.get(Long.valueOf(Long.valueOf(zzM).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_internal_zzcfb_java_lang_Long.get()).longValue();
        }
    }

    public final int zzb(String str, zzcfb<Integer> com_google_android_gms_internal_zzcfb_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_internal_zzcfb_java_lang_Integer.get()).intValue();
        }
        Object zzM = super.zzwC().zzM(str, com_google_android_gms_internal_zzcfb_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzM)) {
            return ((Integer) com_google_android_gms_internal_zzcfb_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_internal_zzcfb_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzM).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_internal_zzcfb_java_lang_Integer.get()).intValue();
        }
    }

    public final int zzdM(@Size(min = 1) String str) {
        return zzb(str, zzcfa.zzbqj);
    }

    @Nullable
    final Boolean zzdN(@Size(min = 1) String str) {
        Boolean bool = null;
        zzbo.zzcF(str);
        try {
            if (super.getContext().getPackageManager() == null) {
                super.zzwF().zzyx().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = zzbgz.zzaP(super.getContext()).getApplicationInfo(super.getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    super.zzwF().zzyx().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    super.zzwF().zzyx().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            super.zzwF().zzyx().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public final boolean zzdO(String str) {
        return "1".equals(super.zzwC().zzM(str, "gaia_collection_enabled"));
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    public final /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zzkq() {
        return super.zzkq();
    }

    public final boolean zzln() {
        if (this.zzagU == null) {
            synchronized (this) {
                if (this.zzagU == null) {
                    ApplicationInfo applicationInfo = super.getContext().getApplicationInfo();
                    String zzsf = zzr.zzsf();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzsf);
                        this.zzagU = Boolean.valueOf(z);
                    }
                    if (this.zzagU == null) {
                        this.zzagU = Boolean.TRUE;
                        super.zzwF().zzyx().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.zzagU.booleanValue();
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
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

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }

    public final boolean zzxF() {
        Boolean zzdN = zzdN("firebase_analytics_collection_deactivated");
        return zzdN != null && zzdN.booleanValue();
    }

    public final String zzya() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"debug.firebase.analytics.app", ""});
        } catch (ClassNotFoundException e) {
            super.zzwF().zzyx().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            super.zzwF().zzyx().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            super.zzwF().zzyx().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            super.zzwF().zzyx().zzj("SystemProperties.get() threw an exception", e4);
        }
        return "";
    }
}
