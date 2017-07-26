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

public final class zzcid extends zzchj {
    private final zzciq zzbtT;
    private zzcfd zzbtU;
    private Boolean zzbtV;
    private final zzcer zzbtW;
    private final zzcjf zzbtX;
    private final List<Runnable> zzbtY = new ArrayList();
    private final zzcer zzbtZ;

    protected zzcid(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
        this.zzbtX = new zzcjf(com_google_android_gms_internal_zzcgl.zzkq());
        this.zzbtT = new zzciq(this);
        this.zzbtW = new zzcie(this, com_google_android_gms_internal_zzcgl);
        this.zzbtZ = new zzcii(this, com_google_android_gms_internal_zzcgl);
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
        this.zzbtW.zzs(zzcem.zzxB());
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
        } else if (((long) this.zzbtY.size()) >= zzcem.zzxJ()) {
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
    protected final void zza(zzcfd com_google_android_gms_internal_zzcfd) {
        super.zzjC();
        zzbo.zzu(com_google_android_gms_internal_zzcfd);
        this.zzbtU = com_google_android_gms_internal_zzcfd;
        zzkP();
        zzzl();
    }

    @WorkerThread
    final void zza(zzcfd com_google_android_gms_internal_zzcfd, com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza) {
        super.zzjC();
        super.zzwp();
        zzkD();
        zzcem.zzxE();
        List arrayList = new ArrayList();
        zzcem.zzxN();
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
                if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcez) {
                    try {
                        com_google_android_gms_internal_zzcfd.zza((zzcez) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e) {
                        super.zzwF().zzyx().zzj("Failed to send event to the service", e);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcji) {
                    try {
                        com_google_android_gms_internal_zzcfd.zza((zzcji) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
                    } catch (RemoteException e2) {
                        super.zzwF().zzyx().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzcek) {
                    try {
                        com_google_android_gms_internal_zzcfd.zza((zzcek) com_google_android_gms_common_internal_safeparcel_zza2, super.zzwu().zzdV(super.zzwF().zzyE()));
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
        zzm(new zzcih(this, com_google_android_gms_measurement_AppMeasurement_zzb));
    }

    @WorkerThread
    public final void zza(AtomicReference<String> atomicReference) {
        super.zzjC();
        zzkD();
        zzm(new zzcif(this, atomicReference));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcek>> atomicReference, String str, String str2, String str3) {
        super.zzjC();
        zzkD();
        zzm(new zzcim(this, atomicReference, str, str2, str3));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcji>> atomicReference, String str, String str2, String str3, boolean z) {
        super.zzjC();
        zzkD();
        zzm(new zzcin(this, atomicReference, str, str2, str3, z));
    }

    @WorkerThread
    protected final void zza(AtomicReference<List<zzcji>> atomicReference, boolean z) {
        super.zzjC();
        zzkD();
        zzm(new zzcip(this, atomicReference, z));
    }

    @WorkerThread
    protected final void zzb(zzcji com_google_android_gms_internal_zzcji) {
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcio(this, super.zzwy().zza(com_google_android_gms_internal_zzcji), com_google_android_gms_internal_zzcji));
    }

    @WorkerThread
    protected final void zzc(zzcez com_google_android_gms_internal_zzcez, String str) {
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcik(this, true, super.zzwy().zza(com_google_android_gms_internal_zzcez), com_google_android_gms_internal_zzcez, str));
    }

    @WorkerThread
    protected final void zzf(zzcek com_google_android_gms_internal_zzcek) {
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        super.zzjC();
        zzkD();
        zzcem.zzxE();
        zzm(new zzcil(this, true, super.zzwy().zzc(com_google_android_gms_internal_zzcek), new zzcek(com_google_android_gms_internal_zzcek), com_google_android_gms_internal_zzcek));
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
                    zzcem.zzxE();
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
            zzcem.zzxE();
            List queryIntentServices = super.getContext().getPackageManager().queryIntentServices(new Intent().setClassName(super.getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
            if (queryIntentServices == null || queryIntentServices.size() <= 0) {
                obj = null;
            }
            if (obj != null) {
                super.zzwF().zzyD().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = super.getContext();
                zzcem.zzxE();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzbtT.zzk(intent);
                return;
            }
            super.zzwF().zzyx().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
        }
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

    @WorkerThread
    protected final void zzzj() {
        super.zzjC();
        zzkD();
        zzm(new zzcij(this));
    }

    @WorkerThread
    protected final void zzzk() {
        super.zzjC();
        zzkD();
        zzm(new zzcig(this));
    }
}
