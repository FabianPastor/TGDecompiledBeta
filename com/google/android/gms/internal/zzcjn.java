package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
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

public final class zzcjn extends zzcjl {
    protected zzckb zzjgx;
    private EventInterceptor zzjgy;
    private final Set<OnEventListener> zzjgz = new CopyOnWriteArraySet();
    private boolean zzjha;
    private final AtomicReference<String> zzjhb = new AtomicReference();

    protected zzcjn(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final void zza(ConditionalUserProperty conditionalUserProperty) {
        long currentTimeMillis = zzws().currentTimeMillis();
        zzbq.checkNotNull(conditionalUserProperty);
        zzbq.zzgm(conditionalUserProperty.mName);
        zzbq.zzgm(conditionalUserProperty.mOrigin);
        zzbq.checkNotNull(conditionalUserProperty.mValue);
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        String str = conditionalUserProperty.mName;
        Object obj = conditionalUserProperty.mValue;
        if (zzawu().zzkd(str) != 0) {
            zzawy().zzazd().zzj("Invalid conditional user property name", zzawt().zzjj(str));
        } else if (zzawu().zzl(str, obj) != 0) {
            zzawy().zzazd().zze("Invalid conditional user property value", zzawt().zzjj(str), obj);
        } else {
            Object zzm = zzawu().zzm(str, obj);
            if (zzm == null) {
                zzawy().zzazd().zze("Unable to normalize conditional user property value", zzawt().zzjj(str), obj);
                return;
            }
            conditionalUserProperty.mValue = zzm;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (TextUtils.isEmpty(conditionalUserProperty.mTriggerEventName) || (j <= 15552000000L && j >= 1)) {
                j = conditionalUserProperty.mTimeToLive;
                if (j > 15552000000L || j < 1) {
                    zzawy().zzazd().zze("Invalid conditional user property time to live", zzawt().zzjj(str), Long.valueOf(j));
                    return;
                } else {
                    zzawx().zzg(new zzcjp(this, conditionalUserProperty));
                    return;
                }
            }
            zzawy().zzazd().zze("Invalid conditional user property timeout", zzawt().zzjj(str), Long.valueOf(j));
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
        zzawx().zzg(new zzcjv(this, str, str2, j, bundle2, z, z2, z3, str3));
    }

    private final void zza(String str, String str2, long j, Object obj) {
        zzawx().zzg(new zzcjw(this, str, str2, obj, j));
    }

    private final void zza(String str, String str2, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zza(str, str2, zzws().currentTimeMillis(), bundle, true, z2, z3, null);
    }

    private final void zza(String str, String str2, Object obj, long j) {
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        if (!this.zziwf.isEnabled()) {
            zzawy().zzazi().log("User property not set since app measurement is disabled");
        } else if (this.zziwf.zzazv()) {
            zzawy().zzazi().zze("Setting user property (FE)", zzawt().zzjh(str2), obj);
            zzawp().zzb(new zzcln(str2, j, obj, str));
        }
    }

    private final void zza(String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = zzws().currentTimeMillis();
        zzbq.zzgm(str2);
        ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
        conditionalUserProperty.mAppId = str;
        conditionalUserProperty.mName = str2;
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        if (str3 != null) {
            conditionalUserProperty.mExpiredEventName = str3;
            conditionalUserProperty.mExpiredEventParams = bundle;
        }
        zzawx().zzg(new zzcjq(this, conditionalUserProperty));
    }

    private final Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        if (zzawx().zzazs()) {
            zzawy().zzazd().log("Cannot get user properties from analytics worker thread");
            return Collections.emptyMap();
        }
        zzawx();
        if (zzcih.zzau()) {
            zzawy().zzazd().log("Cannot get user properties from main thread");
            return Collections.emptyMap();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zziwf.zzawx().zzg(new zzcjs(this, atomicReference, str, str2, str3, z));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzawy().zzazf().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzcln> list = (List) atomicReference.get();
        if (list == null) {
            zzawy().zzazf().log("Timed out waiting for get user properties");
            return Collections.emptyMap();
        }
        Map<String, Object> arrayMap = new ArrayMap(list.size());
        for (zzcln com_google_android_gms_internal_zzcln : list) {
            arrayMap.put(com_google_android_gms_internal_zzcln.name, com_google_android_gms_internal_zzcln.getValue());
        }
        return arrayMap;
    }

    private final void zzb(ConditionalUserProperty conditionalUserProperty) {
        zzve();
        zzxf();
        zzbq.checkNotNull(conditionalUserProperty);
        zzbq.zzgm(conditionalUserProperty.mName);
        zzbq.zzgm(conditionalUserProperty.mOrigin);
        zzbq.checkNotNull(conditionalUserProperty.mValue);
        if (this.zziwf.isEnabled()) {
            zzcln com_google_android_gms_internal_zzcln = new zzcln(conditionalUserProperty.mName, conditionalUserProperty.mTriggeredTimestamp, conditionalUserProperty.mValue, conditionalUserProperty.mOrigin);
            try {
                zzcha zza = zzawu().zza(conditionalUserProperty.mTriggeredEventName, conditionalUserProperty.mTriggeredEventParams, conditionalUserProperty.mOrigin, 0, true, false);
                zzawp().zzf(new zzcgl(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzcln, conditionalUserProperty.mCreationTimestamp, false, conditionalUserProperty.mTriggerEventName, zzawu().zza(conditionalUserProperty.mTimedOutEventName, conditionalUserProperty.mTimedOutEventParams, conditionalUserProperty.mOrigin, 0, true, false), conditionalUserProperty.mTriggerTimeout, zza, conditionalUserProperty.mTimeToLive, zzawu().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, 0, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        zzawy().zzazi().log("Conditional property not sent since Firebase Analytics is disabled");
    }

    private final void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzbq.checkNotNull(bundle);
        zzve();
        zzxf();
        if (this.zziwf.isEnabled()) {
            if (!this.zzjha) {
                this.zzjha = true;
                try {
                    try {
                        Class.forName("com.google.android.gms.tagmanager.TagManagerService").getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{getContext()});
                    } catch (Exception e) {
                        zzawy().zzazf().zzj("Failed to invoke Tag Manager's initialize() method", e);
                    }
                } catch (ClassNotFoundException e2) {
                    zzawy().zzazh().log("Tag Manager is not found and thus will not be used");
                }
            }
            boolean equals = "am".equals(str);
            boolean zzki = zzclq.zzki(str2);
            if (z && this.zzjgy != null && !zzki && !equals) {
                zzawy().zzazi().zze("Passing event to registered event handler (FE)", zzawt().zzjh(str2), zzawt().zzx(bundle));
                this.zzjgy.interceptEvent(str, str2, bundle, j);
                return;
            } else if (this.zziwf.zzazv()) {
                int zzkb = zzawu().zzkb(str2);
                if (zzkb != 0) {
                    zzawu();
                    this.zziwf.zzawu().zza(str3, zzkb, "_ev", zzclq.zza(str2, 40, true), str2 != null ? str2.length() : 0);
                    return;
                }
                int i;
                Bundle zza;
                List singletonList = Collections.singletonList("_o");
                Bundle zza2 = zzawu().zza(str2, bundle, singletonList, z3, true);
                List arrayList = new ArrayList();
                arrayList.add(zza2);
                long nextLong = zzawu().zzbaz().nextLong();
                int i2 = 0;
                String[] strArr = (String[]) zza2.keySet().toArray(new String[bundle.size()]);
                Arrays.sort(strArr);
                int length = strArr.length;
                int i3 = 0;
                while (i3 < length) {
                    int length2;
                    String str4 = strArr[i3];
                    Object obj = zza2.get(str4);
                    zzawu();
                    Bundle[] zzaf = zzclq.zzaf(obj);
                    if (zzaf != null) {
                        zza2.putInt(str4, zzaf.length);
                        for (i = 0; i < zzaf.length; i++) {
                            zza = zzawu().zza("_ep", zzaf[i], singletonList, z3, false);
                            zza.putString("_en", str2);
                            zza.putLong("_eid", nextLong);
                            zza.putString("_gn", str4);
                            zza.putInt("_ll", zzaf.length);
                            zza.putInt("_i", i);
                            arrayList.add(zza);
                        }
                        length2 = zzaf.length + i2;
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
                zzb zzbao = zzawq().zzbao();
                if (!(zzbao == null || zza2.containsKey("_sc"))) {
                    zzbao.zzjib = true;
                }
                i = 0;
                while (i < arrayList.size()) {
                    zza = (Bundle) arrayList.get(i);
                    String str5 = (i != 0 ? 1 : null) != null ? "_ep" : str2;
                    zza.putString("_o", str);
                    if (!zza.containsKey("_sc")) {
                        zzckc.zza(zzbao, zza);
                    }
                    Bundle zzy = z2 ? zzawu().zzy(zza) : zza;
                    zzawy().zzazi().zze("Logging event (FE)", zzawt().zzjh(str2), zzawt().zzx(zzy));
                    zzawp().zzc(new zzcha(str5, new zzcgx(zzy), str, j), str3);
                    if (!equals) {
                        for (OnEventListener onEvent : this.zzjgz) {
                            onEvent.onEvent(str, str2, new Bundle(zzy), j);
                        }
                    }
                    i++;
                }
                if (zzawq().zzbao() != null && "_ae".equals(str2)) {
                    zzaww().zzbs(true);
                    return;
                }
                return;
            } else {
                return;
            }
        }
        zzawy().zzazi().log("Event not sent since app measurement is disabled");
    }

    private final void zzbp(boolean z) {
        zzve();
        zzxf();
        zzawy().zzazi().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        zzawz().setMeasurementEnabled(z);
        zzawp().zzbaq();
    }

    private final void zzc(ConditionalUserProperty conditionalUserProperty) {
        zzve();
        zzxf();
        zzbq.checkNotNull(conditionalUserProperty);
        zzbq.zzgm(conditionalUserProperty.mName);
        if (this.zziwf.isEnabled()) {
            zzcln com_google_android_gms_internal_zzcln = new zzcln(conditionalUserProperty.mName, 0, null, null);
            try {
                zzawp().zzf(new zzcgl(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, com_google_android_gms_internal_zzcln, conditionalUserProperty.mCreationTimestamp, conditionalUserProperty.mActive, conditionalUserProperty.mTriggerEventName, null, conditionalUserProperty.mTriggerTimeout, null, conditionalUserProperty.mTimeToLive, zzawu().zza(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, conditionalUserProperty.mCreationTimestamp, true, false)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        zzawy().zzazi().log("Conditional property not cleared since Firebase Analytics is disabled");
    }

    private final List<ConditionalUserProperty> zzk(String str, String str2, String str3) {
        if (zzawx().zzazs()) {
            zzawy().zzazd().log("Cannot get conditional user properties from analytics worker thread");
            return Collections.emptyList();
        }
        zzawx();
        if (zzcih.zzau()) {
            zzawy().zzazd().log("Cannot get conditional user properties from main thread");
            return Collections.emptyList();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zziwf.zzawx().zzg(new zzcjr(this, atomicReference, str, str2, str3));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzawy().zzazf().zze("Interrupted waiting for get conditional user properties", str, e);
            }
        }
        List<zzcgl> list = (List) atomicReference.get();
        if (list == null) {
            zzawy().zzazf().zzj("Timed out waiting for get conditional user properties", str);
            return Collections.emptyList();
        }
        List<ConditionalUserProperty> arrayList = new ArrayList(list.size());
        for (zzcgl com_google_android_gms_internal_zzcgl : list) {
            ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
            conditionalUserProperty.mAppId = str;
            conditionalUserProperty.mOrigin = str2;
            conditionalUserProperty.mCreationTimestamp = com_google_android_gms_internal_zzcgl.zziyh;
            conditionalUserProperty.mName = com_google_android_gms_internal_zzcgl.zziyg.name;
            conditionalUserProperty.mValue = com_google_android_gms_internal_zzcgl.zziyg.getValue();
            conditionalUserProperty.mActive = com_google_android_gms_internal_zzcgl.zziyi;
            conditionalUserProperty.mTriggerEventName = com_google_android_gms_internal_zzcgl.zziyj;
            if (com_google_android_gms_internal_zzcgl.zziyk != null) {
                conditionalUserProperty.mTimedOutEventName = com_google_android_gms_internal_zzcgl.zziyk.name;
                if (com_google_android_gms_internal_zzcgl.zziyk.zzizt != null) {
                    conditionalUserProperty.mTimedOutEventParams = com_google_android_gms_internal_zzcgl.zziyk.zzizt.zzayx();
                }
            }
            conditionalUserProperty.mTriggerTimeout = com_google_android_gms_internal_zzcgl.zziyl;
            if (com_google_android_gms_internal_zzcgl.zziym != null) {
                conditionalUserProperty.mTriggeredEventName = com_google_android_gms_internal_zzcgl.zziym.name;
                if (com_google_android_gms_internal_zzcgl.zziym.zzizt != null) {
                    conditionalUserProperty.mTriggeredEventParams = com_google_android_gms_internal_zzcgl.zziym.zzizt.zzayx();
                }
            }
            conditionalUserProperty.mTriggeredTimestamp = com_google_android_gms_internal_zzcgl.zziyg.zzjji;
            conditionalUserProperty.mTimeToLive = com_google_android_gms_internal_zzcgl.zziyn;
            if (com_google_android_gms_internal_zzcgl.zziyo != null) {
                conditionalUserProperty.mExpiredEventName = com_google_android_gms_internal_zzcgl.zziyo.name;
                if (com_google_android_gms_internal_zzcgl.zziyo.zzizt != null) {
                    conditionalUserProperty.mExpiredEventParams = com_google_android_gms_internal_zzcgl.zziyo.zzizt.zzayx();
                }
            }
            arrayList.add(conditionalUserProperty);
        }
        return arrayList;
    }

    public final void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        zza(null, str, str2, bundle);
    }

    public final void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        zzbq.zzgm(str);
        zzawi();
        zza(str, str2, str3, bundle);
    }

    public final Task<String> getAppInstanceId() {
        try {
            String zzazn = zzawz().zzazn();
            return zzazn != null ? Tasks.forResult(zzazn) : Tasks.call(zzawx().zzazt(), new zzcjy(this));
        } catch (Exception e) {
            zzawy().zzazf().log("Failed to schedule task for getAppInstanceId");
            return Tasks.forException(e);
        }
    }

    public final List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        return zzk(null, str, str2);
    }

    public final List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        zzbq.zzgm(str);
        zzawi();
        return zzk(str, str2, str3);
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        return zzb(null, str, str2, z);
    }

    public final Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        zzbq.zzgm(str);
        zzawi();
        return zzb(str, str2, str3, z);
    }

    public final void registerOnMeasurementEventListener(OnEventListener onEventListener) {
        zzxf();
        zzbq.checkNotNull(onEventListener);
        if (!this.zzjgz.add(onEventListener)) {
            zzawy().zzazf().log("OnEventListener already registered");
        }
    }

    public final void resetAnalyticsData() {
        zzawx().zzg(new zzcka(this));
    }

    public final void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        zzbq.checkNotNull(conditionalUserProperty);
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            zzawy().zzazf().log("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public final void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        zzbq.checkNotNull(conditionalUserProperty);
        zzbq.zzgm(conditionalUserProperty.mAppId);
        zzawi();
        zza(new ConditionalUserProperty(conditionalUserProperty));
    }

    public final void setEventInterceptor(EventInterceptor eventInterceptor) {
        zzve();
        zzxf();
        if (!(eventInterceptor == null || eventInterceptor == this.zzjgy)) {
            zzbq.zza(this.zzjgy == null, "EventInterceptor already set.");
        }
        this.zzjgy = eventInterceptor;
    }

    public final void setMeasurementEnabled(boolean z) {
        zzxf();
        zzawx().zzg(new zzcjo(this, z));
    }

    public final void setMinimumSessionDuration(long j) {
        zzawx().zzg(new zzcjt(this, j));
    }

    public final void setSessionTimeoutDuration(long j) {
        zzawx().zzg(new zzcju(this, j));
    }

    public final void unregisterOnMeasurementEventListener(OnEventListener onEventListener) {
        zzxf();
        zzbq.checkNotNull(onEventListener);
        if (!this.zzjgz.remove(onEventListener)) {
            zzawy().zzazf().log("OnEventListener had not been registered");
        }
    }

    public final void zza(String str, String str2, Bundle bundle, long j) {
        zza(str, str2, j, bundle, false, true, true, null);
    }

    public final void zza(String str, String str2, Bundle bundle, boolean z) {
        boolean z2 = this.zzjgy == null || zzclq.zzki(str2);
        zza(str, str2, bundle, true, z2, true, null);
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

    protected final boolean zzaxz() {
        return false;
    }

    public final String zzazn() {
        return (String) this.zzjhb.get();
    }

    public final void zzb(String str, String str2, Object obj) {
        int i = 0;
        zzbq.zzgm(str);
        long currentTimeMillis = zzws().currentTimeMillis();
        int zzkd = zzawu().zzkd(str2);
        String zza;
        if (zzkd != 0) {
            zzawu();
            zza = zzclq.zza(str2, 24, true);
            if (str2 != null) {
                i = str2.length();
            }
            this.zziwf.zzawu().zza(zzkd, "_ev", zza, i);
        } else if (obj != null) {
            zzkd = zzawu().zzl(str2, obj);
            if (zzkd != 0) {
                zzawu();
                zza = zzclq.zza(str2, 24, true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i = String.valueOf(obj).length();
                }
                this.zziwf.zzawu().zza(zzkd, "_ev", zza, i);
                return;
            }
            Object zzm = zzawu().zzm(str2, obj);
            if (zzm != null) {
                zza(str, str2, currentTimeMillis, zzm);
            }
        } else {
            zza(str, str2, currentTimeMillis, null);
        }
    }

    final String zzbd(long j) {
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            zzawx().zzg(new zzcjz(this, atomicReference));
            try {
                atomicReference.wait(j);
            } catch (InterruptedException e) {
                zzawy().zzazf().log("Interrupted waiting for app instance id");
                return null;
            }
        }
        return (String) atomicReference.get();
    }

    public final List<zzcln> zzbq(boolean z) {
        zzxf();
        zzawy().zzazi().log("Fetching user attributes (FE)");
        if (zzawx().zzazs()) {
            zzawy().zzazd().log("Cannot get all user properties from analytics worker thread");
            return Collections.emptyList();
        }
        zzawx();
        if (zzcih.zzau()) {
            zzawy().zzazd().log("Cannot get all user properties from main thread");
            return Collections.emptyList();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zziwf.zzawx().zzg(new zzcjx(this, atomicReference, z));
            try {
                atomicReference.wait(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            } catch (InterruptedException e) {
                zzawy().zzazf().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzcln> list = (List) atomicReference.get();
        if (list != null) {
            return list;
        }
        zzawy().zzazf().log("Timed out waiting for get user properties");
        return Collections.emptyList();
    }

    public final void zzc(String str, String str2, Bundle bundle) {
        boolean z = this.zzjgy == null || zzclq.zzki(str2);
        zza(str, str2, bundle, true, z, false, null);
    }

    final void zzjp(String str) {
        this.zzjhb.set(str);
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
