package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.api.internal.zzbz;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzcim;
import com.google.android.gms.internal.zzcln;
import com.google.android.gms.internal.zzclq;
import java.util.List;
import java.util.Map;

@Keep
@Deprecated
public class AppMeasurement {
    public static final String CRASH_ORIGIN = "crash";
    public static final String FCM_ORIGIN = "fcm";
    private final zzcim zziwf;

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
            zzbq.checkNotNull(conditionalUserProperty);
            this.mAppId = conditionalUserProperty.mAppId;
            this.mOrigin = conditionalUserProperty.mOrigin;
            this.mCreationTimestamp = conditionalUserProperty.mCreationTimestamp;
            this.mName = conditionalUserProperty.mName;
            if (conditionalUserProperty.mValue != null) {
                this.mValue = zzclq.zzag(conditionalUserProperty.mValue);
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

    public interface EventInterceptor {
        void interceptEvent(String str, String str2, Bundle bundle, long j);
    }

    public interface OnEventListener {
        void onEvent(String str, String str2, Bundle bundle, long j);
    }

    public interface zza {
        boolean zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb, zzb com_google_android_gms_measurement_AppMeasurement_zzb2);
    }

    public static class zzb {
        public String zziwk;
        public String zziwl;
        public long zziwm;

        public zzb(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
            this.zziwk = com_google_android_gms_measurement_AppMeasurement_zzb.zziwk;
            this.zziwl = com_google_android_gms_measurement_AppMeasurement_zzb.zziwl;
            this.zziwm = com_google_android_gms_measurement_AppMeasurement_zzb.zziwm;
        }
    }

    public static final class Event extends com.google.firebase.analytics.FirebaseAnalytics.Event {
        public static final String[] zziwg = new String[]{"app_clear_data", "app_exception", "app_remove", "app_upgrade", "app_install", "app_update", "firebase_campaign", "error", "first_open", "first_visit", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "ad_exposure", "adunit_exposure", "ad_query", "ad_activeview", "ad_impression", "ad_click", "screen_view", "firebase_extra_parameter"};
        public static final String[] zziwh = new String[]{"_cd", "_ae", "_ui", "_ug", "_in", "_au", "_cmp", "_err", "_f", "_v", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu", "_aq", "_aa", "_ai", "_ac", "_vs", "_ep"};

        public static String zziq(String str) {
            return zzclq.zza(str, zziwg, zziwh);
        }
    }

    public static final class Param extends com.google.firebase.analytics.FirebaseAnalytics.Param {
        public static final String[] zziwi = new String[]{"firebase_conversion", "engagement_time_msec", "exposure_time", "ad_event_id", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "firebase_previous_screen", "firebase_previous_class", "firebase_previous_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count", "firebase_event_id", "firebase_extra_params_ct", "firebase_group_name", "firebase_list_length", "firebase_index", "firebase_event_name"};
        public static final String[] zziwj = new String[]{"_c", "_et", "_xt", "_aeid", "_ai", "_err", "_ev", "_el", "_o", "_sn", "_sc", "_si", "_pn", "_pc", "_pi", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin", "_eid", "_epc", "_gn", "_ll", "_i", "_en"};

        public static String zziq(String str) {
            return zzclq.zza(str, zziwi, zziwj);
        }
    }

    public static final class UserProperty extends com.google.firebase.analytics.FirebaseAnalytics.UserProperty {
        public static final String[] zziwn = new String[]{"firebase_last_notification", "first_open_time", "first_visit_time", "last_deep_link_referrer", "user_id", "first_open_after_install"};
        public static final String[] zziwo = new String[]{"_ln", "_fot", "_fvt", "_ldl", "_id", "_fi"};

        public static String zziq(String str) {
            return zzclq.zza(str, zziwn, zziwo);
        }
    }

    public AppMeasurement(zzcim com_google_android_gms_internal_zzcim) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        this.zziwf = com_google_android_gms_internal_zzcim;
    }

    @Keep
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzcim.zzdx(context).zzazz();
    }

    @Keep
    public void beginAdUnitExposure(String str) {
        this.zziwf.zzawk().beginAdUnitExposure(str);
    }

