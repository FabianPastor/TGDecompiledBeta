package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.EventInterceptor;
import com.google.android.gms.measurement.AppMeasurement.OnEventListener;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;

public final class zzchl extends zzchj {
    protected zzchy zzbto;
    private EventInterceptor zzbtp;
    private final Set<OnEventListener> zzbtq = new CopyOnWriteArraySet();
    private boolean zzbtr;
    private final AtomicReference<String> zzbts = new AtomicReference();

    protected zzchl(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    public static int getMaxUserProperties(String str) {
        zzbo.zzcF(str);
        return zzcem.zzxu();
    }

    private final void zza(ConditionalUserProperty conditionalUserProperty) {
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        zzbo.zzcF(conditionalUserProperty.mOrigin);
        zzbo.zzu(conditionalUserProperty.mValue);
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        String str = conditionalUserProperty.mName;
        Object obj = conditionalUserProperty.mValue;
        if (super.zzwB().zzes(str) != 0) {
            super.zzwF().zzyx().zzj("Invalid conditional user property name", super.zzwA().zzdY(str));
        } else if (super.zzwB().zzl(str, obj) != 0) {
            super.zzwF().zzyx().zze("Invalid conditional user property value", super.zzwA().zzdY(str), obj);
        } else {
            Object zzm = super.zzwB().zzm(str, obj);
            if (zzm == null) {
                super.zzwF().zzyx().zze("Unable to normalize conditional user property value", super.zzwA().zzdY(str), obj);
                return;
            }
            conditionalUserProperty.mValue = zzm;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (TextUtils.isEmpty(conditionalUserProperty.mTriggerEventName) || (j <= zzcem.zzxw() && j >= 1)) {
                j = conditionalUserProperty.mTimeToLive;
                if (j > zzcem.zzxx() || j < 1) {
                    super.zzwF().zzyx().zze("Invalid conditional user property time to live", super.zzwA().zzdY(str), Long.valueOf(j));
                    return;
                } else {
                    super.zzwE().zzj(new zzchn(this, conditionalUserProperty));
                    return;
                }
            }
            super.zzwF().zzyx().zze("Invalid conditional user property timeout", super.zzwA().zzdY(str), Long.valueOf(j));
        }
    }

    private final void zza(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        Bundle bundle2;
        if (bundle == null) {
            bundle2 = new Bundle();
        } else {
            bundle2 = new Bundle(bundle);
            for (String str4 : bundle2.keySet()) {
                Object obj = bundle2.get(str4);
                if (obj instanceof Bundle) {
                    bundle2.putBundle(str4, new Bundle((Bundle) obj));
                } else if (obj instanceof Parcelable[]) {
                    Parcelable[] parcelableArr = (Parcelable[]) obj;
                    for (r4 = 0; r4 < parcelableArr.length; r4++) {
                        if (parcelableArr[r4] instanceof Bundle) {
                            parcelableArr[r4] = new Bundle((Bundle) parcelableArr[r4]);
                        }
                    }
                } else if (obj instanceof ArrayList) {
                    ArrayList arrayList = (ArrayList) obj;
                    for (r4 = 0; r4 < arrayList.size(); r4++) {
                        Object obj2 = arrayList.get(r4);
                        if (obj2 instanceof Bundle) {
                            arrayList.set(r4, new Bundle((Bundle) obj2));
                        }
                    }
                }
            }
        }
        super.zzwE().zzj(new zzcht(this, str, str2, j, bundle2, z, z2, z3, str3));
    }

    private final void zza(String str, String str2, long j, Object obj) {
        super.zzwE().zzj(new zzchu(this, str, str2, obj, j));
    }

    private final void zza(String str, String str2, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zza(str, str2, super.zzkq().currentTimeMillis(), bundle, true, z2, z3, null);
    }

    @WorkerThread
    private final void zza(String str, String str2, Object obj, long j) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        super.zzjC();
        super.zzwp();
        zzkD();
        if (!this.zzboe.isEnabled()) {
            super.zzwF().zzyC().log("User property not set since app measurement is disabled");
        } else if (this.zzboe.zzyP()) {
            super.zzwF().zzyC().zze("Setting user property (FE)", super.zzwA().zzdW(str2), obj);
            super.zzww().zzb(new zzcji(str2, j, obj, str));
        }
    }

