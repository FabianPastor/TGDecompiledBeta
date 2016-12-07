package com.google.android.gms.measurement.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.measurement.AppMeasurement.zzc;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public class zzac extends zzaa {
    protected zza auO;
    private zzb auP;
    private final Set<zzc> auQ = new CopyOnWriteArraySet();
    private boolean auR;

    @MainThread
    @TargetApi(14)
    private class zza implements ActivityLifecycleCallbacks {
        final /* synthetic */ zzac auS;

        private zza(zzac com_google_android_gms_measurement_internal_zzac) {
            this.auS = com_google_android_gms_measurement_internal_zzac;
        }

        private boolean zzms(String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            this.auS.zzd("auto", "_ldl", str);
            return true;
        }

        public void onActivityCreated(Activity activity, Bundle bundle) {
            try {
                this.auS.zzbwb().zzbxe().log("onActivityCreated");
                Intent intent = activity.getIntent();
                if (intent != null) {
                    Uri data = intent.getData();
                    if (data != null && data.isHierarchical()) {
                        if (bundle == null) {
                            Bundle zzu = this.auS.zzbvx().zzu(data);
                            if (zzu != null) {
                                this.auS.zzf("auto", "_cmp", zzu);
                            }
                        }
                        String queryParameter = data.getQueryParameter("referrer");
                        if (!TextUtils.isEmpty(queryParameter)) {
                            Object obj = (queryParameter.contains("gclid") && (queryParameter.contains("utm_campaign") || queryParameter.contains("utm_source") || queryParameter.contains("utm_medium") || queryParameter.contains("utm_term") || queryParameter.contains("utm_content"))) ? 1 : null;
                            if (obj == null) {
                                this.auS.zzbwb().zzbxd().log("Activity created with data 'referrer' param without gclid and at least one utm field");
                                return;
                            } else {
                                this.auS.zzbwb().zzbxd().zzj("Activity created with referrer", queryParameter);
                                zzms(queryParameter);
                            }
                        } else {
                            return;
                        }
                    }
                }
            } catch (Throwable th) {
                this.auS.zzbwb().zzbwy().zzj("Throwable caught in onActivityCreated", th);
            }
            this.auS.zzbvu().onActivityCreated(activity, bundle);
        }

        public void onActivityDestroyed(Activity activity) {
            this.auS.zzbvu().onActivityDestroyed(activity);
        }

        @MainThread
        public void onActivityPaused(Activity activity) {
            this.auS.zzbvu().onActivityPaused(activity);
            this.auS.zzbvz().zzbzd();
        }

        @MainThread
        public void onActivityResumed(Activity activity) {
            this.auS.zzbvu().onActivityResumed(activity);
            this.auS.zzbvz().zzbzb();
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            this.auS.zzbvu().onActivitySaveInstanceState(activity, bundle);
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    protected zzac(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private void zza(String str, String str2, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zza(str, str2, zzabz().currentTimeMillis(), bundle, z, z2, z3, str3);
    }

    @WorkerThread
    private void zza(String str, String str2, Object obj, long j) {
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzzx();
        zzaby();
        zzacj();
        if (!this.aqw.isEnabled()) {
            zzbwb().zzbxd().log("User property not set since app measurement is disabled");
        } else if (this.aqw.zzbxq()) {
            zzbwb().zzbxd().zze("Setting user property (FE)", str2, obj);
            zzbvt().zzb(new UserAttributeParcel(str2, j, obj, str));
        }
    }

    @WorkerThread
    private void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzaa.zzy(bundle);
        zzzx();
        zzacj();
        if (this.aqw.isEnabled()) {
            if (!this.auR) {
                this.auR = true;
                zzbyr();
            }
            boolean zzne = zzal.zzne(str2);
            if (z && this.auP != null && !zzne) {
                zzbwb().zzbxd().zze("Passing event to registered event handler (FE)", str2, bundle);
                this.auP.zzb(str, str2, bundle, j);
                return;
            } else if (this.aqw.zzbxq()) {
                int zzmw = zzbvx().zzmw(str2);
                if (zzmw != 0) {
                    this.aqw.zzbvx().zza(zzmw, "_ev", zzbvx().zza(str2, zzbwd().zzbud(), true), str2 != null ? str2.length() : 0);
                    return;
                }
                bundle.putString("_o", str);
                Bundle zza = zzbvx().zza(str2, bundle, zzf.zzz("_o"), z3);
                if (!bundle.containsKey("_sc")) {
                    zzbwd().zzayi();
                    AppMeasurement.zzf zzbyt = zzbvu().zzbyt();
                    if (zzbyt != null) {
                        zzbyt.avr = true;
                    }
                    zzad.zza(zzbyt, zza);
                }
                Bundle zzam = z2 ? zzam(zza) : zza;
                zzbwb().zzbxd().zze("Logging event (FE)", str2, zzam);
                zzbvt().zzc(new EventParcel(str2, new EventParams(zzam), str, j), str3);
                for (zzc zzc : this.auQ) {
                    zzc.zzc(str, str2, new Bundle(zzam), j);
                }
                return;
            } else {
                return;
            }
        }
        zzbwb().zzbxd().log("Event not sent since app measurement is disabled");
    }

    @WorkerThread
    private void zzbyr() {
        try {
            zzg(Class.forName(zzbys()));
        } catch (ClassNotFoundException e) {
            zzbwb().zzbxc().log("Tag Manager is not found and thus will not be used");
        }
    }

    private String zzbys() {
        return "com.google.android.gms.tagmanager.TagManagerService";
    }

    @WorkerThread
    private void zzci(boolean z) {
        zzzx();
        zzaby();
        zzacj();
        zzbwb().zzbxd().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        zzbwc().setMeasurementEnabled(z);
        zzbvt().zzbyv();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public void setMeasurementEnabled(final boolean z) {
        zzacj();
        zzaby();
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzac auS;

            public void run() {
                this.auS.zzci(z);
            }
        });
    }

    public void setMinimumSessionDuration(final long j) {
        zzaby();
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzac auS;

            public void run() {
                this.auS.zzbwc().ati.set(j);
                this.auS.zzbwb().zzbxd().zzj("Minimum session duration set", Long.valueOf(j));
            }
        });
    }

    public void setSessionTimeoutDuration(final long j) {
        zzaby();
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzac auS;

            public void run() {
                this.auS.zzbwc().atj.set(j);
                this.auS.zzbwb().zzbxd().zzj("Session timeout duration set", Long.valueOf(j));
            }
        });
    }

    @WorkerThread
    public void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        zzzx();
        zzaby();
        zzacj();
        if (!(com_google_android_gms_measurement_AppMeasurement_zzb == null || com_google_android_gms_measurement_AppMeasurement_zzb == this.auP)) {
            zzaa.zza(this.auP == null, (Object) "EventInterceptor already set.");
        }
        this.auP = com_google_android_gms_measurement_AppMeasurement_zzb;
    }

    public void zza(zzc com_google_android_gms_measurement_AppMeasurement_zzc) {
        zzaby();
        zzacj();
        zzaa.zzy(com_google_android_gms_measurement_AppMeasurement_zzc);
        if (!this.auQ.add(com_google_android_gms_measurement_AppMeasurement_zzc)) {
            zzbwb().zzbxa().log("OnEventListener already registered");
        }
    }

    protected void zza(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        final Bundle bundle2 = bundle != null ? new Bundle(bundle) : new Bundle();
        final String str4 = str;
        final String str5 = str2;
        final long j2 = j;
        final boolean z4 = z;
        final boolean z5 = z2;
        final boolean z6 = z3;
        final String str6 = str3;
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzac auS;

            public void run() {
                this.auS.zzb(str4, str5, j2, bundle2, z4, z5, z6, str6);
            }
        });
    }

    void zza(String str, String str2, long j, Object obj) {
        final String str3 = str;
        final String str4 = str2;
        final Object obj2 = obj;
        final long j2 = j;
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzac auS;

            public void run() {
                this.auS.zza(str3, str4, obj2, j2);
            }
        });
    }

    public void zza(String str, String str2, Bundle bundle, boolean z) {
        zzaby();
        boolean z2 = this.auP == null || zzal.zzne(str2);
        zza(str, str2, bundle, true, z2, z, null);
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    Bundle zzam(Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (bundle != null) {
            for (String str : bundle.keySet()) {
                Object zzl = zzbvx().zzl(str, bundle.get(str));
                if (zzl == null) {
                    zzbwb().zzbxa().zzj("Param value can't be null", str);
                } else {
                    zzbvx().zza(bundle2, str, zzl);
                }
            }
        }
        return bundle2;
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    @TargetApi(14)
    public void zzbyp() {
        if (getContext().getApplicationContext() instanceof Application) {
            Application application = (Application) getContext().getApplicationContext();
            if (this.auO == null) {
                this.auO = new zza();
            }
            application.unregisterActivityLifecycleCallbacks(this.auO);
            application.registerActivityLifecycleCallbacks(this.auO);
            zzbwb().zzbxe().log("Registered activity lifecycle callback");
        }
    }

    @WorkerThread
    public void zzbyq() {
        zzzx();
        zzaby();
        zzacj();
        if (this.aqw.zzbxq()) {
            zzbvt().zzbyq();
            String zzbxn = zzbwc().zzbxn();
            if (!TextUtils.isEmpty(zzbxn) && !zzbxn.equals(zzbvs().zzbws())) {
                Bundle bundle = new Bundle();
                bundle.putString("_po", zzbxn);
                zzf("auto", "_ou", bundle);
            }
        }
    }

    public List<UserAttributeParcel> zzcj(final boolean z) {
        zzaby();
        zzacj();
        zzbwb().zzbxd().log("Fetching user attributes (FE)");
        final AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.aqw.zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zzac auS;

                public void run() {
                    this.auS.zzbvt().zza(atomicReference, z);
                }
            });
            try {
                atomicReference.wait(HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
            } catch (InterruptedException e) {
                zzbwb().zzbxa().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<UserAttributeParcel> list = (List) atomicReference.get();
        if (list != null) {
            return list;
        }
        zzbwb().zzbxa().log("Timed out waiting for get user properties");
        return Collections.emptyList();
    }

    public void zzd(String str, String str2, Bundle bundle, long j) {
        zzaby();
        zza(str, str2, j, bundle, false, true, true, null);
    }

    public void zzd(String str, String str2, Object obj) {
        int i = 0;
        zzaa.zzib(str);
        long currentTimeMillis = zzabz().currentTimeMillis();
        int zzmy = zzbvx().zzmy(str2);
        String zza;
        if (zzmy != 0) {
            zza = zzbvx().zza(str2, zzbwd().zzbue(), true);
            if (str2 != null) {
                i = str2.length();
            }
            this.aqw.zzbvx().zza(zzmy, "_ev", zza, i);
        } else if (obj != null) {
            zzmy = zzbvx().zzm(str2, obj);
            if (zzmy != 0) {
                zza = zzbvx().zza(str2, zzbwd().zzbue(), true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i = String.valueOf(obj).length();
                }
                this.aqw.zzbvx().zza(zzmy, "_ev", zza, i);
                return;
            }
            Object zzn = zzbvx().zzn(str2, obj);
            if (zzn != null) {
                zza(str, str2, currentTimeMillis, zzn);
            }
        } else {
            zza(str, str2, currentTimeMillis, null);
        }
    }

    public void zzf(String str, String str2, Bundle bundle) {
        zzaby();
        boolean z = this.auP == null || zzal.zzne(str2);
        zza(str, str2, bundle, true, z, false, null);
    }

    @WorkerThread
    public void zzg(Class<?> cls) {
        try {
            cls.getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{getContext()});
        } catch (Exception e) {
            zzbwb().zzbxa().zzj("Failed to invoke Tag Manager's initialize() method", e);
        }
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