    @Keep
    protected void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        this.zziwf.zzawm().clearConditionalUserProperty(str, str2, bundle);
    }

    @Keep
    protected void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        this.zziwf.zzawm().clearConditionalUserPropertyAs(str, str2, str3, bundle);
    }

    @Keep
    public void endAdUnitExposure(String str) {
        this.zziwf.zzawk().endAdUnitExposure(str);
    }

    @Keep
    public long generateEventId() {
        return this.zziwf.zzawu().zzbay();
    }

    @Keep
    public String getAppInstanceId() {
        return this.zziwf.zzawm().zzazn();
    }

    @Keep
    protected List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        return this.zziwf.zzawm().getConditionalUserProperties(str, str2);
    }

    @Keep
    protected List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        return this.zziwf.zzawm().getConditionalUserPropertiesAs(str, str2, str3);
    }

    @Keep
    public String getCurrentScreenClass() {
        zzb zzbap = this.zziwf.zzawq().zzbap();
        return zzbap != null ? zzbap.zziwl : null;
    }

    @Keep
    public String getCurrentScreenName() {
        zzb zzbap = this.zziwf.zzawq().zzbap();
        return zzbap != null ? zzbap.zziwk : null;
    }

    @Keep
    public String getGmpAppId() {
        try {
            return zzbz.zzajh();
        } catch (IllegalStateException e) {
            this.zziwf.zzawy().zzazd().zzj("getGoogleAppId failed with exception", e);
            return null;
        }
    }

    @Keep
    protected int getMaxUserProperties(String str) {
        this.zziwf.zzawm();
        zzbq.zzgm(str);
        return 25;
    }

    @Keep
    protected Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        return this.zziwf.zzawm().getUserProperties(str, str2, z);
    }

    public Map<String, Object> getUserProperties(boolean z) {
        List<zzcln> zzbq = this.zziwf.zzawm().zzbq(z);
        Map<String, Object> arrayMap = new ArrayMap(zzbq.size());
        for (zzcln com_google_android_gms_internal_zzcln : zzbq) {
            arrayMap.put(com_google_android_gms_internal_zzcln.name, com_google_android_gms_internal_zzcln.getValue());
        }
        return arrayMap;
    }

    @Keep
    protected Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        return this.zziwf.zzawm().getUserPropertiesAs(str, str2, str3, z);
    }

    public final void logEvent(String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (!"_iap".equals(str)) {
            int zzka = this.zziwf.zzawu().zzka(str);
            if (zzka != 0) {
                this.zziwf.zzawu();
                this.zziwf.zzawu().zza(zzka, "_ev", zzclq.zza(str, 40, true), str != null ? str.length() : 0);
                return;
            }
        }
        this.zziwf.zzawm().zza("app", str, bundle, true);
    }

    @Keep
    public void logEventInternal(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zziwf.zzawm().zzc(str, str2, bundle);
    }

    public void logEventInternalNoInterceptor(String str, String str2, Bundle bundle, long j) {
        this.zziwf.zzawm().zza(str, str2, bundle == null ? new Bundle() : bundle, j);
    }

    public void registerOnMeasurementEventListener(OnEventListener onEventListener) {
        this.zziwf.zzawm().registerOnMeasurementEventListener(onEventListener);
    }

    @Keep
    public void registerOnScreenChangeCallback(zza com_google_android_gms_measurement_AppMeasurement_zza) {
        this.zziwf.zzawq().registerOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zza);
    }

    @Keep
    protected void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        this.zziwf.zzawm().setConditionalUserProperty(conditionalUserProperty);
    }

    @Keep
    protected void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        this.zziwf.zzawm().setConditionalUserPropertyAs(conditionalUserProperty);
    }

    public void setEventInterceptor(EventInterceptor eventInterceptor) {
        this.zziwf.zzawm().setEventInterceptor(eventInterceptor);
    }

    @Deprecated
    public void setMeasurementEnabled(boolean z) {
        this.zziwf.zzawm().setMeasurementEnabled(z);
    }

    public final void setMinimumSessionDuration(long j) {
        this.zziwf.zzawm().setMinimumSessionDuration(j);
    }

    public final void setSessionTimeoutDuration(long j) {
        this.zziwf.zzawm().setSessionTimeoutDuration(j);
    }

    public final void setUserProperty(String str, String str2) {
        int zzkc = this.zziwf.zzawu().zzkc(str);
        if (zzkc != 0) {
            this.zziwf.zzawu();
            this.zziwf.zzawu().zza(zzkc, "_ev", zzclq.zza(str, 24, true), str != null ? str.length() : 0);
            return;
        }
        setUserPropertyInternal("app", str, str2);
    }

    public void setUserPropertyInternal(String str, String str2, Object obj) {
        this.zziwf.zzawm().zzb(str, str2, obj);
    }

    public void unregisterOnMeasurementEventListener(OnEventListener onEventListener) {
        this.zziwf.zzawm().unregisterOnMeasurementEventListener(onEventListener);
    }

    @Keep
    public void unregisterOnScreenChangeCallback(zza com_google_android_gms_measurement_AppMeasurement_zza) {
        this.zziwf.zzawq().unregisterOnScreenChangeCallback(com_google_android_gms_measurement_AppMeasurement_zza);
    }
}
