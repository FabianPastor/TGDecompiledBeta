package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzm implements Callback {
    private final Handler mHandler;
    private final zza zzaFU;
    private final ArrayList<ConnectionCallbacks> zzaFV = new ArrayList();
    final ArrayList<ConnectionCallbacks> zzaFW = new ArrayList();
    private final ArrayList<OnConnectionFailedListener> zzaFX = new ArrayList();
    private volatile boolean zzaFY = false;
    private final AtomicInteger zzaFZ = new AtomicInteger(0);
    private boolean zzaGa = false;
    private final Object zzrJ = new Object();

    public interface zza {
        boolean isConnected();

        Bundle zzuC();
    }

    public zzm(Looper looper, zza com_google_android_gms_common_internal_zzm_zza) {
        this.zzaFU = com_google_android_gms_common_internal_zzm_zza;
        this.mHandler = new Handler(looper, this);
    }

    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) message.obj;
            synchronized (this.zzrJ) {
                if (this.zzaFY && this.zzaFU.isConnected() && this.zzaFV.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(this.zzaFU.zzuC());
                }
            }
            return true;
        }
        Log.wtf("GmsClientEvents", "Don't know how to handle message: " + message.what, new Exception());
        return false;
    }

    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        boolean contains;
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrJ) {
            contains = this.zzaFV.contains(connectionCallbacks);
        }
        return contains;
    }

    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener) {
        boolean contains;
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrJ) {
            contains = this.zzaFX.contains(onConnectionFailedListener);
        }
        return contains;
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrJ) {
            if (this.zzaFV.contains(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzaFV.add(connectionCallbacks);
            }
        }
        if (this.zzaFU.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, connectionCallbacks));
        }
    }

    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrJ) {
            if (this.zzaFX.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzaFX.add(onConnectionFailedListener);
            }
        }
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrJ) {
            if (!this.zzaFV.remove(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 52).append("unregisterConnectionCallbacks(): listener ").append(valueOf).append(" not found").toString());
            } else if (this.zzaGa) {
                this.zzaFW.add(connectionCallbacks);
            }
        }
    }

    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrJ) {
            if (!this.zzaFX.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    public void zzcV(int i) {
        boolean z = false;
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            z = true;
        }
        zzac.zza(z, (Object) "onUnintentionalDisconnection must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzrJ) {
            this.zzaGa = true;
            ArrayList arrayList = new ArrayList(this.zzaFV);
            int i2 = this.zzaFZ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) it.next();
                if (!this.zzaFY || this.zzaFZ.get() != i2) {
                    break;
                } else if (this.zzaFV.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnectionSuspended(i);
                }
            }
            this.zzaFW.clear();
            this.zzaGa = false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzn(ConnectionResult connectionResult) {
        zzac.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionFailure must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzrJ) {
            ArrayList arrayList = new ArrayList(this.zzaFX);
            int i = this.zzaFZ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                OnConnectionFailedListener onConnectionFailedListener = (OnConnectionFailedListener) it.next();
                if (!this.zzaFY || this.zzaFZ.get() != i) {
                } else if (this.zzaFX.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }

    public void zzq(Bundle bundle) {
        boolean z = true;
        zzac.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionSuccess must only be called on the Handler thread");
        synchronized (this.zzrJ) {
            zzac.zzaw(!this.zzaGa);
            this.mHandler.removeMessages(1);
            this.zzaGa = true;
            if (this.zzaFW.size() != 0) {
                z = false;
            }
            zzac.zzaw(z);
            ArrayList arrayList = new ArrayList(this.zzaFV);
            int i = this.zzaFZ.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) it.next();
                if (!this.zzaFY || !this.zzaFU.isConnected() || this.zzaFZ.get() != i) {
                    break;
                } else if (!this.zzaFW.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(bundle);
                }
            }
            this.zzaFW.clear();
            this.zzaGa = false;
        }
    }

    public void zzxX() {
        this.zzaFY = false;
        this.zzaFZ.incrementAndGet();
    }

    public void zzxY() {
        this.zzaFY = true;
    }
}
