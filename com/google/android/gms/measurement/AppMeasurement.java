package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzf;
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
    private final zzx anq;

    public interface zzb {
        @WorkerThread
        void zzb(String str, String str2, Bundle bundle, long j);
    }

    public interface zzc {
        @WorkerThread
        void zzc(String str, String str2, Bundle bundle, long j);
    }

    public static final class zza extends Event {
        public static final Map<String, String> anr = zzf.zzb(new String[]{"app_clear_data", "app_exception", "app_remove", "app_update", "firebase_campaign", "error", "first_open", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement"}, new String[]{"_cd", "_ae", "_ui", "_au", "_cmp", "_err", "_f", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e"});
    }

    public static final class zzd extends Param {
        public static final Map<String, String> ans = zzf.zzb(new String[]{"firebase_conversion", "engagement_time_msec", "firebase_error", "firebase_error_value", "firebase_error_length", BuildConfig.BUILD_TYPE, "realtime", "firebase_event_origin", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update"}, new String[]{"_c", "_et", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu"});
    }

    public static final class zze extends UserProperty {
        public static final Map<String, String> ant = zzf.zzb(new String[]{"firebase_last_notification", "first_open_time", "last_deep_link_referrer", "user_id"}, new String[]{"_ln", "_fot", "_ldl", "_id"});
    }

    public AppMeasurement(zzx com_google_android_gms_measurement_internal_zzx) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzx);
        this.anq = com_google_android_gms_measurement_internal_zzx;
    }

    @Keep
    @RequiresPermission(allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzx.zzdt(context).zzbwy();
    }

    private void zzc(String str, String str2, Object obj) {
        this.anq.zzbux().zzd(str, str2, obj);
    }

    public void logEvent(@Size(max = 32, min = 1) @NonNull String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (this.anq.zzbvi().zzact() || !"_iap".equals(str)) {
            int zzmy = this.anq.zzbvc().zzmy(str);
            if (zzmy != 0) {
                this.anq.zzbvc().zza(zzmy, "_ev", this.anq.zzbvc().zza(str, this.anq.zzbvi().zzbtn(), true), str != null ? str.length() : 0);
                return;
            }
        }
        this.anq.zzbux().zza("app", str, bundle, true);
    }

    @Deprecated
    public void setMeasurementEnabled(boolean z) {
        this.anq.zzbux().setMeasurementEnabled(z);
    }

    public void setMinimumSessionDuration(long j) {
        this.anq.zzbux().setMinimumSessionDuration(j);
    }

    public void setSessionTimeoutDuration(long j) {
        this.anq.zzbux().setSessionTimeoutDuration(j);
    }

    public void setUserId(String str) {
        zzb("app", "_id", str);
    }

    public void setUserProperty(@Size(max = 24, min = 1) @NonNull String str, @Nullable @Size(max = 36) String str2) {
        int zzna = this.anq.zzbvc().zzna(str);
        if (zzna != 0) {
            this.anq.zzbvc().zza(zzna, "_ev", this.anq.zzbvc().zza(str, this.anq.zzbvi().zzbto(), true), str != null ? str.length() : 0);
        } else {
            zzb("app", str, str2);
        }
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.anq.zzbux().zza(com_google_android_gms_measurement_AppMeasurement_zzb);
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        this.anq.zzbux().zza(com_google_android_gms_measurement_AppMeasurement_zzc);
    }

    public void zza(String str, String str2, Bundle bundle, long j) {
        this.anq.zzbux().zzd(str, str2, bundle == null ? new Bundle() : bundle, j);
    }

    public void zzb(String str, String str2, Object obj) {
        zzc(str, str2, obj);
    }

    @WorkerThread
    public Map<String, Object> zzce(boolean z) {
        List<UserAttributeParcel> zzci = this.anq.zzbux().zzci(z);
        Map<String, Object> hashMap = new HashMap(zzci.size());
        for (UserAttributeParcel userAttributeParcel : zzci) {
            hashMap.put(userAttributeParcel.name, userAttributeParcel.getValue());
        }
        return hashMap;
    }

    public void zze(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.anq.zzbux().zzf(str, str2, bundle);
    }
}
