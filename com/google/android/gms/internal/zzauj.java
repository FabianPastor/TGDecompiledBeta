package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.measurement.AppMeasurement.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;

public class zzauj extends zzauh {
    protected zza zzbuY;
    private zzb zzbuZ;
    private final Set<zzc> zzbva = new CopyOnWriteArraySet();
    private boolean zzbvb;
    private String zzbvc = null;
    private String zzbvd = null;

    @MainThread
    @TargetApi(14)
    private class zza implements ActivityLifecycleCallbacks {
        final /* synthetic */ zzauj zzbve;

        private zza(zzauj com_google_android_gms_internal_zzauj) {
            this.zzbve = com_google_android_gms_internal_zzauj;
        }

        private boolean zzfR(String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            this.zzbve.zzd("auto", "_ldl", str);
            return true;
        }

        public void onActivityCreated(Activity activity, Bundle bundle) {
            try {
                this.zzbve.zzKl().zzMf().log("onActivityCreated");
                Intent intent = activity.getIntent();
                if (intent != null) {
                    Uri data = intent.getData();
                    if (data != null && data.isHierarchical()) {
                        if (bundle == null) {
                            Bundle zzu = this.zzbve.zzKh().zzu(data);
                            String str = this.zzbve.zzKh().zzA(intent) ? "gs" : "auto";
                            if (zzu != null) {
                                this.zzbve.zze(str, "_cmp", zzu);
                            }
                        }
                        String queryParameter = data.getQueryParameter("referrer");
                        if (!TextUtils.isEmpty(queryParameter)) {
                            Object obj = (queryParameter.contains("gclid") && (queryParameter.contains("utm_campaign") || queryParameter.contains("utm_source") || queryParameter.contains("utm_medium") || queryParameter.contains("utm_term") || queryParameter.contains("utm_content"))) ? 1 : null;
                            if (obj == null) {
                                this.zzbve.zzKl().zzMe().log("Activity created with data 'referrer' param without gclid and at least one utm field");
                                return;
                            } else {
                                this.zzbve.zzKl().zzMe().zzj("Activity created with referrer", queryParameter);
                                zzfR(queryParameter);
                            }
                        } else {
                            return;
                        }
                    }
                }
            } catch (Throwable th) {
                this.zzbve.zzKl().zzLZ().zzj("Throwable caught in onActivityCreated", th);
            }
            this.zzbve.zzKe().onActivityCreated(activity, bundle);
        }

        public void onActivityDestroyed(Activity activity) {
            this.zzbve.zzKe().onActivityDestroyed(activity);
        }

        @MainThread
        public void onActivityPaused(Activity activity) {
            this.zzbve.zzKe().onActivityPaused(activity);
            this.zzbve.zzKj().zzNg();
        }

