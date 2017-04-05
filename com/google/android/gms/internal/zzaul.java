package com.google.android.gms.internal;

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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.common.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public class zzaul extends zzauh {
    private final zza zzbvA;
    private zzatt zzbvB;
    private Boolean zzbvC;
    private final zzatk zzbvD;
    private final zzauo zzbvE;
    private final List<Runnable> zzbvF = new ArrayList();
    private final zzatk zzbvG;

    protected class zza implements ServiceConnection, zzb, zzc {
        final /* synthetic */ zzaul zzbvH;
        private volatile boolean zzbvO;
        private volatile zzatw zzbvP;

        protected zza(zzaul com_google_android_gms_internal_zzaul) {
            this.zzbvH = com_google_android_gms_internal_zzaul;
        }

        @MainThread
        public void onConnected(@Nullable Bundle bundle) {
            zzac.zzdj("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    final zzatt com_google_android_gms_internal_zzatt = (zzatt) this.zzbvP.zzxD();
                    this.zzbvP = null;
                    this.zzbvH.zzKk().zzm(new Runnable(this) {
                        final /* synthetic */ zza zzbvR;

                        public void run() {
                            synchronized (this.zzbvR) {
                                this.zzbvR.zzbvO = false;
                                if (!this.zzbvR.zzbvH.isConnected()) {
                                    this.zzbvR.zzbvH.zzKl().zzMd().log("Connected to remote service");
                                    this.zzbvR.zzbvH.zza(com_google_android_gms_internal_zzatt);
                                }
                            }
                        }
                    });
                } catch (DeadObjectException e) {
                    this.zzbvP = null;
                    this.zzbvO = false;
                } catch (IllegalStateException e2) {
                    this.zzbvP = null;
                    this.zzbvO = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zzdj("MeasurementServiceConnection.onConnectionFailed");
            zzatx zzMu = this.zzbvH.zzbqc.zzMu();
            if (zzMu != null) {
                zzMu.zzMa().zzj("Service connection failed", connectionResult);
            }
            synchronized (this) {
                this.zzbvO = false;
                this.zzbvP = null;
            }
        }

        @MainThread
        public void onConnectionSuspended(int i) {
            zzac.zzdj("MeasurementServiceConnection.onConnectionSuspended");
            this.zzbvH.zzKl().zzMd().log("Service connection suspended");
            this.zzbvH.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zza zzbvR;

                {
                    this.zzbvR = r1;
                }

                public void run() {
                    zzaul com_google_android_gms_internal_zzaul = this.zzbvR.zzbvH;
                    Context context = this.zzbvR.zzbvH.getContext();
                    this.zzbvR.zzbvH.zzKn().zzLg();
                    com_google_android_gms_internal_zzaul.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                }
            });
        }

        @MainThread
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzac.zzdj("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (iBinder == null) {
                    this.zzbvO = false;
                    this.zzbvH.zzKl().zzLY().log("Service connected with null binder");
                    return;
                }
                zzatt com_google_android_gms_internal_zzatt = null;
                try {
                    String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_internal_zzatt = com.google.android.gms.internal.zzatt.zza.zzes(iBinder);
                        this.zzbvH.zzKl().zzMe().log("Bound to IMeasurementService interface");
                    } else {
                        this.zzbvH.zzKl().zzLY().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.zzbvH.zzKl().zzLY().log("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_internal_zzatt == null) {
                    this.zzbvO = false;
                    try {
                        com.google.android.gms.common.stats.zza.zzyJ().zza(this.zzbvH.getContext(), this.zzbvH.zzbvA);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.zzbvH.zzKk().zzm(new Runnable(this) {
                        final /* synthetic */ zza zzbvR;

                        public void run() {
                            synchronized (this.zzbvR) {
                                this.zzbvR.zzbvO = false;
                                if (!this.zzbvR.zzbvH.isConnected()) {
                                    this.zzbvR.zzbvH.zzKl().zzMe().log("Connected to service");
                                    this.zzbvR.zzbvH.zza(com_google_android_gms_internal_zzatt);
                                }
                            }
                        }
                    });
                }
            }
        }

        @MainThread
        public void onServiceDisconnected(final ComponentName componentName) {
            zzac.zzdj("MeasurementServiceConnection.onServiceDisconnected");
            this.zzbvH.zzKl().zzMd().log("Service disconnected");
            this.zzbvH.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zza zzbvR;

                public void run() {
                    this.zzbvR.zzbvH.onServiceDisconnected(componentName);
                }
            });
        }

        @WorkerThread
        public void zzNb() {
            this.zzbvH.zzmR();
            Context context = this.zzbvH.getContext();
            synchronized (this) {
                if (this.zzbvO) {
                    this.zzbvH.zzKl().zzMe().log("Connection attempt already in progress");
                } else if (this.zzbvP != null) {
                    this.zzbvH.zzKl().zzMe().log("Already awaiting connection attempt");
                } else {
                    this.zzbvP = new zzatw(context, Looper.getMainLooper(), this, this);
                    this.zzbvH.zzKl().zzMe().log("Connecting to remote service");
                    this.zzbvO = true;
                    this.zzbvP.zzxz();
                }
            }
        }

        @WorkerThread
        public void zzz(Intent intent) {
            this.zzbvH.zzmR();
            Context context = this.zzbvH.getContext();
            com.google.android.gms.common.stats.zza zzyJ = com.google.android.gms.common.stats.zza.zzyJ();
            synchronized (this) {
                if (this.zzbvO) {
                    this.zzbvH.zzKl().zzMe().log("Connection attempt already in progress");
                    return;
                }
                this.zzbvO = true;
                zzyJ.zza(context, intent, this.zzbvH.zzbvA, (int) TsExtractor.TS_STREAM_TYPE_AC3);
            }
        }
    }

    protected zzaul(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        this.zzbvE = new zzauo(com_google_android_gms_internal_zzaue.zznR());
        this.zzbvA = new zza(this);
        this.zzbvD = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                this.zzbvH.zzop();
            }
        };
        this.zzbvG = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                this.zzbvH.zzKl().zzMa().log("Tasks have been queued for a long time");
            }
        };
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName componentName) {
        zzmR();
        if (this.zzbvB != null) {
            this.zzbvB = null;
            zzKl().zzMe().zzj("Disconnected from device MeasurementService", componentName);
            zzMZ();
        }
    }

    private boolean zzMX() {
        zzKn().zzLg();
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private void zzMZ() {
        zzmR();
        zzoD();
    }

    @WorkerThread
    private void zzNa() {
        zzmR();
        zzKl().zzMe().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbvF.size()));
        for (Runnable zzm : this.zzbvF) {
            zzKk().zzm(zzm);
        }
        this.zzbvF.clear();
        this.zzbvG.cancel();
    }

    @WorkerThread
    private void zzo(Runnable runnable) throws IllegalStateException {
        zzmR();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzbvF.size()) >= zzKn().zzLm()) {
            zzKl().zzLY().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzbvF.add(runnable);
            this.zzbvG.zzy(ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            zzoD();
        }
    }

    @WorkerThread
    private void zzoo() {
        zzmR();
        this.zzbvE.start();
        this.zzbvD.zzy(zzKn().zzpq());
    }

    @WorkerThread
    private void zzop() {
        zzmR();
        if (isConnected()) {
            zzKl().zzMe().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    @WorkerThread
    public void disconnect() {
        zzmR();
        zzob();
        try {
            com.google.android.gms.common.stats.zza.zzyJ().zza(getContext(), this.zzbvA);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzbvB = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzmR();
        zzob();
        return this.zzbvB != null;
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

    @WorkerThread
    protected void zzMR() {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            {
                this.zzbvH = r1;
            }

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Discarding data. Failed to send app launch");
                    return;
                }
                try {
                    zzc.zza(this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf()));
                    this.zzbvH.zza(zzc, null);
                    this.zzbvH.zzoo();
                } catch (RemoteException e) {
                    this.zzbvH.zzKl().zzLY().zzj("Failed to send app launch to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected void zzMW() {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            {
                this.zzbvH = r1;
            }

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Failed to send measurementEnabled to service");
                    return;
                }
                try {
                    zzc.zzb(this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf()));
                    this.zzbvH.zzoo();
                } catch (RemoteException e) {
                    this.zzbvH.zzKl().zzLY().zzj("Failed to send measurementEnabled to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected boolean zzMY() {
        zzmR();
        zzob();
        zzKn().zzLg();
        zzKl().zzMe().log("Checking service availability");
        switch (zze.zzuY().isGooglePlayServicesAvailable(getContext())) {
            case 0:
                zzKl().zzMe().log("Service available");
                return true;
            case 1:
                zzKl().zzMe().log("Service missing");
                return false;
            case 2:
                zzKl().zzMd().log("Service container out of date");
                return true;
            case 3:
                zzKl().zzMa().log("Service disabled");
                return false;
            case 9:
                zzKl().zzMa().log("Service invalid");
                return false;
            case 18:
                zzKl().zzMa().log("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    protected void zza(zzatt com_google_android_gms_internal_zzatt) {
        zzmR();
        zzac.zzw(com_google_android_gms_internal_zzatt);
        this.zzbvB = com_google_android_gms_internal_zzatt;
        zzoo();
        zzNa();
    }

    @WorkerThread
    void zza(zzatt com_google_android_gms_internal_zzatt, com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza) {
        zzmR();
        zzJW();
        zzob();
        int i = VERSION.SDK_INT;
        zzKn().zzLg();
        List<com.google.android.gms.common.internal.safeparcel.zza> arrayList = new ArrayList();
        zzKn().zzLp();
        int i2 = 100;
        for (int i3 = 0; i3 < PointerIconCompat.TYPE_CONTEXT_MENU && r1 == 100; i3++) {
            Object zzlD = zzKf().zzlD(100);
            if (zzlD != null) {
                arrayList.addAll(zzlD);
                i2 = zzlD.size();
            } else {
                i2 = 0;
            }
            if (com_google_android_gms_common_internal_safeparcel_zza != null && r1 < 100) {
                arrayList.add(com_google_android_gms_common_internal_safeparcel_zza);
            }
            for (com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza2 : arrayList) {
                if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzatq) {
                    try {
                        com_google_android_gms_internal_zzatt.zza((zzatq) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMf()));
                    } catch (RemoteException e) {
                        zzKl().zzLY().zzj("Failed to send event to the service", e);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzauq) {
                    try {
                        com_google_android_gms_internal_zzatt.zza((zzauq) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMf()));
                    } catch (RemoteException e2) {
                        zzKl().zzLY().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzatg) {
                    try {
                        com_google_android_gms_internal_zzatt.zza((zzatg) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMf()));
                    } catch (RemoteException e22) {
                        zzKl().zzLY().zzj("Failed to send conditional property to the service", e22);
                    }
                } else {
                    zzKl().zzLY().log("Discarding data. Unrecognized parcel type.");
                }
            }
        }
    }

    @WorkerThread
    protected void zza(final zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Failed to send current screen to service");
                    return;
                }
                try {
                    if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
                        zzc.zza(0, null, null, this.zzbvH.getContext().getPackageName());
                    } else {
                        zzc.zza(com_google_android_gms_measurement_AppMeasurement_zzf.zzbqh, com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf, com_google_android_gms_measurement_AppMeasurement_zzf.zzbqg, this.zzbvH.getContext().getPackageName());
                    }
                    this.zzbvH.zzoo();
                } catch (RemoteException e) {
                    this.zzbvH.zzKl().zzLY().zzj("Failed to send current screen to the service", e);
                }
            }
        });
    }

    @WorkerThread
    public void zza(final AtomicReference<String> atomicReference) {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference) {
                    try {
                        zzatt zzc = this.zzbvH.zzbvB;
                        if (zzc == null) {
                            this.zzbvH.zzKl().zzLY().log("Failed to get app instance id");
                        } else {
                            atomicReference.set(zzc.zzc(this.zzbvH.zzKb().zzfD(null)));
                            this.zzbvH.zzoo();
                            atomicReference.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzj("Failed to get app instance id", e);
                    } finally {
                        atomicReference.notify();
                    }
                }
            }
        });
    }

    @WorkerThread
    protected void zza(AtomicReference<List<zzatg>> atomicReference, String str, String str2, String str3) {
        zzmR();
        zzob();
        final AtomicReference<List<zzatg>> atomicReference2 = atomicReference;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference2) {
                    try {
                        zzatt zzc = this.zzbvH.zzbvB;
                        if (zzc == null) {
                            this.zzbvH.zzKl().zzLY().zzd("Failed to get conditional properties", zzatx.zzfE(str4), str5, str6);
                            atomicReference2.set(Collections.emptyList());
                        } else {
                            if (TextUtils.isEmpty(str4)) {
                                atomicReference2.set(zzc.zza(str5, str6, this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf())));
                            } else {
                                atomicReference2.set(zzc.zzn(str4, str5, str6));
                            }
                            this.zzbvH.zzoo();
                            atomicReference2.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzd("Failed to get conditional properties", zzatx.zzfE(str4), str5, e);
                        atomicReference2.set(Collections.emptyList());
                    } finally {
                        atomicReference2.notify();
                    }
                }
            }
        });
    }

    @WorkerThread
    protected void zza(AtomicReference<List<zzauq>> atomicReference, String str, String str2, String str3, boolean z) {
        zzmR();
        zzob();
        final AtomicReference<List<zzauq>> atomicReference2 = atomicReference;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final boolean z2 = z;
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference2) {
                    try {
                        zzatt zzc = this.zzbvH.zzbvB;
                        if (zzc == null) {
                            this.zzbvH.zzKl().zzLY().zzd("Failed to get user properties", zzatx.zzfE(str4), str5, str6);
                            atomicReference2.set(Collections.emptyList());
                        } else {
                            if (TextUtils.isEmpty(str4)) {
                                atomicReference2.set(zzc.zza(str5, str6, z2, this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf())));
                            } else {
                                atomicReference2.set(zzc.zza(str4, str5, str6, z2));
                            }
                            this.zzbvH.zzoo();
                            atomicReference2.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzd("Failed to get user properties", zzatx.zzfE(str4), str5, e);
                        atomicReference2.set(Collections.emptyList());
                    } finally {
                        atomicReference2.notify();
                    }
                }
            }
        });
    }

    @WorkerThread
    protected void zza(final AtomicReference<List<zzauq>> atomicReference, final boolean z) {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference) {
                    try {
                        zzatt zzc = this.zzbvH.zzbvB;
                        if (zzc == null) {
                            this.zzbvH.zzKl().zzLY().log("Failed to get user properties");
                        } else {
                            atomicReference.set(zzc.zza(this.zzbvH.zzKb().zzfD(null), z));
                            this.zzbvH.zzoo();
                            atomicReference.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzj("Failed to get user properties", e);
                    } finally {
                        atomicReference.notify();
                    }
                }
            }
        });
    }

    @WorkerThread
    protected void zzb(final zzauq com_google_android_gms_internal_zzauq) {
        zzmR();
        zzob();
        int i = VERSION.SDK_INT;
        zzKn().zzLg();
        final boolean z = zzKf().zza(com_google_android_gms_internal_zzauq);
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Discarding data. Failed to set user attribute");
                    return;
                }
                this.zzbvH.zza(zzc, z ? null : com_google_android_gms_internal_zzauq);
                this.zzbvH.zzoo();
            }
        });
    }

    @WorkerThread
    protected void zzc(zzatq com_google_android_gms_internal_zzatq, String str) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzmR();
        zzob();
        int i = VERSION.SDK_INT;
        zzKn().zzLg();
        final boolean z = zzKf().zza(com_google_android_gms_internal_zzatq);
        final zzatq com_google_android_gms_internal_zzatq2 = com_google_android_gms_internal_zzatq;
        final String str2 = str;
        zzo(new Runnable(this, true) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Discarding data. Failed to send event to service");
                    return;
                }
                if (true) {
                    this.zzbvH.zza(zzc, z ? null : com_google_android_gms_internal_zzatq2);
                } else {
                    try {
                        if (TextUtils.isEmpty(str2)) {
                            zzc.zza(com_google_android_gms_internal_zzatq2, this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf()));
                        } else {
                            zzc.zza(com_google_android_gms_internal_zzatq2, str2, this.zzbvH.zzKl().zzMf());
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzj("Failed to send event to the service", e);
                    }
                }
                this.zzbvH.zzoo();
            }
        });
    }

    @WorkerThread
    protected void zzf(zzatg com_google_android_gms_internal_zzatg) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzmR();
        zzob();
        zzKn().zzLg();
        final boolean z = zzKf().zzc(com_google_android_gms_internal_zzatg);
        final zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
        final zzatg com_google_android_gms_internal_zzatg3 = com_google_android_gms_internal_zzatg;
        zzo(new Runnable(this, true) {
            final /* synthetic */ zzaul zzbvH;

            public void run() {
                zzatt zzc = this.zzbvH.zzbvB;
                if (zzc == null) {
                    this.zzbvH.zzKl().zzLY().log("Discarding data. Failed to send conditional user property to service");
                    return;
                }
                if (true) {
                    this.zzbvH.zza(zzc, z ? null : com_google_android_gms_internal_zzatg2);
                } else {
                    try {
                        if (TextUtils.isEmpty(com_google_android_gms_internal_zzatg3.packageName)) {
                            zzc.zza(com_google_android_gms_internal_zzatg2, this.zzbvH.zzKb().zzfD(this.zzbvH.zzKl().zzMf()));
                        } else {
                            zzc.zzb(com_google_android_gms_internal_zzatg2);
                        }
                    } catch (RemoteException e) {
                        this.zzbvH.zzKl().zzLY().zzj("Failed to send conditional user property to the service", e);
                    }
                }
                this.zzbvH.zzoo();
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zznR() {
        return super.zznR();
    }

    @WorkerThread
    void zzoD() {
        zzmR();
        zzob();
        if (!isConnected()) {
            if (this.zzbvC == null) {
                this.zzbvC = zzKm().zzMm();
                if (this.zzbvC == null) {
                    zzKl().zzMe().log("State of service unknown");
                    this.zzbvC = Boolean.valueOf(zzMY());
                    zzKm().zzaK(this.zzbvC.booleanValue());
                }
            }
            if (this.zzbvC.booleanValue()) {
                zzKl().zzMe().log("Using measurement service");
                this.zzbvA.zzNb();
            } else if (zzMX()) {
                zzKl().zzMe().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = getContext();
                zzKn().zzLg();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzbvA.zzz(intent);
            } else {
                zzKl().zzLY().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
            }
        }
    }
}
