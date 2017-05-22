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
    private final zza zzbvD;
    private zzatt zzbvE;
    private Boolean zzbvF;
    private final zzatk zzbvG;
    private final zzauo zzbvH;
    private final List<Runnable> zzbvI = new ArrayList();
    private final zzatk zzbvJ;

    protected class zza implements ServiceConnection, zzb, zzc {
        final /* synthetic */ zzaul zzbvK;
        private volatile boolean zzbvR;
        private volatile zzatw zzbvS;

        protected zza(zzaul com_google_android_gms_internal_zzaul) {
            this.zzbvK = com_google_android_gms_internal_zzaul;
        }

        @MainThread
        public void onConnected(@Nullable Bundle bundle) {
            zzac.zzdj("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    final zzatt com_google_android_gms_internal_zzatt = (zzatt) this.zzbvS.zzxD();
                    this.zzbvS = null;
                    this.zzbvK.zzKk().zzm(new Runnable(this) {
                        final /* synthetic */ zza zzbvU;

                        public void run() {
                            synchronized (this.zzbvU) {
                                this.zzbvU.zzbvR = false;
                                if (!this.zzbvU.zzbvK.isConnected()) {
                                    this.zzbvU.zzbvK.zzKl().zzMe().log("Connected to remote service");
                                    this.zzbvU.zzbvK.zza(com_google_android_gms_internal_zzatt);
                                }
                            }
                        }
                    });
                } catch (DeadObjectException e) {
                    this.zzbvS = null;
                    this.zzbvR = false;
                } catch (IllegalStateException e2) {
                    this.zzbvS = null;
                    this.zzbvR = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zzdj("MeasurementServiceConnection.onConnectionFailed");
            zzatx zzMv = this.zzbvK.zzbqb.zzMv();
            if (zzMv != null) {
                zzMv.zzMb().zzj("Service connection failed", connectionResult);
            }
            synchronized (this) {
                this.zzbvR = false;
                this.zzbvS = null;
            }
            this.zzbvK.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zza zzbvU;

                {
                    this.zzbvU = r1;
                }

                public void run() {
                    this.zzbvU.zzbvK.zzbvE = null;
                    this.zzbvU.zzbvK.zzNc();
                }
            });
        }

        @MainThread
        public void onConnectionSuspended(int i) {
            zzac.zzdj("MeasurementServiceConnection.onConnectionSuspended");
            this.zzbvK.zzKl().zzMe().log("Service connection suspended");
            this.zzbvK.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zza zzbvU;

                {
                    this.zzbvU = r1;
                }

                public void run() {
                    zzaul com_google_android_gms_internal_zzaul = this.zzbvU.zzbvK;
                    Context context = this.zzbvU.zzbvK.getContext();
                    this.zzbvU.zzbvK.zzKn().zzLh();
                    com_google_android_gms_internal_zzaul.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                }
            });
        }

        @MainThread
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzac.zzdj("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (iBinder == null) {
                    this.zzbvR = false;
                    this.zzbvK.zzKl().zzLZ().log("Service connected with null binder");
                    return;
                }
                zzatt com_google_android_gms_internal_zzatt = null;
                try {
                    String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_internal_zzatt = com.google.android.gms.internal.zzatt.zza.zzes(iBinder);
                        this.zzbvK.zzKl().zzMf().log("Bound to IMeasurementService interface");
                    } else {
                        this.zzbvK.zzKl().zzLZ().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.zzbvK.zzKl().zzLZ().log("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_internal_zzatt == null) {
                    this.zzbvR = false;
                    try {
                        com.google.android.gms.common.stats.zza.zzyJ().zza(this.zzbvK.getContext(), this.zzbvK.zzbvD);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.zzbvK.zzKk().zzm(new Runnable(this) {
                        final /* synthetic */ zza zzbvU;

                        public void run() {
                            synchronized (this.zzbvU) {
                                this.zzbvU.zzbvR = false;
                                if (!this.zzbvU.zzbvK.isConnected()) {
                                    this.zzbvU.zzbvK.zzKl().zzMf().log("Connected to service");
                                    this.zzbvU.zzbvK.zza(com_google_android_gms_internal_zzatt);
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
            this.zzbvK.zzKl().zzMe().log("Service disconnected");
            this.zzbvK.zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zza zzbvU;

                public void run() {
                    this.zzbvU.zzbvK.onServiceDisconnected(componentName);
                }
            });
        }

        @WorkerThread
        public void zzNd() {
            this.zzbvK.zzmR();
            Context context = this.zzbvK.getContext();
            synchronized (this) {
                if (this.zzbvR) {
                    this.zzbvK.zzKl().zzMf().log("Connection attempt already in progress");
                } else if (this.zzbvS != null) {
                    this.zzbvK.zzKl().zzMf().log("Already awaiting connection attempt");
                } else {
                    this.zzbvS = new zzatw(context, Looper.getMainLooper(), this, this);
                    this.zzbvK.zzKl().zzMf().log("Connecting to remote service");
                    this.zzbvR = true;
                    this.zzbvS.zzxz();
                }
            }
        }

        @WorkerThread
        public void zzz(Intent intent) {
            this.zzbvK.zzmR();
            Context context = this.zzbvK.getContext();
            com.google.android.gms.common.stats.zza zzyJ = com.google.android.gms.common.stats.zza.zzyJ();
            synchronized (this) {
                if (this.zzbvR) {
                    this.zzbvK.zzKl().zzMf().log("Connection attempt already in progress");
                    return;
                }
                this.zzbvR = true;
                zzyJ.zza(context, intent, this.zzbvK.zzbvD, (int) TsExtractor.TS_STREAM_TYPE_AC3);
            }
        }
    }

    protected zzaul(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        this.zzbvH = new zzauo(com_google_android_gms_internal_zzaue.zznR());
        this.zzbvD = new zza(this);
        this.zzbvG = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                this.zzbvK.zzop();
            }
        };
        this.zzbvJ = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                this.zzbvK.zzKl().zzMb().log("Tasks have been queued for a long time");
            }
        };
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName componentName) {
        zzmR();
        if (this.zzbvE != null) {
            this.zzbvE = null;
            zzKl().zzMf().zzj("Disconnected from device MeasurementService", componentName);
            zzNb();
        }
    }

    private boolean zzMZ() {
        zzKn().zzLh();
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private void zzNb() {
        zzmR();
        zzoD();
    }

    @WorkerThread
    private void zzNc() {
        zzmR();
        zzKl().zzMf().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbvI.size()));
        for (Runnable zzm : this.zzbvI) {
            zzKk().zzm(zzm);
        }
        this.zzbvI.clear();
        this.zzbvJ.cancel();
    }

    @WorkerThread
    private void zzo(Runnable runnable) throws IllegalStateException {
        zzmR();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzbvI.size()) >= zzKn().zzLn()) {
            zzKl().zzLZ().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzbvI.add(runnable);
            this.zzbvJ.zzy(ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            zzoD();
        }
    }

    @WorkerThread
    private void zzoo() {
        zzmR();
        this.zzbvH.start();
        this.zzbvG.zzy(zzKn().zzpq());
    }

    @WorkerThread
    private void zzop() {
        zzmR();
        if (isConnected()) {
            zzKl().zzMf().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    @WorkerThread
    public void disconnect() {
        zzmR();
        zzob();
        try {
            com.google.android.gms.common.stats.zza.zzyJ().zza(getContext(), this.zzbvD);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzbvE = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzmR();
        zzob();
        return this.zzbvE != null;
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
    protected void zzMT() {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvK;

            {
                this.zzbvK = r1;
            }

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Discarding data. Failed to send app launch");
                    return;
                }
                try {
                    zzd.zza(this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg()));
                    this.zzbvK.zza(zzd, null);
                    this.zzbvK.zzoo();
                } catch (RemoteException e) {
                    this.zzbvK.zzKl().zzLZ().zzj("Failed to send app launch to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected void zzMY() {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvK;

            {
                this.zzbvK = r1;
            }

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Failed to send measurementEnabled to service");
                    return;
                }
                try {
                    zzd.zzb(this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg()));
                    this.zzbvK.zzoo();
                } catch (RemoteException e) {
                    this.zzbvK.zzKl().zzLZ().zzj("Failed to send measurementEnabled to the service", e);
                }
            }
        });
    }

    @WorkerThread
    protected boolean zzNa() {
        zzmR();
        zzob();
        zzKn().zzLh();
        zzKl().zzMf().log("Checking service availability");
        switch (zze.zzuY().isGooglePlayServicesAvailable(getContext())) {
            case 0:
                zzKl().zzMf().log("Service available");
                return true;
            case 1:
                zzKl().zzMf().log("Service missing");
                return false;
            case 2:
                zzKl().zzMe().log("Service container out of date");
                return true;
            case 3:
                zzKl().zzMb().log("Service disabled");
                return false;
            case 9:
                zzKl().zzMb().log("Service invalid");
                return false;
            case 18:
                zzKl().zzMb().log("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    protected void zza(zzatt com_google_android_gms_internal_zzatt) {
        zzmR();
        zzac.zzw(com_google_android_gms_internal_zzatt);
        this.zzbvE = com_google_android_gms_internal_zzatt;
        zzoo();
        zzNc();
    }

    @WorkerThread
    void zza(zzatt com_google_android_gms_internal_zzatt, com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza) {
        zzmR();
        zzJW();
        zzob();
        int i = VERSION.SDK_INT;
        zzKn().zzLh();
        List<com.google.android.gms.common.internal.safeparcel.zza> arrayList = new ArrayList();
        zzKn().zzLq();
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
                        com_google_android_gms_internal_zzatt.zza((zzatq) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMg()));
                    } catch (RemoteException e) {
                        zzKl().zzLZ().zzj("Failed to send event to the service", e);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzauq) {
                    try {
                        com_google_android_gms_internal_zzatt.zza((zzauq) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMg()));
                    } catch (RemoteException e2) {
                        zzKl().zzLZ().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzatg) {
                    try {
                        com_google_android_gms_internal_zzatt.zza((zzatg) com_google_android_gms_common_internal_safeparcel_zza2, zzKb().zzfD(zzKl().zzMg()));
                    } catch (RemoteException e22) {
                        zzKl().zzLZ().zzj("Failed to send conditional property to the service", e22);
                    }
                } else {
                    zzKl().zzLZ().log("Discarding data. Unrecognized parcel type.");
                }
            }
        }
    }

    @WorkerThread
    protected void zza(final zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Failed to send current screen to service");
                    return;
                }
                try {
                    if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
                        zzd.zza(0, null, null, this.zzbvK.getContext().getPackageName());
                    } else {
                        zzd.zza(com_google_android_gms_measurement_AppMeasurement_zzf.zzbqg, com_google_android_gms_measurement_AppMeasurement_zzf.zzbqe, com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf, this.zzbvK.getContext().getPackageName());
                    }
                    this.zzbvK.zzoo();
                } catch (RemoteException e) {
                    this.zzbvK.zzKl().zzLZ().zzj("Failed to send current screen to the service", e);
                }
            }
        });
    }

    @WorkerThread
    public void zza(final AtomicReference<String> atomicReference) {
        zzmR();
        zzob();
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvK;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference) {
                    try {
                        zzatt zzd = this.zzbvK.zzbvE;
                        if (zzd == null) {
                            this.zzbvK.zzKl().zzLZ().log("Failed to get app instance id");
                        } else {
                            atomicReference.set(zzd.zzc(this.zzbvK.zzKb().zzfD(null)));
                            this.zzbvK.zzoo();
                            atomicReference.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzj("Failed to get app instance id", e);
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
            final /* synthetic */ zzaul zzbvK;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference2) {
                    try {
                        zzatt zzd = this.zzbvK.zzbvE;
                        if (zzd == null) {
                            this.zzbvK.zzKl().zzLZ().zzd("Failed to get conditional properties", zzatx.zzfE(str4), str5, str6);
                            atomicReference2.set(Collections.emptyList());
                        } else {
                            if (TextUtils.isEmpty(str4)) {
                                atomicReference2.set(zzd.zza(str5, str6, this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg())));
                            } else {
                                atomicReference2.set(zzd.zzn(str4, str5, str6));
                            }
                            this.zzbvK.zzoo();
                            atomicReference2.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzd("Failed to get conditional properties", zzatx.zzfE(str4), str5, e);
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
            final /* synthetic */ zzaul zzbvK;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference2) {
                    try {
                        zzatt zzd = this.zzbvK.zzbvE;
                        if (zzd == null) {
                            this.zzbvK.zzKl().zzLZ().zzd("Failed to get user properties", zzatx.zzfE(str4), str5, str6);
                            atomicReference2.set(Collections.emptyList());
                        } else {
                            if (TextUtils.isEmpty(str4)) {
                                atomicReference2.set(zzd.zza(str5, str6, z2, this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg())));
                            } else {
                                atomicReference2.set(zzd.zza(str4, str5, str6, z2));
                            }
                            this.zzbvK.zzoo();
                            atomicReference2.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzd("Failed to get user properties", zzatx.zzfE(str4), str5, e);
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
            final /* synthetic */ zzaul zzbvK;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                synchronized (atomicReference) {
                    try {
                        zzatt zzd = this.zzbvK.zzbvE;
                        if (zzd == null) {
                            this.zzbvK.zzKl().zzLZ().log("Failed to get user properties");
                        } else {
                            atomicReference.set(zzd.zza(this.zzbvK.zzKb().zzfD(null), z));
                            this.zzbvK.zzoo();
                            atomicReference.notify();
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzj("Failed to get user properties", e);
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
        zzKn().zzLh();
        final boolean z = zzKf().zza(com_google_android_gms_internal_zzauq);
        zzo(new Runnable(this) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Discarding data. Failed to set user attribute");
                    return;
                }
                this.zzbvK.zza(zzd, z ? null : com_google_android_gms_internal_zzauq);
                this.zzbvK.zzoo();
            }
        });
    }

    @WorkerThread
    protected void zzc(zzatq com_google_android_gms_internal_zzatq, String str) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzmR();
        zzob();
        int i = VERSION.SDK_INT;
        zzKn().zzLh();
        final boolean z = zzKf().zza(com_google_android_gms_internal_zzatq);
        final zzatq com_google_android_gms_internal_zzatq2 = com_google_android_gms_internal_zzatq;
        final String str2 = str;
        zzo(new Runnable(this, true) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Discarding data. Failed to send event to service");
                    return;
                }
                if (true) {
                    this.zzbvK.zza(zzd, z ? null : com_google_android_gms_internal_zzatq2);
                } else {
                    try {
                        if (TextUtils.isEmpty(str2)) {
                            zzd.zza(com_google_android_gms_internal_zzatq2, this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg()));
                        } else {
                            zzd.zza(com_google_android_gms_internal_zzatq2, str2, this.zzbvK.zzKl().zzMg());
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzj("Failed to send event to the service", e);
                    }
                }
                this.zzbvK.zzoo();
            }
        });
    }

    @WorkerThread
    protected void zzf(zzatg com_google_android_gms_internal_zzatg) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzmR();
        zzob();
        zzKn().zzLh();
        final boolean z = zzKf().zzc(com_google_android_gms_internal_zzatg);
        final zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
        final zzatg com_google_android_gms_internal_zzatg3 = com_google_android_gms_internal_zzatg;
        zzo(new Runnable(this, true) {
            final /* synthetic */ zzaul zzbvK;

            public void run() {
                zzatt zzd = this.zzbvK.zzbvE;
                if (zzd == null) {
                    this.zzbvK.zzKl().zzLZ().log("Discarding data. Failed to send conditional user property to service");
                    return;
                }
                if (true) {
                    this.zzbvK.zza(zzd, z ? null : com_google_android_gms_internal_zzatg2);
                } else {
                    try {
                        if (TextUtils.isEmpty(com_google_android_gms_internal_zzatg3.packageName)) {
                            zzd.zza(com_google_android_gms_internal_zzatg2, this.zzbvK.zzKb().zzfD(this.zzbvK.zzKl().zzMg()));
                        } else {
                            zzd.zzb(com_google_android_gms_internal_zzatg2);
                        }
                    } catch (RemoteException e) {
                        this.zzbvK.zzKl().zzLZ().zzj("Failed to send conditional user property to the service", e);
                    }
                }
                this.zzbvK.zzoo();
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
            if (this.zzbvF == null) {
                this.zzbvF = zzKm().zzMn();
                if (this.zzbvF == null) {
                    zzKl().zzMf().log("State of service unknown");
                    this.zzbvF = Boolean.valueOf(zzNa());
                    zzKm().zzaJ(this.zzbvF.booleanValue());
                }
            }
            if (this.zzbvF.booleanValue()) {
                zzKl().zzMf().log("Using measurement service");
                this.zzbvD.zzNd();
            } else if (zzMZ()) {
                zzKl().zzMf().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = getContext();
                zzKn().zzLh();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzbvD.zzz(intent);
            } else {
                zzKl().zzLZ().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
            }
        }
    }
}
