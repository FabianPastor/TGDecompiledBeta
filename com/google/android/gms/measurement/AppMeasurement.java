package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.measurement.internal.UserAttributeParcel;
import com.google.android.gms.measurement.internal.zzx;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.BuildConfig;

@Deprecated
public class AppMeasurement {
    private final zzx aqw;

    public interface zzb {
        @WorkerThread
        void zzb(String str, String str2, Bundle bundle, long j);
    }

    public interface zzc {
        @WorkerThread
        void zzc(String str, String str2, Bundle bundle, long j);
    }

    public interface zzd {
        @MainThread
        boolean zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, zzf com_google_android_gms_measurement_AppMeasurement_zzf2);
    }

    public static class zzf {
        public String aqA;
        public long aqB;
        public String aqz;

        public zzf(zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
            this.aqz = com_google_android_gms_measurement_AppMeasurement_zzf.aqz;
            this.aqA = com_google_android_gms_measurement_AppMeasurement_zzf.aqA;
            this.aqB = com_google_android_gms_measurement_AppMeasurement_zzf.aqB;
        }
    }

    public static final class zza extends Event {
        public static final Map<String, String> aqx = com.google.android.gms.common.util.zzf.zzb(new String[]{"app_clear_data", "app_exception", "app_remove", "app_install", "app_update", "firebase_campaign", "error", "first_open", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement"}, new String[]{"_cd", "_ae", "_ui", "_in", "_au", "_cmp", "_err", "_f", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e"});
    }

    public static final class zze extends Param {
        public static final Map<String, String> aqy = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_conversion", "engagement_time_msec", "firebase_error", "firebase_error_value", "firebase_error_length", BuildConfig.BUILD_TYPE, "realtime", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count"}, new String[]{"_c", "_et", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_sn", "_sc", "_si", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin"});
    }

    public static final class zzg extends UserProperty {
        public static final Map<String, String> aqC = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_last_notification", "first_open_time", "last_deep_link_referrer", "user_id"}, new String[]{"_ln", "_fot", "_ldl", "_id"});
    }

    public AppMeasurement(zzx com_google_android_gms_measurement_internal_zzx) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzx);
        this.aqw = com_google_android_gms_measurement_internal_zzx;
    }

    @Keep
    @RequiresPermission(allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzx.zzdq(context).zzbxt();
    }

    private void zzc(String str, String str2, Object obj) {
        this.aqw.zzbvq().zzd(str, str2, obj);
    }

    public void logEvent(@Size(max = 32, min = 1) @NonNull String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (this.aqw.zzbwd().zzayi() || !"_iap".equals(str)) {
            int zzmv = this.aqw.zzbvx().zzmv(str);
            if (zzmv != 0) {
                this.aqw.zzbvx().zza(zzmv, "_ev", this.aqw.zzbvx().zza(str, this.aqw.zzbwd().zzbud(), true), str != null ? str.length() : 0);
                return;
            }
        }
        this.aqw.zzbvq().zza("app", str, bundle, true);
    }

    @Keep
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.aqw.zzbvu().registerOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @Deprecated
    public void setMeasurementEnabled(boolean z) {
        this.aqw.zzbvq().setMeasurementEnabled(z);
    }

    public void setMinimumSessionDuration(long j) {
        this.aqw.zzbvq().setMinimumSessionDuration(j);
    }

    public void setSessionTimeoutDuration(long j) {
        this.aqw.zzbvq().setSessionTimeoutDuration(j);
    }

    public void setUserId(String str) {
        zzb("app", "_id", str);
    }

    public void setUserProperty(@Size(max = 24, min = 1) @NonNull String str, @Nullable @Size(max = 36) String str2) {
        int zzmx = this.aqw.zzbvx().zzmx(str);
        if (zzmx != 0) {
            this.aqw.zzbvx().zza(zzmx, "_ev", this.aqw.zzbvx().zza(str, this.aqw.zzbwd().zzbue(), true), str != null ? str.length() : 0);
        } else {
            zzb("app", str, str2);
        }
    }

    @Keep
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.aqw.zzbvu().unregisterOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.aqw.zzbvq().zza(com_google_android_gms_measurement_AppMeasurement_zzb);
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        this.aqw.zzbvq().zza(com_google_android_gms_measurement_AppMeasurement_zzc);
    }

    public void zza(String str, String str2, Bundle bundle, long j) {
        this.aqw.zzbvq().zzd(str, str2, bundle == null ? new Bundle() : bundle, j);
    }

    public void zzb(String str, String str2, Object obj) {
        zzc(str, str2, obj);
    }

    @WorkerThread
    public Map<String, Object> zzcf(boolean z) {
        List<UserAttributeParcel> zzcj = this.aqw.zzbvq().zzcj(z);
        Map<String, Object> hashMap = new HashMap(zzcj.size());
        for (UserAttributeParcel userAttributeParcel : zzcj) {
            hashMap.put(userAttributeParcel.name, userAttributeParcel.getValue());
        }
        return hashMap;
    }

    public void zze(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.aqw.zzbvq().zzf(str, str2, bundle);
    }
}
