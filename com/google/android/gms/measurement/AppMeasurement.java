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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzaas;
import com.google.android.gms.internal.zzatp;
import com.google.android.gms.internal.zzaub;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.BuildConfig;

@Deprecated
public class AppMeasurement {
    private final zzatp zzbpw;

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
        public String zzbpA;
        public long zzbpB;
        public String zzbpz;

        public zzf(zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
            this.zzbpz = com_google_android_gms_measurement_AppMeasurement_zzf.zzbpz;
            this.zzbpA = com_google_android_gms_measurement_AppMeasurement_zzf.zzbpA;
            this.zzbpB = com_google_android_gms_measurement_AppMeasurement_zzf.zzbpB;
        }
    }

    public static final class zza extends Event {
        public static final Map<String, String> zzbpx = com.google.android.gms.common.util.zzf.zzb(new String[]{"app_clear_data", "app_exception", "app_remove", "app_install", "app_update", "firebase_campaign", "error", "first_open", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "firebase_ad_exposure", "firebase_adunit_exposure"}, new String[]{"_cd", "_ae", "_ui", "_in", "_au", "_cmp", "_err", "_f", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu"});
    }

    public static final class zze extends Param {
        public static final Map<String, String> zzbpy = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_conversion", "engagement_time_msec", "exposure_time", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", BuildConfig.BUILD_TYPE, "realtime", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count"}, new String[]{"_c", "_et", "_xt", "_ai", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_sn", "_sc", "_si", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin"});
    }

    public static final class zzg extends UserProperty {
        public static final Map<String, String> zzbpC = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_last_notification", "first_open_time", "last_deep_link_referrer", "user_id"}, new String[]{"_ln", "_fot", "_ldl", "_id"});
    }

    public AppMeasurement(zzatp com_google_android_gms_internal_zzatp) {
        zzac.zzw(com_google_android_gms_internal_zzatp);
        this.zzbpw = com_google_android_gms_internal_zzatp;
    }

    @Keep
    @RequiresPermission(allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzatp.zzbu(context).zzLw();
    }

    private void zzc(String str, String str2, Object obj) {
        this.zzbpw.zzJi().zzd(str, str2, obj);
    }

    @Keep
    public void beginAdUnitExposure(@Size(min = 1) @NonNull String str) {
        this.zzbpw.zzJg().beginAdUnitExposure(str);
    }

    @Keep
    public void endAdUnitExposure(@Size(min = 1) @NonNull String str) {
        this.zzbpw.zzJg().endAdUnitExposure(str);
    }

    @Keep
    public long generateEventId() {
        return this.zzbpw.zzJp().zzMi();
    }

    @Keep
    @Nullable
    @WorkerThread
    public String getAppInstanceId() {
        return this.zzbpw.zzJi().zzfS(null);
    }

    @Keep
    @Nullable
    @WorkerThread
    public String getAppInstanceIdOnPackageSide(String str) {
        return this.zzbpw.zzJi().getAppInstanceIdOnPackageSide(str);
    }

    @Keep
    @Nullable
    public String getCurrentScreenName() {
        zzf zzLV = this.zzbpw.zzJm().zzLV();
        return zzLV != null ? zzLV.zzbpz : null;
    }

    @Keep
    @Nullable
    public String getCurrentScreenNameOnPackageSide(String str) {
        zzf zzfU = this.zzbpw.zzJm().zzfU(str);
        return zzfU != null ? zzfU.zzbpz : null;
    }

    @Keep
    @Nullable
    public String getGmpAppId() {
        try {
            return zzaas.zzwj();
        } catch (IllegalStateException e) {
            this.zzbpw.zzJt().zzLa().zzj("getGoogleAppId failed with exception", e);
            return null;
        }
    }

    @Keep
    @Nullable
    @WorkerThread
    public String getGmpAppIdOnPackageSide(String str) {
        return this.zzbpw.zzJi().getGmpAppIdOnPackageSide(str);
    }

    public void logEvent(@Size(max = 32, min = 1) @NonNull String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zzbpw.zzJv().zzKk();
        if (!"_iap".equals(str)) {
            int zzfX = this.zzbpw.zzJp().zzfX(str);
            if (zzfX != 0) {
                this.zzbpw.zzJp().zza(zzfX, "_ev", this.zzbpw.zzJp().zza(str, this.zzbpw.zzJv().zzJU(), true), str != null ? str.length() : 0);
                return;
            }
        }
        this.zzbpw.zzJi().zza("app", str, bundle, true);
    }

    @Keep
    public void logEventInternal(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zzbpw.zzJi().zze(str, str2, bundle);
    }

    @Keep
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.zzbpw.zzJm().registerOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @Deprecated
    public void setMeasurementEnabled(boolean z) {
        this.zzbpw.zzJi().setMeasurementEnabled(z);
    }

    public void setMinimumSessionDuration(long j) {
        this.zzbpw.zzJi().setMinimumSessionDuration(j);
    }

    public void setSessionTimeoutDuration(long j) {
        this.zzbpw.zzJi().setSessionTimeoutDuration(j);
    }

    public void setUserId(String str) {
        zzb("app", "_id", str);
    }

    public void setUserProperty(@Size(max = 24, min = 1) @NonNull String str, @Nullable @Size(max = 36) String str2) {
        int zzfZ = this.zzbpw.zzJp().zzfZ(str);
        if (zzfZ != 0) {
            this.zzbpw.zzJp().zza(zzfZ, "_ev", this.zzbpw.zzJp().zza(str, this.zzbpw.zzJv().zzJV(), true), str != null ? str.length() : 0);
        } else {
            zzb("app", str, str2);
        }
    }

    @Keep
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.zzbpw.zzJm().unregisterOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.zzbpw.zzJi().zza(com_google_android_gms_measurement_AppMeasurement_zzb);
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        this.zzbpw.zzJi().zza(com_google_android_gms_measurement_AppMeasurement_zzc);
    }

    public void zza(String str, String str2, Bundle bundle, long j) {
        this.zzbpw.zzJi().zzd(str, str2, bundle == null ? new Bundle() : bundle, j);
    }

    @WorkerThread
    public Map<String, Object> zzaE(boolean z) {
        List<zzaub> zzaI = this.zzbpw.zzJi().zzaI(z);
        Map<String, Object> hashMap = new HashMap(zzaI.size());
        for (zzaub com_google_android_gms_internal_zzaub : zzaI) {
            hashMap.put(com_google_android_gms_internal_zzaub.name, com_google_android_gms_internal_zzaub.getValue());
        }
        return hashMap;
    }

    public void zzb(String str, String str2, Object obj) {
        zzc(str, str2, obj);
    }
}
