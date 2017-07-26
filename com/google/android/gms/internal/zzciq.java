package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.stats.zza;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class zzciq implements ServiceConnection, zzf, zzg {
    final /* synthetic */ zzcid zzbua;
    private volatile boolean zzbuh;
    private volatile zzcfk zzbui;

    protected zzciq(zzcid com_google_android_gms_internal_zzcid) {
        this.zzbua = com_google_android_gms_internal_zzcid;
    }

    @MainThread
    public final void onConnected(@Nullable Bundle bundle) {
        zzbo.zzcz("MeasurementServiceConnection.onConnected");
        synchronized (this) {
            try {
                zzcfd com_google_android_gms_internal_zzcfd = (zzcfd) this.zzbui.zzrf();
                this.zzbui = null;
                this.zzbua.zzwE().zzj(new zzcit(this, com_google_android_gms_internal_zzcfd));
            } catch (DeadObjectException e) {
                this.zzbui = null;
                this.zzbuh = false;
            } catch (IllegalStateException e2) {
                this.zzbui = null;
                this.zzbuh = false;
            }
        }
    }

    @MainThread
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzbo.zzcz("MeasurementServiceConnection.onConnectionFailed");
        zzcfl zzyQ = this.zzbua.zzboe.zzyQ();
        if (zzyQ != null) {
            zzyQ.zzyz().zzj("Service connection failed", connectionResult);
        }
        synchronized (this) {
            this.zzbuh = false;
            this.zzbui = null;
        }
        this.zzbua.zzwE().zzj(new zzciv(this));
    }

    @MainThread
    public final void onConnectionSuspended(int i) {
        zzbo.zzcz("MeasurementServiceConnection.onConnectionSuspended");
        this.zzbua.zzwF().zzyC().log("Service connection suspended");
        this.zzbua.zzwE().zzj(new zzciu(this));
    }

    @MainThread
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        zzcfd com_google_android_gms_internal_zzcfd;
        zzbo.zzcz("MeasurementServiceConnection.onServiceConnected");
        synchronized (this) {
            if (iBinder == null) {
                this.zzbuh = false;
                this.zzbua.zzwF().zzyx().log("Service connected with null binder");
                return;
            }
            try {
                String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                    if (iBinder == null) {
                        com_google_android_gms_internal_zzcfd = null;
                    } else {
                        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                        com_google_android_gms_internal_zzcfd = queryLocalInterface instanceof zzcfd ? (zzcfd) queryLocalInterface : new zzcff(iBinder);
                    }
                    try {
                        this.zzbua.zzwF().zzyD().log("Bound to IMeasurementService interface");
                    } catch (RemoteException e) {
                        this.zzbua.zzwF().zzyx().log("Service connect failed to get IMeasurementService");
                        if (com_google_android_gms_internal_zzcfd != null) {
                            this.zzbuh = false;
                            try {
                                zza.zzrU();
                                this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                            } catch (IllegalArgumentException e2) {
                            }
                        } else {
                            this.zzbua.zzwE().zzj(new zzcir(this, com_google_android_gms_internal_zzcfd));
                        }
                    }
                    if (com_google_android_gms_internal_zzcfd != null) {
                        this.zzbuh = false;
                        zza.zzrU();
                        this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                    } else {
                        this.zzbua.zzwE().zzj(new zzcir(this, com_google_android_gms_internal_zzcfd));
                    }
                }
                this.zzbua.zzwF().zzyx().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                com_google_android_gms_internal_zzcfd = null;
                if (com_google_android_gms_internal_zzcfd != null) {
                    this.zzbua.zzwE().zzj(new zzcir(this, com_google_android_gms_internal_zzcfd));
                } else {
                    this.zzbuh = false;
                    zza.zzrU();
                    this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                }
            } catch (RemoteException e3) {
                com_google_android_gms_internal_zzcfd = null;
                this.zzbua.zzwF().zzyx().log("Service connect failed to get IMeasurementService");
                if (com_google_android_gms_internal_zzcfd != null) {
                    this.zzbua.zzwE().zzj(new zzcir(this, com_google_android_gms_internal_zzcfd));
                } else {
                    this.zzbuh = false;
                    zza.zzrU();
                    this.zzbua.getContext().unbindService(this.zzbua.zzbtT);
                }
            }
        }
    }

    @MainThread
    public final void onServiceDisconnected(ComponentName componentName) {
        zzbo.zzcz("MeasurementServiceConnection.onServiceDisconnected");
        this.zzbua.zzwF().zzyC().log("Service disconnected");
        this.zzbua.zzwE().zzj(new zzcis(this, componentName));
    }

    @WorkerThread
    public final void zzk(Intent intent) {
        this.zzbua.zzjC();
        Context context = this.zzbua.getContext();
        zza zzrU = zza.zzrU();
        synchronized (this) {
            if (this.zzbuh) {
                this.zzbua.zzwF().zzyD().log("Connection attempt already in progress");
                return;
            }
            this.zzbuh = true;
            zzrU.zza(context, intent, this.zzbua.zzbtT, TsExtractor.TS_STREAM_TYPE_AC3);
        }
    }

    @WorkerThread
    public final void zzzm() {
        this.zzbua.zzjC();
        Context context = this.zzbua.getContext();
        synchronized (this) {
            if (this.zzbuh) {
                this.zzbua.zzwF().zzyD().log("Connection attempt already in progress");
            } else if (this.zzbui != null) {
                this.zzbua.zzwF().zzyD().log("Already awaiting connection attempt");
            } else {
                this.zzbui = new zzcfk(context, Looper.getMainLooper(), this, this);
                this.zzbua.zzwF().zzyD().log("Connecting to remote service");
                this.zzbuh = true;
                this.zzbui.zzrb();
            }
        }
    }
}
