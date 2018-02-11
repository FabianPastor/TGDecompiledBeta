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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.stats.zza;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class zzcku implements ServiceConnection, zzf, zzg {
    final /* synthetic */ zzckg zzjij;
    private volatile boolean zzjiq;
    private volatile zzchl zzjir;

    protected zzcku(zzckg com_google_android_gms_internal_zzckg) {
        this.zzjij = com_google_android_gms_internal_zzckg;
    }

    public final void onConnected(Bundle bundle) {
        zzbq.zzge("MeasurementServiceConnection.onConnected");
        synchronized (this) {
            try {
                zzche com_google_android_gms_internal_zzche = (zzche) this.zzjir.zzakn();
                this.zzjir = null;
                this.zzjij.zzawx().zzg(new zzckx(this, com_google_android_gms_internal_zzche));
            } catch (DeadObjectException e) {
                this.zzjir = null;
                this.zzjiq = false;
            } catch (IllegalStateException e2) {
                this.zzjir = null;
                this.zzjiq = false;
            }
        }
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        zzbq.zzge("MeasurementServiceConnection.onConnectionFailed");
        zzchm zzazx = this.zzjij.zziwf.zzazx();
        if (zzazx != null) {
            zzazx.zzazf().zzj("Service connection failed", connectionResult);
        }
        synchronized (this) {
            this.zzjiq = false;
            this.zzjir = null;
        }
        this.zzjij.zzawx().zzg(new zzckz(this));
    }

    public final void onConnectionSuspended(int i) {
        zzbq.zzge("MeasurementServiceConnection.onConnectionSuspended");
        this.zzjij.zzawy().zzazi().log("Service connection suspended");
        this.zzjij.zzawx().zzg(new zzcky(this));
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        zzbq.zzge("MeasurementServiceConnection.onServiceConnected");
        synchronized (this) {
            if (iBinder == null) {
                this.zzjiq = false;
                this.zzjij.zzawy().zzazd().log("Service connected with null binder");
                return;
            }
            zzche com_google_android_gms_internal_zzche;
            try {
                String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                    if (iBinder == null) {
                        com_google_android_gms_internal_zzche = null;
                    } else {
                        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                        com_google_android_gms_internal_zzche = queryLocalInterface instanceof zzche ? (zzche) queryLocalInterface : new zzchg(iBinder);
                    }
                    try {
                        this.zzjij.zzawy().zzazj().log("Bound to IMeasurementService interface");
                    } catch (RemoteException e) {
                        this.zzjij.zzawy().zzazd().log("Service connect failed to get IMeasurementService");
                        if (com_google_android_gms_internal_zzche != null) {
                            this.zzjiq = false;
                            try {
                                zza.zzamc();
                                this.zzjij.getContext().unbindService(this.zzjij.zzjic);
                            } catch (IllegalArgumentException e2) {
                            }
                        } else {
                            this.zzjij.zzawx().zzg(new zzckv(this, com_google_android_gms_internal_zzche));
                        }
                    }
                    if (com_google_android_gms_internal_zzche != null) {
                        this.zzjiq = false;
                        zza.zzamc();
                        this.zzjij.getContext().unbindService(this.zzjij.zzjic);
                    } else {
                        this.zzjij.zzawx().zzg(new zzckv(this, com_google_android_gms_internal_zzche));
                    }
                }
                this.zzjij.zzawy().zzazd().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                com_google_android_gms_internal_zzche = null;
                if (com_google_android_gms_internal_zzche != null) {
                    this.zzjij.zzawx().zzg(new zzckv(this, com_google_android_gms_internal_zzche));
                } else {
                    this.zzjiq = false;
                    zza.zzamc();
                    this.zzjij.getContext().unbindService(this.zzjij.zzjic);
                }
            } catch (RemoteException e3) {
                com_google_android_gms_internal_zzche = null;
                this.zzjij.zzawy().zzazd().log("Service connect failed to get IMeasurementService");
                if (com_google_android_gms_internal_zzche != null) {
                    this.zzjij.zzawx().zzg(new zzckv(this, com_google_android_gms_internal_zzche));
                } else {
                    this.zzjiq = false;
                    zza.zzamc();
                    this.zzjij.getContext().unbindService(this.zzjij.zzjic);
                }
            }
        }
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        zzbq.zzge("MeasurementServiceConnection.onServiceDisconnected");
        this.zzjij.zzawy().zzazi().log("Service disconnected");
        this.zzjij.zzawx().zzg(new zzckw(this, componentName));
    }

    public final void zzbau() {
        this.zzjij.zzve();
        Context context = this.zzjij.getContext();
        synchronized (this) {
            if (this.zzjiq) {
                this.zzjij.zzawy().zzazj().log("Connection attempt already in progress");
            } else if (this.zzjir != null) {
                this.zzjij.zzawy().zzazj().log("Already awaiting connection attempt");
            } else {
                this.zzjir = new zzchl(context, Looper.getMainLooper(), this, this);
                this.zzjij.zzawy().zzazj().log("Connecting to remote service");
                this.zzjiq = true;
                this.zzjir.zzakj();
            }
        }
    }

    public final void zzn(Intent intent) {
        this.zzjij.zzve();
        Context context = this.zzjij.getContext();
        zza zzamc = zza.zzamc();
        synchronized (this) {
            if (this.zzjiq) {
                this.zzjij.zzawy().zzazj().log("Connection attempt already in progress");
                return;
            }
            this.zzjij.zzawy().zzazj().log("Using local app measurement service");
            this.zzjiq = true;
            zzamc.zza(context, intent, this.zzjij.zzjic, TsExtractor.TS_STREAM_TYPE_AC3);
        }
    }
}