        @MainThread
        public void onActivityResumed(Activity activity) {
            this.zzbve.zzKe().onActivityResumed(activity);
            this.zzbve.zzKj().zzNe();
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            this.zzbve.zzKe().onActivitySaveInstanceState(activity, bundle);
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    protected zzauj(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private Bundle zzM(Bundle bundle) {
        if (bundle == null) {
            return new Bundle();
        }
        Bundle bundle2 = new Bundle(bundle);
        for (String str : bundle2.keySet()) {
            Object obj = bundle2.get(str);
            if (obj instanceof Bundle) {
                bundle2.putBundle(str, new Bundle((Bundle) obj));
            } else if (obj instanceof Parcelable[]) {
                Parcelable[] parcelableArr = (Parcelable[]) obj;
                for (r2 = 0; r2 < parcelableArr.length; r2++) {
                    if (parcelableArr[r2] instanceof Bundle) {
                        parcelableArr[r2] = new Bundle((Bundle) parcelableArr[r2]);
                    }
                }
            } else if (obj instanceof ArrayList) {
                ArrayList arrayList = (ArrayList) obj;
                for (r2 = 0; r2 < arrayList.size(); r2++) {
                    Object obj2 = arrayList.get(r2);
                    if (obj2 instanceof Bundle) {
                        arrayList.set(r2, new Bundle((Bundle) obj2));
                    }
                }
            }
        }
        return bundle2;
    }

    @WorkerThread
    private void zzMU() {
        try {
            zzf(Class.forName(zzMV()));
        } catch (ClassNotFoundException e) {
            zzKl().zzMd().log("Tag Manager is not found and thus will not be used");
        }
    }

    private String zzMV() {
        return "com.google.android.gms.tagmanager.TagManagerService";
    }

    private void zza(final ConditionalUserProperty conditionalUserProperty) {
        long currentTimeMillis = zznR().currentTimeMillis();
        zzac.zzw(conditionalUserProperty);
        zzac.zzdr(conditionalUserProperty.mName);
        zzac.zzdr(conditionalUserProperty.mOrigin);
        zzac.zzw(conditionalUserProperty.mValue);
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        String str = conditionalUserProperty.mName;
        Object obj = conditionalUserProperty.mValue;
        if (zzKh().zzfX(str) != 0) {
            zzKl().zzLZ().zzj("Invalid conditional user property name", str);
        } else if (zzKh().zzl(str, obj) != 0) {
            zzKl().zzLZ().zze("Invalid conditional user property value", str, obj);
        } else {
            Object zzm = zzKh().zzm(str, obj);
            if (zzm == null) {
                zzKl().zzLZ().zze("Unable to normalize conditional user property value", str, obj);
                return;
            }
            conditionalUserProperty.mValue = zzm;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (j > zzKn().zzLb() || j < 1) {
                zzKl().zzLZ().zze("Invalid conditional user property timeout", str, Long.valueOf(j));
                return;
            }
            j = conditionalUserProperty.mTimeToLive;
            if (j > zzKn().zzLc() || j < 1) {
                zzKl().zzLZ().zze("Invalid conditional user property time to live", str, Long.valueOf(j));
            } else {
                zzKk().zzm(new Runnable(this) {
                    final /* synthetic */ zzauj zzbve;

                    public void run() {
                        this.zzbve.zzb(conditionalUserProperty);
                    }
                });
            }
        }
    }

    private void zza(String str, String str2, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zza(str, str2, zznR().currentTimeMillis(), bundle, z, z2, z3, str3);
    }

    @WorkerThread
    private void zza(String str, String str2, Object obj, long j) {
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzJW();
        zzob();
        if (!this.zzbqb.isEnabled()) {
            zzKl().zzMe().log("User property not set since app measurement is disabled");
        } else if (this.zzbqb.zzMu()) {
            zzKl().zzMe().zze("Setting user property (FE)", str2, obj);
            zzKd().zzb(new zzauq(str2, j, obj, str));
        }
    }

    private void zza(String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = zznR().currentTimeMillis();
        zzac.zzdr(str2);
        final ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
        conditionalUserProperty.mAppId = str;
        conditionalUserProperty.mName = str2;
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        if (str3 != null) {
            conditionalUserProperty.mExpiredEventName = str3;
            conditionalUserProperty.mExpiredEventParams = bundle;
        }
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zzc(conditionalUserProperty);
            }
        });
    }

    @WorkerThread
    private void zzaL(boolean z) {
        zzmR();
        zzJW();
        zzob();
        zzKl().zzMe().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        zzKm().setMeasurementEnabled(z);
        zzKd().zzMY();
    }

    private Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        final AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            final String str4 = str;
            final String str5 = str2;
            final String str6 = str3;
            final boolean z2 = z;
            this.zzbqb.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauj zzbve;

