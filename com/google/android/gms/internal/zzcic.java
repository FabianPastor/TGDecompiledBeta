package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.WorkerThread;
import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zza;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public final class zzcic extends zzchi {
    private final zzcip zzbtT;
    private zzcfc zzbtU;
    private Boolean zzbtV;
    private final zzceq zzbtW;
    private final zzcje zzbtX;
    private final List<Runnable> zzbtY = new ArrayList();
    private final zzceq zzbtZ;

    protected zzcic(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
        this.zzbtX = new zzcje(com_google_android_gms_internal_zzcgk.zzkq());
        this.zzbtT = new zzcip(this);
        this.zzbtW = new zzcid(this, com_google_android_gms_internal_zzcgk);
        this.zzbtZ = new zzcih(this, com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final void onServiceDisconnected(ComponentName componentName) {
        super.zzjC();
        if (this.zzbtU != null) {
            this.zzbtU = null;
            super.zzwF().zzyD().zzj("Disconnected from device MeasurementService", componentName);
            super.zzjC();
            zzla();
        }
    }

    @WorkerThread
    private final void zzkP() {
        super.zzjC();
        this.zzbtX.start();
        this.zzbtW.zzs(zzcel.zzxB());
    }

    @WorkerThread
    private final void zzkQ() {
        super.zzjC();
        if (isConnected()) {
            super.zzwF().zzyD().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    @WorkerThread
    private final void zzm(Runnable runnable) throws IllegalStateException {
        super.zzjC();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzbtY.size()) >= zzcel.zzxJ()) {
            super.zzwF().zzyx().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzbtY.add(runnable);
            this.zzbtZ.zzs(ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            zzla();
        }
    }

    @WorkerThread
    private final void zzzl() {
        super.zzjC();
        super.zzwF().zzyD().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbtY.size()));
        for (Runnable run : this.zzbtY) {
            try {
                run.run();
            } catch (Throwable th) {
                super.zzwF().zzyx().zzj("Task exception while flushing queue", th);
            }
        }
        this.zzbtY.clear();
        this.zzbtZ.cancel();
    }

    @WorkerThread
    public final void disconnect() {
        super.zzjC();
        zzkD();
        try {
            zza.zzrU();
            super.getContext().unbindService(this.zzbtT);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzbtU = null;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public final boolean isConnected() {
        super.zzjC();
        zzkD();
        return this.zzbtU != null;
    }

    @WorkerThread
    protected final void zza(zzcfc com_google_android_gms_internal_zzcfc) {
        super.zzjC();
        zzbo.zzu(com_google_android_gms_internal_zzcfc);
        this.zzbtU = com_google_android_gms_internal_zzcfc;
        zzkP();
        zzzl();
    }

    @WorkerThread
    final void zza(zzcfc com_google_android_gms_internal_zzcfc, com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza) {
        super.zzjC();
        super.zzwp();
        zzkD();
        zzcel.zzxE();
        List arrayList = new ArrayList();
        zzcel.zzxN();
        int i = 100;
        for (int i2 = 0; i2 < PointerIconCompat.TYPE_CONTEXT_MENU && r5 == 100; i2++) {
            Object zzbp = super.zzwy().zzbp(100);
            if (zzbp != null) {
                arrayList.addAll(zzbp);
                i = zzbp.size();
            } else {
                i = 0;
            }
            if (com_google_android_gms_common_internal_safeparcel_zza != null && r5 < 100) {
                arrayList.add(com_google_android_gms_common_internal_safeparcel_zza);
            }
            ArrayList arrayList2 = (ArrayList) arrayList;
            int size = arrayList2.size();
            int i3 = 0;
            while (i3 < size) {
                Object obj = arrayList2.get(i3);
                i3++;
                com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza2 = (com.google.android.gms.common.internal.safeparcel.zza) obj;
                if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcey) {
                    try {
                        com_google_android_gms_internal_zzcfc.zza((zzcey) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e) {
                        super.zzwF().zzyx().zzj("Failed to send event to the service", e);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcjh) {
                    try {
                        com_google_android_gms_internal_zzcfc.zza((zzcjh) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e2) {
                        super.zzwF().zzyx().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcej) {
                    try {
                        com_google_android_gms_internal_zzcfc.zza((zzcej) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e22) {
                        super.zzwF().zzyx().zzj("Failed to send conditional property to the service", e22);
                    }
                } else {
                    super.zzwF().zzyx().log("Discarding data. Unrecognized parcel type.");
                }
            }
        }
    }

    @WorkerThread
    protected final void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        super.zzjC();
        zzkD();
        zzm(new zzcig(this, com_google_android_gms_measurement_AppMeasurement_zzb));
    }

    @WorkerThread
    public final void zza(AtomicReference<String> atomicReference) {
        super.zzjC();
        zzkD();
        zzm(new zzcie(this, atomicReference));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcej>> atomicReference, String str, String str2, String str3) {
        super.zzjC();
        zzkD();
        zzm(new zzcil(this, atomicReference, str, str2, str3));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcjh>> atomicReference, String str, String str2, String str3, boolean z) {
        super.zzjC();
        zzkD();
        zzm(new zzcim(this, atomicReference, str, str2, str3, z));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcjh>> atomicReference, boolean z) {
        super.zzjC();
        zzkD();
        zzm(new zzcio(this, atomicReference, z));
    }

    @WorkerThread
    protected final void zzb(zzcjh com_google_android_gms_internal_zzcjh) {
        super.zzjC();
        zzkD();
        zzcel.zzxE();
        zzm(new zzcin(this, super.zzwy().zza(com_google_android_gms_internal_zzcjh), com_google_android_gms_internal_zzcjh));
    }

    @WorkerThread
    protected final void zzc(zzcey com_google_android_gms_internal_zzcey, String str) {
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        super.zzjC();
        zzkD();
        zzcel.zzxE();
        zzm(new zzcij(this, true, super.zzwy().zza(com_google_android_gms_internal_zzcey), com_google_android_gms_internal_zzcey, str));
    }

    @WorkerThread
    protected final void zzf(zzcej com_google_android_gms_internal_zzcej) {
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        super.zzjC();
        zzkD();
        zzcel.zzxE();
        zzm(new zzcik(this, true, super.zzwy().zzc(com_google_android_gms_internal_zzcej), new zzcej(com_google_android_gms_internal_zzcej), com_google_android_gms_internal_zzcej));
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @WorkerThread
    final void zzla() {
        Object obj = 1;
        super.zzjC();
        zzkD();
        if (!isConnected()) {
            if (this.zzbtV == null) {
                this.zzbtV = super.zzwG().zzyI();
                if (this.zzbtV == null) {
                    super.zzwF().zzyD().log("State of service unknown");
                    super.zzjC();
                    zzkD();
                    zzcel.zzxE();
                    super.zzwF().zzyD().log("Checking service availability");
                    boolean z;
                    switch (com.google.android.gms.common.zze.zzoW().isGooglePlayServicesAvailable(super.getContext())) {
                        case 0:
                            super.zzwF().zzyD().log("Service available");
                            z = true;
                            break;
                        case 1:
                            super.zzwF().zzyD().log("Service missing");
                            break;
                        case 2:
                            super.zzwF().zzyC().log("Service container out of date");
                            z = true;
                            break;
                        case 3:
                            super.zzwF().zzyz().log("Service disabled");
                            break;
                        case 9:
                            super.zzwF().zzyz().log("Service invalid");
                            break;
                        case 18:
                            super.zzwF().zzyz().log("Service updating");
                            z = true;
                            break;
                    }
                }
            }
            if (this.zzbtV.booleanValue()) {
                super.zzwF().zzyD().log("Using measurement service");
                this.zzbtT.zzzm();
                return;
            }
            zzcel.zzxE();
            List queryIntentServices = super.getContext().getPackageManager().queryIntentServices(new Intent().setClassName(super.getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
            if (queryIntentServices == null || queryIntentServices.size() <= 0) {
                obj = null;
            }
            if (obj != null) {
                super.zzwF().zzyD().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = super.getContext();
                zzcel.zzxE();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzbtT.zzk(intent);
                return;
            }
            super.zzwF().zzyx().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
        }
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
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

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }

    @WorkerThread
    protected final void zzzj() {
        super.zzjC();
        zzkD();
        zzm(new zzcii(this));
    }

    @WorkerThread
    protected final void zzzk() {
        super.zzjC();
        zzkD();
        zzm(new zzcif(this));
    }
}
