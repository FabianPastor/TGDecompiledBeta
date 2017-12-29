package com.google.firebase.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzcim;
import com.google.android.gms.tasks.Task;

@Keep
public final class FirebaseAnalytics {
    private final zzcim zziwf;

    public static class Event {
    }

    public static class Param {
    }

    public static class UserProperty {
    }

    public FirebaseAnalytics(zzcim com_google_android_gms_internal_zzcim) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        this.zziwf = com_google_android_gms_internal_zzcim;
    }

    @Keep
    public static FirebaseAnalytics getInstance(Context context) {
        return zzcim.zzdx(context).zzbaa();
    }

    public final Task<String> getAppInstanceId() {
        return this.zziwf.zzawm().getAppInstanceId();
    }

    public final void logEvent(String str, Bundle bundle) {
        this.zziwf.zzazz().logEvent(str, bundle);
    }

    public final void resetAnalyticsData() {
        this.zziwf.zzawm().resetAnalyticsData();
    }

    public final void setAnalyticsCollectionEnabled(boolean z) {
        this.zziwf.zzazz().setMeasurementEnabled(z);
    }

    @Keep
    public final void setCurrentScreen(Activity activity, String str, String str2) {
        this.zziwf.zzawq().setCurrentScreen(activity, str, str2);
    }

    public final void setMinimumSessionDuration(long j) {
        this.zziwf.zzazz().setMinimumSessionDuration(j);
    }

    public final void setSessionTimeoutDuration(long j) {
        this.zziwf.zzazz().setSessionTimeoutDuration(j);
    }

    public final void setUserId(String str) {
        this.zziwf.zzazz().setUserPropertyInternal("app", "_id", str);
    }

    public final void setUserProperty(String str, String str2) {
        this.zziwf.zzazz().setUserProperty(str, str2);
    }
}