                public void run() {
                    this.zzbve.zzbqb.zzKd().zza(atomicReference, str4, str5, str6, z2);
                }
            });
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzKl().zzMb().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzauq> list = (List) atomicReference.get();
        if (list == null) {
            zzKl().zzMb().log("Timed out waiting for get user properties");
            return Collections.emptyMap();
        }
        Map<String, Object> arrayMap = new ArrayMap(list.size());
        for (zzauq com_google_android_gms_internal_zzauq : list) {
            arrayMap.put(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
        }
        return arrayMap;
    }

    @WorkerThread
    private void zzb(ConditionalUserProperty conditionalUserProperty) {
        zzmR();
        zzob();
        zzac.zzw(conditionalUserProperty);
        zzac.zzdr(conditionalUserProperty.mName);
        zzac.zzdr(conditionalUserProperty.mOrigin);
        zzac.zzw(conditionalUserProperty.mValue);
        if (this.zzbqb.isEnabled()) {
            zzauq com_google_android_gms_internal_zzauq = new zzauq(conditionalUserProperty.mName, conditionalUserProperty.mTriggeredTimestamp, conditionalUserProperty.mValue, conditionalUserProperty.mOrigin);
            try {
                zzatq zza = zzKh().zza(conditionalUserProperty.mTriggeredEventName, conditionalUserProperty.mTriggeredEventParams, conditionalUserProperty.mOrigin, 0, true, false);
                zzKd().zzf(new zzatg(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzauq, conditionalUserProperty.mCreationTimestamp, false, conditionalUserProperty.mTriggerEventName, zzKh().zza(conditionalUserProperty.mTimedOutEventName, conditionalUserProperty.mTimedOutEventParams, conditionalUserProperty.mOrigin, 0, true, false), conditionalUserProperty.mTriggerTimeout, zza, conditionalUserProperty.mTimeToLive, zzKh().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, 0, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        zzKl().zzMe().log("Conditional property not sent since Firebase Analytics is disabled");
    }

    @WorkerThread
    private void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzac.zzw(bundle);
        zzmR();
        zzob();
        if (this.zzbqb.isEnabled()) {
            if (!this.zzbvb) {
                this.zzbvb = true;
                zzMU();
            }
            boolean equals = "am".equals(str);
            boolean zzgd = zzaut.zzgd(str2);
            if (z && this.zzbuZ != null && !zzgd && !equals) {
                zzKl().zzMe().zze("Passing event to registered event handler (FE)", str2, bundle);
                this.zzbuZ.zzb(str, str2, bundle, j);
                return;
            } else if (this.zzbqb.zzMu()) {
                int zzfV = zzKh().zzfV(str2);
                if (zzfV != 0) {
                    this.zzbqb.zzKh().zza(zzfV, "_ev", zzKh().zza(str2, zzKn().zzKM(), true), str2 != null ? str2.length() : 0);
                    return;
                }
                int i;
                Bundle zza;
                List zzx = zzf.zzx("_o");
                Bundle zza2 = zzKh().zza(str2, bundle, zzx, z3, true);
                List arrayList = new ArrayList();
                arrayList.add(zza2);
                long nextLong = zzKm().zzMh().nextLong();
                int i2 = 0;
                String[] strArr = (String[]) zza2.keySet().toArray(new String[bundle.size()]);
                Arrays.sort(strArr);
                int length = strArr.length;
                int i3 = 0;
                while (i3 < length) {
                    int i4;
                    String str4 = strArr[i3];
                    Bundle[] zzH = zzKh().zzH(zza2.get(str4));
                    if (zzH == null) {
                        i4 = i2;
                    } else {
                        zza2.putInt(str4, zzH.length);
                        for (i = 0; i < zzH.length; i++) {
                            zza = zzKh().zza("_ep", zzH[i], zzx, z3, false);
                            zza.putString("_en", str2);
                            zza.putLong("_eid", nextLong);
                            zza.putString("_gn", str4);
                            zza.putInt("_ll", zzH.length);
                            zza.putInt("_i", i);
                            arrayList.add(zza);
                        }
                        i4 = zzH.length + i2;
                    }
                    i3++;
                    i2 = i4;
                }
                if (i2 != 0) {
                    zza2.putLong("_eid", nextLong);
                    zza2.putInt("_epc", i2);
                }
                zzKn().zzLh();
                AppMeasurement.zzf zzMW = zzKe().zzMW();
                if (!(zzMW == null || zza2.containsKey("_sc"))) {
                    zzMW.zzbvC = true;
                }
                i = 0;
                while (i < arrayList.size()) {
                    zza = (Bundle) arrayList.get(i);
                    String str5 = (i != 0 ? 1 : null) != null ? "_ep" : str2;
                    zza.putString("_o", str);
                    if (!zza.containsKey("_sc")) {
                        zzauk.zza(zzMW, zza);
                    }
                    Bundle zzN = z2 ? zzKh().zzN(zza) : zza;
                    zzKl().zzMe().zze("Logging event (FE)", str2, zzN);
                    zzKd().zzc(new zzatq(str5, new zzato(zzN), str, j), str3);
                    if (!equals) {
                        for (zzc zzc : this.zzbva) {
                            zzc.zzc(str, str2, new Bundle(zzN), j);
                        }
                    }
                    i++;
                }
                return;
            } else {
                return;
            }
        }
        zzKl().zzMe().log("Event not sent since app measurement is disabled");
    }

    @WorkerThread
    private void zzc(ConditionalUserProperty conditionalUserProperty) {
        zzmR();
        zzob();
        zzac.zzw(conditionalUserProperty);
        zzac.zzdr(conditionalUserProperty.mName);
        if (this.zzbqb.isEnabled()) {
            zzauq com_google_android_gms_internal_zzauq = new zzauq(conditionalUserProperty.mName, 0, null, null);
            try {
                zzKd().zzf(new zzatg(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzauq, conditionalUserProperty.mCreationTimestamp, conditionalUserProperty.mActive, conditionalUserProperty.mTriggerEventName, null, conditionalUserProperty.mTriggerTimeout, null, conditionalUserProperty.mTimeToLive, zzKh().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, conditionalUserProperty.mCreationTimestamp, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        zzKl().zzMe().log("Conditional property not cleared since Firebase Analytics is disabled");
    }

    private List<ConditionalUserProperty> zzo(String str, String str2, String str3) {
        final AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            final String str4 = str;
            final String str5 = str2;
            final String str6 = str3;
            this.zzbqb.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauj zzbve;

                public void run() {
                    this.zzbve.zzbqb.zzKd().zza(atomicReference, str4, str5, str6);
                }
            });
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzKl().zzMb().zze("Interrupted waiting for get conditional user properties", str, e);
            }
        }
        List<zzatg> list = (List) atomicReference.get();
        if (list == null) {
            zzKl().zzMb().zzj("Timed out waiting for get conditional user properties", str);
            return Collections.emptyList();
        }
        List<ConditionalUserProperty> arrayList = new ArrayList(list.size());
        for (zzatg com_google_android_gms_internal_zzatg : list) {
            ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
            conditionalUserProperty.mAppId = str;
            conditionalUserProperty.mOrigin = str2;
            conditionalUserProperty.mCreationTimestamp = com_google_android_gms_internal_zzatg.zzbqY;
            conditionalUserProperty.mName = com_google_android_gms_internal_zzatg.zzbqX.name;
            conditionalUserProperty.mValue = com_google_android_gms_internal_zzatg.zzbqX.getValue();
            conditionalUserProperty.mActive = com_google_android_gms_internal_zzatg.zzbqZ;
            conditionalUserProperty.mTriggerEventName = com_google_android_gms_internal_zzatg.zzbra;
            if (com_google_android_gms_internal_zzatg.zzbrb != null) {
                conditionalUserProperty.mTimedOutEventName = com_google_android_gms_internal_zzatg.zzbrb.name;
                if (com_google_android_gms_internal_zzatg.zzbrb.zzbrH != null) {
                    conditionalUserProperty.mTimedOutEventParams = com_google_android_gms_internal_zzatg.zzbrb.zzbrH.zzLW();
                }
            }
            conditionalUserProperty.mTriggerTimeout = com_google_android_gms_internal_zzatg.zzbrc;
            if (com_google_android_gms_internal_zzatg.zzbrd != null) {
                conditionalUserProperty.mTriggeredEventName = com_google_android_gms_internal_zzatg.zzbrd.name;
                if (com_google_android_gms_internal_zzatg.zzbrd.zzbrH != null) {
                    conditionalUserProperty.mTriggeredEventParams = com_google_android_gms_internal_zzatg.zzbrd.zzbrH.zzLW();
                }
            }
            conditionalUserProperty.mTriggeredTimestamp = com_google_android_gms_internal_zzatg.zzbqX.zzbwf;
            conditionalUserProperty.mTimeToLive = com_google_android_gms_internal_zzatg.zzbre;
            if (com_google_android_gms_internal_zzatg.zzbrf != null) {
                conditionalUserProperty.mExpiredEventName = com_google_android_gms_internal_zzatg.zzbrf.name;
                if (com_google_android_gms_internal_zzatg.zzbrf.zzbrH != null) {
                    conditionalUserProperty.mExpiredEventParams = com_google_android_gms_internal_zzatg.zzbrf.zzbrH.zzLW();
                }
            }
            arrayList.add(conditionalUserProperty);
        }
        return arrayList;
    }

    public void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        zzJW();
        zza(null, str, str2, bundle);
    }

    public void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        zzac.zzdr(str);
        zzJV();
        zza(str, str2, str3, bundle);
    }

    public Task<String> getAppInstanceId() {
        try {
            String zzMm = zzKm().zzMm();
            return zzMm != null ? Tasks.forResult(zzMm) : Tasks.call(zzKk().zzMs(), new Callable<String>(this) {
                final /* synthetic */ zzauj zzbve;

                {
                    this.zzbve = r1;
                }

                public /* synthetic */ Object call() throws Exception {
                    return zzbY();
                }

                public String zzbY() throws Exception {
                    String zzMm = this.zzbve.zzKm().zzMm();
                    if (zzMm == null) {
                        zzMm = this.zzbve.zzKa().zzar(120000);
                        if (zzMm == null) {
                            throw new TimeoutException();
                        }
                        this.zzbve.zzKm().zzfJ(zzMm);
                    }
                    return zzMm;
                }
            });
        } catch (Exception e) {
            zzKl().zzMb().log("Failed to schedule task for getAppInstanceId");
            return Tasks.forException(e);
        }
    }

    public List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        zzJW();
        return zzo(null, str, str2);
    }

    public List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        zzac.zzdr(str);
        zzJV();
        return zzo(str, str2, str3);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public int getMaxUserProperties(String str) {
        zzac.zzdr(str);
        return zzKn().zzKZ();
    }

    public Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        zzJW();
        return zzb(null, str, str2, z);
    }

    public Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        zzac.zzdr(str);
        zzJV();
        return zzb(str, str2, str3, z);
    }

    public void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        zzac.zzw(conditionalUserProperty);
        zzJW();
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            zzKl().zzMb().log("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        zzac.zzw(conditionalUserProperty);
        zzac.zzdr(conditionalUserProperty.mAppId);
        zzJV();
        zza(new ConditionalUserProperty(conditionalUserProperty));
    }

    public void setMeasurementEnabled(final boolean z) {
        zzob();
        zzJW();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zzaL(z);
            }
        });
    }

    public void setMinimumSessionDuration(final long j) {
        zzJW();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zzKm().zzbtn.set(j);
                this.zzbve.zzKl().zzMe().zzj("Minimum session duration set", Long.valueOf(j));
            }
        });
    }

    public void setSessionTimeoutDuration(final long j) {
        zzJW();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zzKm().zzbto.set(j);
                this.zzbve.zzKl().zzMe().zzj("Session timeout duration set", Long.valueOf(j));
            }
        });
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

    @TargetApi(14)
    public void zzMS() {
        if (getContext().getApplicationContext() instanceof Application) {
            Application application = (Application) getContext().getApplicationContext();
            if (this.zzbuY == null) {
                this.zzbuY = new zza();
            }
            application.unregisterActivityLifecycleCallbacks(this.zzbuY);
            application.registerActivityLifecycleCallbacks(this.zzbuY);
            zzKl().zzMf().log("Registered activity lifecycle callback");
        }
    }

    @WorkerThread
    public void zzMT() {
        zzmR();
        zzJW();
        zzob();
        if (this.zzbqb.zzMu()) {
            zzKd().zzMT();
            String zzMp = zzKm().zzMp();
            if (!TextUtils.isEmpty(zzMp) && !zzMp.equals(zzKc().zzLS())) {
                Bundle bundle = new Bundle();
                bundle.putString("_po", zzMp);
                zze("auto", "_ou", bundle);
            }
        }
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        zzmR();
        zzJW();
        zzob();
        if (!(com_google_android_gms_measurement_AppMeasurement_zzb == null || com_google_android_gms_measurement_AppMeasurement_zzb == this.zzbuZ)) {
            zzac.zza(this.zzbuZ == null, (Object) "EventInterceptor already set.");
        }
        this.zzbuZ = com_google_android_gms_measurement_AppMeasurement_zzb;
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        zzJW();
        zzob();
        zzac.zzw(com_google_android_gms_measurement_AppMeasurement_zzc);
        if (!this.zzbva.add(com_google_android_gms_measurement_AppMeasurement_zzc)) {
            zzKl().zzMb().log("OnEventListener already registered");
        }
    }

    protected void zza(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        final Bundle zzM = zzM(bundle);
        final String str4 = str;
        final String str5 = str2;
        final long j2 = j;
        final boolean z4 = z;
        final boolean z5 = z2;
        final boolean z6 = z3;
        final String str6 = str3;
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zzb(str4, str5, j2, zzM, z4, z5, z6, str6);
            }
        });
    }

    void zza(String str, String str2, long j, Object obj) {
        final String str3 = str;
        final String str4 = str2;
        final Object obj2 = obj;
        final long j2 = j;
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauj zzbve;

            public void run() {
                this.zzbve.zza(str3, str4, obj2, j2);
            }
        });
    }

    public void zza(String str, String str2, Bundle bundle, boolean z) {
        zzJW();
        boolean z2 = this.zzbuZ == null || zzaut.zzgd(str2);
        zza(str, str2, bundle, true, z2, z, null);
    }

    public List<zzauq> zzaM(final boolean z) {
        zzJW();
        zzob();
        zzKl().zzMe().log("Fetching user attributes (FE)");
        final AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzbqb.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauj zzbve;

                public void run() {
                    this.zzbve.zzKd().zza(atomicReference, z);
                }
            });
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzKl().zzMb().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzauq> list = (List) atomicReference.get();
        if (list != null) {
            return list;
        }
        zzKl().zzMb().log("Timed out waiting for get user properties");
        return Collections.emptyList();
    }

    @Nullable
    String zzar(long j) {
        if (zzKk().zzMr()) {
            zzKl().zzLZ().log("Cannot retrieve app instance id from analytics worker thread");
            return null;
        } else if (zzKk().zzbc()) {
            zzKl().zzLZ().log("Cannot retrieve app instance id from main thread");
            return null;
        } else {
            long elapsedRealtime = zznR().elapsedRealtime();
            String zzas = zzas(j);
            elapsedRealtime = zznR().elapsedRealtime() - elapsedRealtime;
            return (zzas != null || elapsedRealtime >= j) ? zzas : zzas(j - elapsedRealtime);
        }
    }

    @Nullable
    String zzas(long j) {
        final AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauj zzbve;

                public void run() {
                    this.zzbve.zzKd().zza(atomicReference);
                }
            });
            try {
                atomicReference.wait(j);
            } catch (InterruptedException e) {
                zzKl().zzMb().log("Interrupted waiting for app instance id");
                return null;
            }
        }
        return (String) atomicReference.get();
    }

    public void zzd(String str, String str2, Bundle bundle, long j) {
        zzJW();
        zza(str, str2, j, bundle, false, true, true, null);
    }

    public void zzd(String str, String str2, Object obj) {
        int i = 0;
        zzac.zzdr(str);
        long currentTimeMillis = zznR().currentTimeMillis();
        int zzfX = zzKh().zzfX(str2);
        String zza;
        if (zzfX != 0) {
            zza = zzKh().zza(str2, zzKn().zzKN(), true);
            if (str2 != null) {
                i = str2.length();
            }
            this.zzbqb.zzKh().zza(zzfX, "_ev", zza, i);
        } else if (obj != null) {
            zzfX = zzKh().zzl(str2, obj);
            if (zzfX != 0) {
                zza = zzKh().zza(str2, zzKn().zzKN(), true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i = String.valueOf(obj).length();
                }
                this.zzbqb.zzKh().zza(zzfX, "_ev", zza, i);
                return;
            }
            Object zzm = zzKh().zzm(str2, obj);
            if (zzm != null) {
                zza(str, str2, currentTimeMillis, zzm);
            }
        } else {
            zza(str, str2, currentTimeMillis, null);
        }
    }

    public void zze(String str, String str2, Bundle bundle) {
        zzJW();
        boolean z = this.zzbuZ == null || zzaut.zzgd(str2);
        zza(str, str2, bundle, true, z, false, null);
    }

    @WorkerThread
    public void zzf(Class<?> cls) {
        try {
            cls.getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{getContext()});
        } catch (Exception e) {
            zzKl().zzMb().zzj("Failed to invoke Tag Manager's initialize() method", e);
        }
    }

    @Nullable
    @WorkerThread
    public synchronized String zzfQ(String str) {
        String zzar;
        zzob();
        zzJW();
        if (str == null || !str.equals(this.zzbvd)) {
            zzar = zzar(30000);
            if (zzar == null) {
                zzar = null;
            } else {
                this.zzbvd = str;
                this.zzbvc = zzar;
                zzar = this.zzbvc;
            }
        } else {
            zzar = this.zzbvc;
        }
        return zzar;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