    private final void zza(String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        zzbo.zzcF(str2);
        ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
        conditionalUserProperty.mAppId = str;
        conditionalUserProperty.mName = str2;
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        if (str3 != null) {
            conditionalUserProperty.mExpiredEventName = str3;
            conditionalUserProperty.mExpiredEventParams = bundle;
        }
        super.zzwE().zzj(new zzcho(this, conditionalUserProperty));
    }

    @Nullable
    private final String zzad(long j) {
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            super.zzwE().zzj(new zzchx(this, atomicReference));
            try {
                atomicReference.wait(j);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().log("Interrupted waiting for app instance id");
                return null;
            }
        }
        return (String) atomicReference.get();
    }

    @WorkerThread
    private final void zzan(boolean z) {
        super.zzjC();
        super.zzwp();
        zzkD();
        super.zzwF().zzyC().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        super.zzwG().setMeasurementEnabled(z);
        super.zzww().zzzj();
    }

    private final Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot get user properties from analytics worker thread");
            return Collections.emptyMap();
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot get user properties from main thread");
            return Collections.emptyMap();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzboe.zzwE().zzj(new zzchq(this, atomicReference, str, str2, str3, z));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzcji> list = (List) atomicReference.get();
        if (list == null) {
            super.zzwF().zzyz().log("Timed out waiting for get user properties");
            return Collections.emptyMap();
        }
        Map<String, Object> arrayMap = new ArrayMap(list.size());
        for (zzcji com_google_android_gms_internal_zzcji : list) {
            arrayMap.put(com_google_android_gms_internal_zzcji.name, com_google_android_gms_internal_zzcji.getValue());
        }
        return arrayMap;
    }

    @WorkerThread
    private final void zzb(ConditionalUserProperty conditionalUserProperty) {
        super.zzjC();
        zzkD();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        zzbo.zzcF(conditionalUserProperty.mOrigin);
        zzbo.zzu(conditionalUserProperty.mValue);
        if (this.zzboe.isEnabled()) {
            zzcji com_google_android_gms_internal_zzcji = new zzcji(conditionalUserProperty.mName, conditionalUserProperty.mTriggeredTimestamp, conditionalUserProperty.mValue, conditionalUserProperty.mOrigin);
            try {
                zzcez zza = super.zzwB().zza(conditionalUserProperty.mTriggeredEventName, conditionalUserProperty.mTriggeredEventParams, conditionalUserProperty.mOrigin, 0, true, false);
                super.zzww().zzf(new zzcek(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzcji, conditionalUserProperty.mCreationTimestamp, false, conditionalUserProperty.mTriggerEventName, super.zzwB().zza(conditionalUserProperty.mTimedOutEventName, conditionalUserProperty.mTimedOutEventParams, conditionalUserProperty.mOrigin, 0, true, false), conditionalUserProperty.mTriggerTimeout, zza, conditionalUserProperty.mTimeToLive, super.zzwB().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, 0, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        super.zzwF().zzyC().log("Conditional property not sent since Firebase Analytics is disabled");
    }

    @WorkerThread
    private final void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzbo.zzu(bundle);
        super.zzjC();
        zzkD();
        if (this.zzboe.isEnabled()) {
            if (!this.zzbtr) {
                this.zzbtr = true;
                try {
                    try {
                        Class.forName("com.google.android.gms.tagmanager.TagManagerService").getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{super.getContext()});
                    } catch (Exception e) {
                        super.zzwF().zzyz().zzj("Failed to invoke Tag Manager's initialize() method", e);
                    }
                } catch (ClassNotFoundException e2) {
                    super.zzwF().zzyB().log("Tag Manager is not found and thus will not be used");
                }
            }
            boolean equals = "am".equals(str);
            boolean zzex = zzcjl.zzex(str2);
            if (z && this.zzbtp != null && !zzex && !equals) {
                super.zzwF().zzyC().zze("Passing event to registered event handler (FE)", super.zzwA().zzdW(str2), super.zzwA().zzA(bundle));
                this.zzbtp.interceptEvent(str, str2, bundle, j);
                return;
            } else if (this.zzboe.zzyP()) {
                int zzeq = super.zzwB().zzeq(str2);
                if (zzeq != 0) {
                    super.zzwB();
                    this.zzboe.zzwB().zza(str3, zzeq, "_ev", zzcjl.zza(str2, zzcem.zzxh(), true), str2 != null ? str2.length() : 0);
                    return;
                }
                int i;
                Bundle zza;
                List singletonList = Collections.singletonList("_o");
                Bundle zza2 = super.zzwB().zza(str2, bundle, singletonList, z3, true);
                List arrayList = new ArrayList();
                arrayList.add(zza2);
                long nextLong = super.zzwB().zzzt().nextLong();
                int i2 = 0;
                String[] strArr = (String[]) zza2.keySet().toArray(new String[bundle.size()]);
                Arrays.sort(strArr);
                int length = strArr.length;
                int i3 = 0;
                while (i3 < length) {
                    int length2;
                    String str4 = strArr[i3];
                    Object obj = zza2.get(str4);
                    super.zzwB();
                    Bundle[] zzC = zzcjl.zzC(obj);
                    if (zzC != null) {
                        zza2.putInt(str4, zzC.length);
                        for (i = 0; i < zzC.length; i++) {
                            zza = super.zzwB().zza("_ep", zzC[i], singletonList, z3, false);
                            zza.putString("_en", str2);
                            zza.putLong("_eid", nextLong);
                            zza.putString("_gn", str4);
                            zza.putInt("_ll", zzC.length);
                            zza.putInt("_i", i);
                            arrayList.add(zza);
                        }
                        length2 = zzC.length + i2;
                    } else {
                        length2 = i2;
                    }
                    i3++;
                    i2 = length2;
                }
                if (i2 != 0) {
                    zza2.putLong("_eid", nextLong);
                    zza2.putInt("_epc", i2);
                }
                zzcem.zzxE();
                zzb zzzh = super.zzwx().zzzh();
                if (!(zzzh == null || zza2.containsKey("_sc"))) {
                    zzzh.zzbtS = true;
                }
                i = 0;
                while (i < arrayList.size()) {
                    zza = (Bundle) arrayList.get(i);
                    String str5 = (i != 0 ? 1 : null) != null ? "_ep" : str2;
                    zza.putString("_o", str);
                    if (!zza.containsKey("_sc")) {
                        zzchz.zza(zzzh, zza);
                    }
                    Bundle zzB = z2 ? super.zzwB().zzB(zza) : zza;
                    super.zzwF().zzyC().zze("Logging event (FE)", super.zzwA().zzdW(str2), super.zzwA().zzA(zzB));
                    super.zzww().zzc(new zzcez(str5, new zzcew(zzB), str, j), str3);
                    if (!equals) {
                        for (OnEventListener onEvent : this.zzbtq) {
                            onEvent.onEvent(str, str2, new Bundle(zzB), j);
                        }
                    }
                    i++;
                }
                zzcem.zzxE();
                if (super.zzwx().zzzh() != null && Event.APP_EXCEPTION.equals(str2)) {
                    super.zzwD().zzap(true);
                    return;
                }
                return;
            } else {
                return;
            }
        }
        super.zzwF().zzyC().log("Event not sent since app measurement is disabled");
    }

    @WorkerThread
    private final void zzc(ConditionalUserProperty conditionalUserProperty) {
        super.zzjC();
        zzkD();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        if (this.zzboe.isEnabled()) {
            zzcji com_google_android_gms_internal_zzcji = new zzcji(conditionalUserProperty.mName, 0, null, null);
            try {
                super.zzww().zzf(new zzcek(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzcji, conditionalUserProperty.mCreationTimestamp, conditionalUserProperty.mActive, conditionalUserProperty.mTriggerEventName, null, conditionalUserProperty.mTriggerTimeout, null, conditionalUserProperty.mTimeToLive, super.zzwB().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, conditionalUserProperty.mCreationTimestamp, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        super.zzwF().zzyC().log("Conditional property not cleared since Firebase Analytics is disabled");
    }

    private final List<ConditionalUserProperty> zzl(String str, String str2, String str3) {
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot get conditional user properties from analytics worker thread");
            return Collections.emptyList();
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot get conditional user properties from main thread");
            return Collections.emptyList();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzboe.zzwE().zzj(new zzchp(this, atomicReference, str, str2, str3));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().zze("Interrupted waiting for get conditional user properties", str, e);
            }
        }
        List<zzcek> list = (List) atomicReference.get();
        if (list == null) {
            super.zzwF().zzyz().zzj("Timed out waiting for get conditional user properties", str);
            return Collections.emptyList();
        }
        List<ConditionalUserProperty> arrayList = new ArrayList(list.size());
        for (zzcek com_google_android_gms_internal_zzcek : list) {
            ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
            conditionalUserProperty.mAppId = str;
            conditionalUserProperty.mOrigin = str2;
            conditionalUserProperty.mCreationTimestamp = com_google_android_gms_internal_zzcek.zzbpe;
            conditionalUserProperty.mName = com_google_android_gms_internal_zzcek.zzbpd.name;
            conditionalUserProperty.mValue = com_google_android_gms_internal_zzcek.zzbpd.getValue();
            conditionalUserProperty.mActive = com_google_android_gms_internal_zzcek.zzbpf;
            conditionalUserProperty.mTriggerEventName = com_google_android_gms_internal_zzcek.zzbpg;
            if (com_google_android_gms_internal_zzcek.zzbph != null) {
                conditionalUserProperty.mTimedOutEventName = com_google_android_gms_internal_zzcek.zzbph.name;
                if (com_google_android_gms_internal_zzcek.zzbph.zzbpM != null) {
                    conditionalUserProperty.mTimedOutEventParams = com_google_android_gms_internal_zzcek.zzbph.zzbpM.zzyt();
                }
            }
            conditionalUserProperty.mTriggerTimeout = com_google_android_gms_internal_zzcek.zzbpi;
            if (com_google_android_gms_internal_zzcek.zzbpj != null) {
                conditionalUserProperty.mTriggeredEventName = com_google_android_gms_internal_zzcek.zzbpj.name;
                if (com_google_android_gms_internal_zzcek.zzbpj.zzbpM != null) {
                    conditionalUserProperty.mTriggeredEventParams = com_google_android_gms_internal_zzcek.zzbpj.zzbpM.zzyt();
                }
            }
            conditionalUserProperty.mTriggeredTimestamp = com_google_android_gms_internal_zzcek.zzbpd.zzbuy;
            conditionalUserProperty.mTimeToLive = com_google_android_gms_internal_zzcek.zzbpk;
            if (com_google_android_gms_internal_zzcek.zzbpl != null) {
                conditionalUserProperty.mExpiredEventName = com_google_android_gms_internal_zzcek.zzbpl.name;
                if (com_google_android_gms_internal_zzcek.zzbpl.zzbpM != null) {
                    conditionalUserProperty.mExpiredEventParams = com_google_android_gms_internal_zzcek.zzbpl.zzbpM.zzyt();
                }
            }
            arrayList.add(conditionalUserProperty);
        }
        return arrayList;
    }

    public final void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        super.zzwp();
        zza(null, str, str2, bundle);
    }

    public final void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        zzbo.zzcF(str);
        super.zzwo();
        zza(str, str2, str3, bundle);
    }

    public final Task<String> getAppInstanceId() {
        try {
            String zzyH = super.zzwG().zzyH();
            return zzyH != null ? Tasks.forResult(zzyH) : Tasks.call(super.zzwE().zzyN(), new zzchw(this));
        } catch (Exception e) {
            super.zzwF().zzyz().log("Failed to schedule task for getAppInstanceId");
            return Tasks.forException(e);
        }
    }

    public final List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        super.zzwp();
        return zzl(null, str, str2);
    }

    public final List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        zzbo.zzcF(str);
        super.zzwo();
        return zzl(str, str2, str3);
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        super.zzwp();
        return zzb(null, str, str2, z);
    }

    public final Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        zzbo.zzcF(str);
        super.zzwo();
        return zzb(str, str2, str3, z);
    }

    public final void registerOnMeasurementEventListener(OnEventListener onEventListener) {
        super.zzwp();
        zzkD();
        zzbo.zzu(onEventListener);
        if (!this.zzbtq.add(onEventListener)) {
            super.zzwF().zzyz().log("OnEventListener already registered");
        }
    }

    public final void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        zzbo.zzu(conditionalUserProperty);
        super.zzwp();
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            super.zzwF().zzyz().log("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public final void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mAppId);
        super.zzwo();
        zza(new ConditionalUserProperty(conditionalUserProperty));
    }

    @WorkerThread
    public final void setEventInterceptor(EventInterceptor eventInterceptor) {
        super.zzjC();
        super.zzwp();
        zzkD();
        if (!(eventInterceptor == null || eventInterceptor == this.zzbtp)) {
            zzbo.zza(this.zzbtp == null, (Object) "EventInterceptor already set.");
        }
        this.zzbtp = eventInterceptor;
    }

    public final void setMeasurementEnabled(boolean z) {
        zzkD();
        super.zzwp();
        super.zzwE().zzj(new zzchm(this, z));
    }

    public final void setMinimumSessionDuration(long j) {
        super.zzwp();
        super.zzwE().zzj(new zzchr(this, j));
    }

    public final void setSessionTimeoutDuration(long j) {
        super.zzwp();
        super.zzwE().zzj(new zzchs(this, j));
    }

    public final void unregisterOnMeasurementEventListener(OnEventListener onEventListener) {
        super.zzwp();
        zzkD();
        zzbo.zzu(onEventListener);
        if (!this.zzbtq.remove(onEventListener)) {
            super.zzwF().zzyz().log("OnEventListener had not been registered");
        }
    }

    public final void zza(String str, String str2, Bundle bundle, long j) {
        super.zzwp();
        zza(str, str2, j, bundle, false, true, true, null);
    }

    public final void zza(String str, String str2, Bundle bundle, boolean z) {
        super.zzwp();
        boolean z2 = this.zzbtp == null || zzcjl.zzex(str2);
        zza(str, str2, bundle, true, z2, true, null);
    }

    @Nullable
    final String zzac(long j) {
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot retrieve app instance id from analytics worker thread");
            return null;
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot retrieve app instance id from main thread");
            return null;
        }
        long elapsedRealtime = super.zzkq().elapsedRealtime();
        String zzad = zzad(120000);
        elapsedRealtime = super.zzkq().elapsedRealtime() - elapsedRealtime;
        return (zzad != null || elapsedRealtime >= 120000) ? zzad : zzad(120000 - elapsedRealtime);
    }

    public final List<zzcji> zzao(boolean z) {
        super.zzwp();
        zzkD();
        super.zzwF().zzyC().log("Fetching user attributes (FE)");
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot get all user properties from analytics worker thread");
            return Collections.emptyList();
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot get all user properties from main thread");
            return Collections.emptyList();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzboe.zzwE().zzj(new zzchv(this, atomicReference, z));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzcji> list = (List) atomicReference.get();
        if (list != null) {
            return list;
        }
        super.zzwF().zzyz().log("Timed out waiting for get user properties");
        return Collections.emptyList();
    }

    public final void zzb(String str, String str2, Object obj) {
        int i = 0;
        zzbo.zzcF(str);
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        int zzes = super.zzwB().zzes(str2);
        String zza;
        if (zzes != 0) {
            super.zzwB();
            zza = zzcjl.zza(str2, zzcem.zzxi(), true);
            if (str2 != null) {
                i = str2.length();
            }
            this.zzboe.zzwB().zza(zzes, "_ev", zza, i);
        } else if (obj != null) {
            zzes = super.zzwB().zzl(str2, obj);
            if (zzes != 0) {
                super.zzwB();
                zza = zzcjl.zza(str2, zzcem.zzxi(), true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i = String.valueOf(obj).length();
                }
                this.zzboe.zzwB().zza(zzes, "_ev", zza, i);
                return;
            }
            Object zzm = super.zzwB().zzm(str2, obj);
            if (zzm != null) {
                zza(str, str2, currentTimeMillis, zzm);
            }
        } else {
            zza(str, str2, currentTimeMillis, null);
        }
    }

    public final void zzd(String str, String str2, Bundle bundle) {
        super.zzwp();
        boolean z = this.zzbtp == null || zzcjl.zzex(str2);
        zza(str, str2, bundle, true, z, false, null);
    }

    final void zzee(@Nullable String str) {
        this.zzbts.set(str);
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
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

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }

    @Nullable
    public final String zzyH() {
        super.zzwp();
        return (String) this.zzbts.get();
    }
}
