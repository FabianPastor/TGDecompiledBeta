package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public class zzae extends zzaa {
    private final zza avs;
    private zzm avt;
    private Boolean avu;
    private final zzf avv;
    private final zzah avw;
    private final List<Runnable> avx = new ArrayList();
    private final zzf avy;

    protected class zza implements ServiceConnection, zzb, zzc {
        private volatile boolean avE;
        private volatile zzp avF;
        final /* synthetic */ zzae avz;

        protected zza(zzae com_google_android_gms_measurement_internal_zzae) {
            this.avz = com_google_android_gms_measurement_internal_zzae;
        }

        @MainThread
        public void onConnected(@Nullable Bundle bundle) {
            zzaa.zzhs("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    final zzm com_google_android_gms_measurement_internal_zzm = (zzm) this.avF.zzavg();
                    this.avF = null;
                    this.avz.zzbwa().zzm(new Runnable(this) {
                        final /* synthetic */ zza avH;

                        public void run() {
                            synchronized (this.avH) {
                                this.avH.avE = false;
                                if (!this.avH.avz.isConnected()) {
                                    this.avH.avz.zzbwb().zzbxd().log("Connected to remote service");
                                    this.avH.avz.zza(com_google_android_gms_measurement_internal_zzm);
                                }
                            }
                        }
                    });
                } catch (DeadObjectException e) {
                    this.avF = null;
                    this.avE = false;
                } catch (IllegalStateException e2) {
                    this.avF = null;
                    this.avE = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzaa.zzhs("MeasurementServiceConnection.onConnectionFailed");
            zzq zzbxr = this.avz.aqw.zzbxr();
            if (zzbxr != null) {
                zzbxr.zzbxa().zzj("Service connection failed", connectionResult);
            }
            synchronized (this) {
                this.avE = false;
                this.avF = null;
            }
        }

        @MainThread
        public void onConnectionSuspended(int i) {
            zzaa.zzhs("MeasurementServiceConnection.onConnectionSuspended");
            this.avz.zzbwb().zzbxd().log("Service connection suspended");
            this.avz.zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zza avH;

                {
                    this.avH = r1;
                }

                public void run() {
                    zzae com_google_android_gms_measurement_internal_zzae = this.avH.avz;
                    Context context = this.avH.avz.getContext();
                    this.avH.avz.zzbwd().zzayi();
                    com_google_android_gms_measurement_internal_zzae.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                }
            });
        }

        @MainThread
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzaa.zzhs("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (iBinder == null) {
                    this.avE = false;
                    this.avz.zzbwb().zzbwy().log("Service connected with null binder");
                    return;
                }
                zzm com_google_android_gms_measurement_internal_zzm = null;
                try {
                    String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_measurement_internal_zzm = com.google.android.gms.measurement.internal.zzm.zza.zzjp(iBinder);
                        this.avz.zzbwb().zzbxe().log("Bound to IMeasurementService interface");
                    } else {
                        this.avz.zzbwb().zzbwy().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.avz.zzbwb().zzbwy().log("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_measurement_internal_zzm == null) {
                    this.avE = false;
                    try {
                        com.google.android.gms.common.stats.zza.zzaxr().zza(this.avz.getContext(), this.avz.avs);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.avz.zzbwa().zzm(new Runnable(this) {
                        final /* synthetic */ zza avH;

                        public void run() {
                            synchronized (this.avH) {
                                this.avH.avE = false;
                                if (!this.avH.avz.isConnected()) {
                                    this.avH.avz.zzbwb().zzbxe().log("Connected to service");
                                    this.avH.avz.zza(com_google_android_gms_measurement_internal_zzm);
                                }
                            }
                        }
                    });
                }
            }
        }

        @MainThread
        public void onServiceDisconnected(final ComponentName componentName) {
            zzaa.zzhs("MeasurementServiceConnection.onServiceDisconnected");
            this.avz.zzbwb().zzbxd().log("Service disconnected");
            this.avz.zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zza avH;

                public void run() {
                    this.avH.avz.onServiceDisconnected(componentName);
                }
            });
        }

        @WorkerThread
        public void zzac(Intent intent) {
            this.avz.zzzx();
            Context context = this.avz.getContext();
            com.google.android.gms.common.stats.zza zzaxr = com.google.android.gms.common.stats.zza.zzaxr();
            synchronized (this) {
                if (this.avE) {
                    this.avz.zzbwb().zzbxe().log("Connection attempt already in progress");
                    return;
                }
                this.avE = true;
                zzaxr.zza(context, intent, this.avz.avs, (int) TsExtractor.TS_STREAM_TYPE_AC3);
            }
        }

        @WorkerThread
        public void zzbza() {
            this.avz.zzzx();
            Context context = this.avz.getContext();
            synchronized (this) {
                if (this.avE) {
                    this.avz.zzbwb().zzbxe().log("Connection attempt already in progress");
                } else if (this.avF != null) {
                    this.avz.zzbwb().zzbxe().log("Already awaiting connection attempt");
                } else {
                    this.avF = new zzp(context, Looper.getMainLooper(), this, this);
                    this.avz.zzbwb().zzbxe().log("Connecting to remote service");
                    this.avE = true;
                    this.avF.zzavd();
                }
            }
        }
    }

    protected zzae(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        this.avw = new zzah(com_google_android_gms_measurement_internal_zzx.zzabz());
        this.avs = new zza(this);
        this.avv = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzae avz;

            public void run() {
                this.avz.zzacx();
            }
        };
        this.avy = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzae avz;

            public void run() {
                this.avz.zzbwb().zzbxa().log("Tasks have been queued for a long time");
            }
        };
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName componentName) {
        zzzx();
        if (this.avt != null) {
            this.avt = null;
            zzbwb().zzbxe().zzj("Disconnected from device MeasurementService", componentName);
            zzbyy();
        }
    }

    @WorkerThread
    private void zza(zzm com_google_android_gms_measurement_internal_zzm) {
        zzzx();
        zzaa.zzy(com_google_android_gms_measurement_internal_zzm);
        this.avt = com_google_android_gms_measurement_internal_zzm;
        zzacw();
        zzbyz();
    }

    @WorkerThread
    private void zzacw() {
        zzzx();
        this.avw.start();
        zzx com_google_android_gms_measurement_internal_zzx = this.aqw;
        this.avv.zzx(zzbwd().zzaez());
    }

    @WorkerThread
    private void zzacx() {
        zzzx();
        if (isConnected()) {
            zzbwb().zzbxe().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    private boolean zzbyw() {
        zzbwd().zzayi();
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private void zzbyy() {
        zzzx();
        zzadl();
    }

    @WorkerThread
    private void zzbyz() {
        zzzx();
        zzbwb().zzbxe().zzj("Processing queued up service tasks", Integer.valueOf(this.avx.size()));
        for (Runnable zzm : this.avx) {
            zzbwa().zzm(zzm);
        }
        this.avx.clear();
        this.avy.cancel();
    }

    @WorkerThread
    private void zzo(Runnable runnable) throws IllegalStateException {
        zzzx();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.avx.size()) >= zzbwd().zzbuy()) {
            zzbwb().zzbwy().log("Discarding data. Max runnable queue size reached");
        } else {
            this.avx.add(runnable);
            zzx com_google_android_gms_measurement_internal_zzx = this.aqw;
            this.avy.zzx(60000);
            zzadl();
        }
    }

    @WorkerThread
    public void disconnect() {
        zzzx();
        zzacj();
        try {
            com.google.android.gms.common.stats.zza.zzaxr().zza(getContext(), this.avs);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.avt = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzzx();
        zzacj();
        return this.avt != null;
    }

    @WorkerThread
    protected void zza(final zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzzx();
        zzacj();
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            public void run() {
                zzm zzc = this.avz.avt;
                if (zzc == null) {
                    this.avz.zzbwb().zzbwy().log("Failed to send current screen to service");
                    return;
                }
                try {
                    if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
                        zzc.zza(0, null, null, this.avz.getContext().getPackageName());
                    } else {
                        zzc.zza(com_google_android_gms_measurement_AppMeasurement_zzf.aqB, com_google_android_gms_measurement_AppMeasurement_zzf.aqz, com_google_android_gms_measurement_AppMeasurement_zzf.aqA, this.avz.getContext().getPackageName());
                    }
                    this.avz.zzacw();
                } catch (RemoteException e) {
                    this.avz.zzbwb().zzbwy().zzj("Failed to send current screen to the service", e);
                }
            }
        });
    }

    @WorkerThread
    void zza(zzm com_google_android_gms_measurement_internal_zzm, AbstractSafeParcelable abstractSafeParcelable) {
        zzzx();
        zzaby();
        zzacj();
        Object obj = (VERSION.SDK_INT < 11 || zzbwd().zzayi()) ? null : 1;
        List<AbstractSafeParcelable> arrayList = new ArrayList();
        zzbwd().zzbvb();
        int i = 100;
        for (int i2 = 0; i2 < PointerIconCompat.TYPE_CONTEXT_MENU && r3 == 100; i2++) {
            if (obj != null) {
                Object zzxe = zzbvv().zzxe(100);
                if (zzxe != null) {
                    arrayList.addAll(zzxe);
                    i = zzxe.size();
                    if (abstractSafeParcelable != null && r3 < 100) {
                        arrayList.add(abstractSafeParcelable);
                    }
                    for (AbstractSafeParcelable abstractSafeParcelable2 : arrayList) {
                        if (abstractSafeParcelable2 instanceof EventParcel) {
                            try {
                                com_google_android_gms_measurement_internal_zzm.zza((EventParcel) abstractSafeParcelable2, zzbvr().zzmi(zzbwb().zzbxf()));
                            } catch (RemoteException e) {
                                zzbwb().zzbwy().zzj("Failed to send event to the service", e);
                            }
                        } else if (abstractSafeParcelable2 instanceof UserAttributeParcel) {
                            zzbwb().zzbwy().log("Discarding data. Unrecognized parcel type.");
                        } else {
                            try {
                                com_google_android_gms_measurement_internal_zzm.zza((UserAttributeParcel) abstractSafeParcelable2, zzbvr().zzmi(zzbwb().zzbxf()));
                            } catch (RemoteException e2) {
                                zzbwb().zzbwy().zzj("Failed to send attribute to the service", e2);
                            }
                        }
                    }
                }
            }
            i = 0;
            arrayList.add(abstractSafeParcelable);
            for (AbstractSafeParcelable abstractSafeParcelable22 : arrayList) {
                if (abstractSafeParcelable22 instanceof EventParcel) {
                    com_google_android_gms_measurement_internal_zzm.zza((EventParcel) abstractSafeParcelable22, zzbvr().zzmi(zzbwb().zzbxf()));
                } else if (abstractSafeParcelable22 instanceof UserAttributeParcel) {
                    zzbwb().zzbwy().log("Discarding data. Unrecognized parcel type.");
                } else {
                    com_google_android_gms_measurement_internal_zzm.zza((UserAttributeParcel) abstractSafeParcelable22, zzbvr().zzmi(zzbwb().zzbxf()));
                }
            }
        }
    }

    @WorkerThread
    protected void zza(final AtomicReference<List<UserAttributeParcel>> atomicReference, final boolean z) {
        zzzx();
        zzacj();
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference) {
                    try {
                        zzm zzc = this.avz.avt;
                        if (zzc == null) {
                            this.avz.zzbwb().zzbwy().log("Failed to get user properties");
                        } else {
                            atomicReference.set(zzc.zza(this.avz.zzbvr().zzmi(null), z));
                            this.avz.zzacw();
                            atomicReference.notify();
                        }
                    } catch (RemoteException e) {
                        this.avz.zzbwb().zzbwy().zzj("Failed to get user properties", e);
                    } finally {
                        atomicReference.notify();
                    }
                }
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    @WorkerThread
    void zzadl() {
        zzzx();
        zzacj();
        if (!isConnected()) {
            if (this.avu == null) {
                this.avu = zzbwc().zzbxl();
                if (this.avu == null) {
                    zzbwb().zzbxe().log("State of service unknown");
                    this.avu = Boolean.valueOf(zzbyx());
                    zzbwc().zzcg(this.avu.booleanValue());
                }
            }
            if (this.avu.booleanValue()) {
                zzbwb().zzbxe().log("Using measurement service");
                this.avs.zzbza();
                return;
            }
            zzx com_google_android_gms_measurement_internal_zzx = this.aqw;
            if (zzbyw()) {
                zzbwb().zzbxe().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = getContext();
                zzbwd().zzayi();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.avs.zzac(intent);
                return;
            }
            zzbwb().zzbwy().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
        }
    }

    @WorkerThread
    protected void zzb(final UserAttributeParcel userAttributeParcel) {
        boolean z = true;
        zzzx();
        zzacj();
        boolean z2 = VERSION.SDK_INT >= 11 && !zzbwd().zzayi();
        if (!(z2 && zzbvv().zza(userAttributeParcel))) {
            z = false;
        }
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            public void run() {
                zzm zzc = this.avz.avt;
                if (zzc == null) {
                    this.avz.zzbwb().zzbwy().log("Discarding data. Failed to set user attribute");
                    return;
                }
                this.avz.zza(zzc, z ? null : userAttributeParcel);
                this.avz.zzacw();
            }
        });
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

    @WorkerThread
    protected void zzbyq() {
        zzzx();
        zzacj();
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            {
                this.avz = r1;
            }

            public void run() {
                zzm zzc = this.avz.avt;
                if (zzc == null) {
                    this.avz.zzbwb().zzbwy().log("Discarding data. Failed to send app launch");
                    return;
                }
                try {
                    this.avz.zza(zzc, null);
                    zzc.zza(this.avz.zzbvr().zzmi(this.avz.zzbwb().zzbxf()));
                    this.avz.zzacw();
                } catch (RemoteException e) {
                    this.avz.zzbwb().zzbwy().zzj("Failed to send app launch to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected void zzbyv() {
        zzzx();
        zzacj();
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            {
                this.avz = r1;
            }

            public void run() {
                zzm zzc = this.avz.avt;
                if (zzc == null) {
                    this.avz.zzbwb().zzbwy().log("Failed to send measurementEnabled to service");
                    return;
                }
                try {
                    zzc.zzb(this.avz.zzbvr().zzmi(this.avz.zzbwb().zzbxf()));
                    this.avz.zzacw();
                } catch (RemoteException e) {
                    this.avz.zzbwb().zzbwy().zzj("Failed to send measurementEnabled to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected boolean zzbyx() {
        zzzx();
        zzacj();
        zzbwd().zzayi();
        zzbwb().zzbxe().log("Checking service availability");
        switch (com.google.android.gms.common.zzc.zzaql().isGooglePlayServicesAvailable(getContext())) {
            case 0:
                zzbwb().zzbxe().log("Service available");
                return true;
            case 1:
                zzbwb().zzbxe().log("Service missing");
                return false;
            case 2:
                zzbwb().zzbxd().log("Service container out of date");
                return true;
            case 3:
                zzbwb().zzbxa().log("Service disabled");
                return false;
            case 9:
                zzbwb().zzbxa().log("Service invalid");
                return false;
            case 18:
                zzbwb().zzbxa().log("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    protected void zzc(EventParcel eventParcel, String str) {
        boolean z = true;
        zzaa.zzy(eventParcel);
        zzzx();
        zzacj();
        final boolean z2 = VERSION.SDK_INT >= 11 && !zzbwd().zzayi();
        if (!(z2 && zzbvv().zza(eventParcel))) {
            z = false;
        }
        final EventParcel eventParcel2 = eventParcel;
        final String str2 = str;
        zzo(new Runnable(this) {
            final /* synthetic */ zzae avz;

            public void run() {
                zzm zzc = this.avz.avt;
                if (zzc == null) {
                    this.avz.zzbwb().zzbwy().log("Discarding data. Failed to send event to service");
                    return;
                }
                if (z2) {
                    this.avz.zza(zzc, z ? null : eventParcel2);
                } else {
                    try {
                        if (TextUtils.isEmpty(str2)) {
                            zzc.zza(eventParcel2, this.avz.zzbvr().zzmi(this.avz.zzbwb().zzbxf()));
                        } else {
                            zzc.zza(eventParcel2, str2, this.avz.zzbwb().zzbxf());
                        }
                    } catch (RemoteException e) {
                        this.avz.zzbwb().zzbwy().zzj("Failed to send event to the service", e);
                    }
                }
                this.avz.zzacw();
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
