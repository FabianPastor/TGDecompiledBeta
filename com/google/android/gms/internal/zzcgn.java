package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzs;
import java.lang.reflect.InvocationTargetException;

public final class zzcgn extends zzcjk {
    private Boolean zzdvl;

    zzcgn(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    public static long zzayb() {
        return ((Long) zzchc.zzjbg.get()).longValue();
    }

    public static long zzayc() {
        return ((Long) zzchc.zzjag.get()).longValue();
    }

    public static boolean zzaye() {
        return ((Boolean) zzchc.zzjab.get()).booleanValue();
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final long zza(String str, zzchd<Long> com_google_android_gms_internal_zzchd_java_lang_Long) {
        if (str == null) {
            return ((Long) com_google_android_gms_internal_zzchd_java_lang_Long.get()).longValue();
        }
        Object zzam = zzawv().zzam(str, com_google_android_gms_internal_zzchd_java_lang_Long.getKey());
        if (TextUtils.isEmpty(zzam)) {
            return ((Long) com_google_android_gms_internal_zzchd_java_lang_Long.get()).longValue();
        }
        try {
            return ((Long) com_google_android_gms_internal_zzchd_java_lang_Long.get(Long.valueOf(Long.valueOf(zzam).longValue()))).longValue();
        } catch (NumberFormatException e) {
            return ((Long) com_google_android_gms_internal_zzchd_java_lang_Long.get()).longValue();
        }
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

    public final boolean zzaya() {
        Boolean zziy = zziy("firebase_analytics_collection_deactivated");
        return zziy != null && zziy.booleanValue();
    }

    public final String zzayd() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{"debug.firebase.analytics.app", TtmlNode.ANONYMOUS_REGION_ID});
        } catch (ClassNotFoundException e) {
            zzawy().zzazd().zzj("Could not find SystemProperties class", e);
        } catch (NoSuchMethodException e2) {
            zzawy().zzazd().zzj("Could not find SystemProperties.get() method", e2);
        } catch (IllegalAccessException e3) {
            zzawy().zzazd().zzj("Could not access SystemProperties.get()", e3);
        } catch (InvocationTargetException e4) {
            zzawy().zzazd().zzj("SystemProperties.get() threw an exception", e4);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public final int zzb(String str, zzchd<Integer> com_google_android_gms_internal_zzchd_java_lang_Integer) {
        if (str == null) {
            return ((Integer) com_google_android_gms_internal_zzchd_java_lang_Integer.get()).intValue();
        }
        Object zzam = zzawv().zzam(str, com_google_android_gms_internal_zzchd_java_lang_Integer.getKey());
        if (TextUtils.isEmpty(zzam)) {
            return ((Integer) com_google_android_gms_internal_zzchd_java_lang_Integer.get()).intValue();
        }
        try {
            return ((Integer) com_google_android_gms_internal_zzchd_java_lang_Integer.get(Integer.valueOf(Integer.valueOf(zzam).intValue()))).intValue();
        } catch (NumberFormatException e) {
            return ((Integer) com_google_android_gms_internal_zzchd_java_lang_Integer.get()).intValue();
        }
    }

    public final int zzix(String str) {
        return zzb(str, zzchc.zzjar);
    }

    final Boolean zziy(String str) {
        Boolean bool = null;
        zzbq.zzgm(str);
        try {
            if (getContext().getPackageManager() == null) {
                zzawy().zzazd().log("Failed to load metadata: PackageManager is null");
            } else {
                ApplicationInfo applicationInfo = zzbhf.zzdb(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
                if (applicationInfo == null) {
                    zzawy().zzazd().log("Failed to load metadata: ApplicationInfo is null");
                } else if (applicationInfo.metaData == null) {
                    zzawy().zzazd().log("Failed to load metadata: Metadata bundle is null");
                } else if (applicationInfo.metaData.containsKey(str)) {
                    bool = Boolean.valueOf(applicationInfo.metaData.getBoolean(str));
                }
            }
        } catch (NameNotFoundException e) {
            zzawy().zzazd().zzj("Failed to load metadata: Package name not found", e);
        }
        return bool;
    }

    public final boolean zziz(String str) {
        return "1".equals(zzawv().zzam(str, "gaia_collection_enabled"));
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }

    public final boolean zzyp() {
        if (this.zzdvl == null) {
            synchronized (this) {
                if (this.zzdvl == null) {
                    ApplicationInfo applicationInfo = getContext().getApplicationInfo();
                    String zzamo = zzs.zzamo();
                    if (applicationInfo != null) {
                        String str = applicationInfo.processName;
                        boolean z = str != null && str.equals(zzamo);
                        this.zzdvl = Boolean.valueOf(z);
                    }
                    if (this.zzdvl == null) {
                        this.zzdvl = Boolean.TRUE;
                        zzawy().zzazd().log("My process not in the list of running processes");
                    }
                }
            }
        }
        return this.zzdvl.booleanValue();
    }
}
