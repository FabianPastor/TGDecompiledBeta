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
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzaba;
import com.google.android.gms.internal.zzaue;
import com.google.android.gms.internal.zzauq;
import com.google.android.gms.internal.zzaut;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.BuildConfig;

@Keep
@Deprecated
public class AppMeasurement {
    private final zzaue zzbqb;

    public static class ConditionalUserProperty {
        @Keep
        public boolean mActive;
        @Keep
        public String mAppId;
        @Keep
        public long mCreationTimestamp;
        @Keep
        public String mExpiredEventName;
        @Keep
        public Bundle mExpiredEventParams;
        @Keep
        public String mName;
        @Keep
        public String mOrigin;
        @Keep
        public long mTimeToLive;
        @Keep
        public String mTimedOutEventName;
        @Keep
        public Bundle mTimedOutEventParams;
        @Keep
        public String mTriggerEventName;
        @Keep
        public long mTriggerTimeout;
        @Keep
        public String mTriggeredEventName;
        @Keep
        public Bundle mTriggeredEventParams;
        @Keep
        public long mTriggeredTimestamp;
        @Keep
        public Object mValue;

        public ConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
            zzac.zzw(conditionalUserProperty);
            this.mAppId = conditionalUserProperty.mAppId;
            this.mOrigin = conditionalUserProperty.mOrigin;
            this.mCreationTimestamp = conditionalUserProperty.mCreationTimestamp;
            this.mName = conditionalUserProperty.mName;
            if (conditionalUserProperty.mValue != null) {
                this.mValue = zzaut.zzI(conditionalUserProperty.mValue);
                if (this.mValue == null) {
                    this.mValue = conditionalUserProperty.mValue;
                }
            }
            this.mValue = conditionalUserProperty.mValue;
            this.mActive = conditionalUserProperty.mActive;
            this.mTriggerEventName = conditionalUserProperty.mTriggerEventName;
            this.mTriggerTimeout = conditionalUserProperty.mTriggerTimeout;
            this.mTimedOutEventName = conditionalUserProperty.mTimedOutEventName;
            if (conditionalUserProperty.mTimedOutEventParams != null) {
                this.mTimedOutEventParams = new Bundle(conditionalUserProperty.mTimedOutEventParams);
            }
            this.mTriggeredEventName = conditionalUserProperty.mTriggeredEventName;
            if (conditionalUserProperty.mTriggeredEventParams != null) {
                this.mTriggeredEventParams = new Bundle(conditionalUserProperty.mTriggeredEventParams);
            }
            this.mTriggeredTimestamp = conditionalUserProperty.mTriggeredTimestamp;
            this.mTimeToLive = conditionalUserProperty.mTimeToLive;
            this.mExpiredEventName = conditionalUserProperty.mExpiredEventName;
            if (conditionalUserProperty.mExpiredEventParams != null) {
                this.mExpiredEventParams = new Bundle(conditionalUserProperty.mExpiredEventParams);
            }
        }
    }

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
        public String zzbqe;
        public String zzbqf;
        public long zzbqg;

        public zzf(zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
            this.zzbqe = com_google_android_gms_measurement_AppMeasurement_zzf.zzbqe;
            this.zzbqf = com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf;
            this.zzbqg = com_google_android_gms_measurement_AppMeasurement_zzf.zzbqg;
        }
    }

    public static final class zza extends Event {
        public static final Map<String, String> zzbqc = com.google.android.gms.common.util.zzf.zzb(new String[]{"app_clear_data", "app_exception", "app_remove", "app_upgrade", "app_install", "app_update", "firebase_campaign", "error", "first_open", "first_visit", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "firebase_ad_exposure", "firebase_adunit_exposure", "firebase_extra_parameter"}, new String[]{"_cd", "_ae", "_ui", "_in", "_ug", "_au", "_cmp", "_err", "_f", "_v", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu", "_ep"});
    }

    public static final class zze extends Param {
        public static final Map<String, String> zzbqd = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_conversion", "engagement_time_msec", "exposure_time", "ad_event_id", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", BuildConfig.BUILD_TYPE, "realtime", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count", "firebase_event_id", "firebase_extra_params_ct", "firebase_group_name", "firebase_list_length", "firebase_index", "firebase_event_name"}, new String[]{"_c", "_et", "_xt", "_aeid", "_ai", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_sn", "_sc", "_si", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin", "_eid", "_epc", "_gn", "_ll", "_i", "_en"});
    }

    public static final class zzg extends UserProperty {
        public static final Map<String, String> zzbqh = com.google.android.gms.common.util.zzf.zzb(new String[]{"firebase_last_notification", "first_open_time", "first_visit_time", "last_deep_link_referrer", "user_id"}, new String[]{"_ln", "_fot", "_fvt", "_ldl", "_id"});
    }

    public AppMeasurement(zzaue com_google_android_gms_internal_zzaue) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqb = com_google_android_gms_internal_zzaue;
    }

    @Keep
    @RequiresPermission(allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzaue.zzbM(context).zzMx();
    }

    private void zzc(String str, String str2, Object obj) {
        this.zzbqb.zzKa().zzd(str, str2, obj);
    }

    @Keep
    public void beginAdUnitExposure(@Size(min = 1) @NonNull String str) {
        this.zzbqb.zzJY().beginAdUnitExposure(str);
    }

    @Keep
    protected void clearConditionalUserProperty(@Size(max = 24, min = 1) @NonNull String str, @Nullable String str2, @Nullable Bundle bundle) {
        this.zzbqb.zzKa().clearConditionalUserProperty(str, str2, bundle);
    }

    @Keep
    protected void clearConditionalUserPropertyAs(@Size(min = 1) @NonNull String str, @Size(max = 24, min = 1) @NonNull String str2, @Nullable String str3, @Nullable Bundle bundle) {
        this.zzbqb.zzKa().clearConditionalUserPropertyAs(str, str2, str3, bundle);
    }

    @Keep
    public void endAdUnitExposure(@Size(min = 1) @NonNull String str) {
        this.zzbqb.zzJY().endAdUnitExposure(str);
    }

    @Keep
    public long generateEventId() {
        return this.zzbqb.zzKh().zzNk();
    }

    @Keep
    @Nullable
    @WorkerThread
    public String getAppInstanceId() {
        return this.zzbqb.zzKa().zzfQ(null);
    }

    @Keep
    @WorkerThread
    protected List<ConditionalUserProperty> getConditionalUserProperties(@Nullable String str, @Nullable @Size(max = 23, min = 1) String str2) {
        return this.zzbqb.zzKa().getConditionalUserProperties(str, str2);
    }

    @Keep
    @WorkerThread
    protected List<ConditionalUserProperty> getConditionalUserPropertiesAs(@Size(min = 1) @NonNull String str, @Nullable String str2, @Nullable @Size(max = 23, min = 1) String str3) {
        return this.zzbqb.zzKa().getConditionalUserPropertiesAs(str, str2, str3);
    }

    @Keep
    @Nullable
    public String getCurrentScreenClass() {
        zzf zzMX = this.zzbqb.zzKe().zzMX();
        return zzMX != null ? zzMX.zzbqf : null;
    }

    @Keep
    @Nullable
    public String getCurrentScreenName() {
        zzf zzMX = this.zzbqb.zzKe().zzMX();
        return zzMX != null ? zzMX.zzbqe : null;
    }

    @Keep
    @Nullable
    public String getGmpAppId() {
        try {
            return zzaba.zzwQ();
        } catch (IllegalStateException e) {
            this.zzbqb.zzKl().zzLZ().zzj("getGoogleAppId failed with exception", e);
            return null;
        }
    }

    @Keep
    @WorkerThread
    protected int getMaxUserProperties(@Size(min = 1) @NonNull String str) {
        return this.zzbqb.zzKa().getMaxUserProperties(str);
    }

    @Keep
    @WorkerThread
    protected Map<String, Object> getUserProperties(@Nullable String str, @Nullable @Size(max = 24, min = 1) String str2, boolean z) {
        return this.zzbqb.zzKa().getUserProperties(str, str2, z);
    }

    @Keep
    @WorkerThread
    protected Map<String, Object> getUserPropertiesAs(@Size(min = 1) @NonNull String str, @Nullable String str2, @Nullable @Size(max = 23, min = 1) String str3, boolean z) {
        return this.zzbqb.zzKa().getUserPropertiesAs(str, str2, str3, z);
    }

    public void logEvent(@Size(max = 40, min = 1) @NonNull String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zzbqb.zzKn().zzLh();
        if (!"_iap".equals(str)) {
            int zzfU = this.zzbqb.zzKh().zzfU(str);
            if (zzfU != 0) {
                this.zzbqb.zzKh().zza(zzfU, "_ev", this.zzbqb.zzKh().zza(str, this.zzbqb.zzKn().zzKM(), true), str != null ? str.length() : 0);
                return;
            }
        }
        this.zzbqb.zzKa().zza("app", str, bundle, true);
    }

    @Keep
    public void logEventInternal(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zzbqb.zzKa().zze(str, str2, bundle);
    }

    @Keep
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.zzbqb.zzKe().registerOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @Keep
    protected void setConditionalUserProperty(@NonNull ConditionalUserProperty conditionalUserProperty) {
        this.zzbqb.zzKa().setConditionalUserProperty(conditionalUserProperty);
    }

    @Keep
    protected void setConditionalUserPropertyAs(@NonNull ConditionalUserProperty conditionalUserProperty) {
        this.zzbqb.zzKa().setConditionalUserPropertyAs(conditionalUserProperty);
    }

    @Deprecated
    public void setMeasurementEnabled(boolean z) {
        this.zzbqb.zzKa().setMeasurementEnabled(z);
    }

    public void setMinimumSessionDuration(long j) {
        this.zzbqb.zzKa().setMinimumSessionDuration(j);
    }

    public void setSessionTimeoutDuration(long j) {
        this.zzbqb.zzKa().setSessionTimeoutDuration(j);
    }

    public void setUserId(String str) {
        zzb("app", "_id", str);
    }

    public void setUserProperty(@Size(max = 24, min = 1) @NonNull String str, @Nullable @Size(max = 36) String str2) {
        int zzfW = this.zzbqb.zzKh().zzfW(str);
        if (zzfW != 0) {
            this.zzbqb.zzKh().zza(zzfW, "_ev", this.zzbqb.zzKh().zza(str, this.zzbqb.zzKn().zzKN(), true), str != null ? str.length() : 0);
        } else {
            zzb("app", str, str2);
        }
    }

    @Keep
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        this.zzbqb.zzKe().unregisterOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        this.zzbqb.zzKa().zza(com_google_android_gms_measurement_AppMeasurement_zzb);
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        this.zzbqb.zzKa().zza(com_google_android_gms_measurement_AppMeasurement_zzc);
    }

    public void zza(String str, String str2, Bundle bundle, long j) {
        this.zzbqb.zzKa().zzd(str, str2, bundle == null ? new Bundle() : bundle, j);
    }

    @WorkerThread
    public Map<String, Object> zzaI(boolean z) {
        List<zzauq> zzaM = this.zzbqb.zzKa().zzaM(z);
        Map<String, Object> arrayMap = new ArrayMap(zzaM.size());
        for (zzauq com_google_android_gms_internal_zzauq : zzaM) {
            arrayMap.put(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
        }
        return arrayMap;
    }

    public void zzb(String str, String str2, Object obj) {
        zzc(str, str2, obj);
    }
}
