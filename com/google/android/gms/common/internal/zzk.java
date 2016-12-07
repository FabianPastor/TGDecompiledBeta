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

public final class zzk implements Callback {
    private final zza Ee;
    private final ArrayList<ConnectionCallbacks> Ef = new ArrayList();
    final ArrayList<ConnectionCallbacks> Eg = new ArrayList();
    private final ArrayList<OnConnectionFailedListener> Eh = new ArrayList();
    private volatile boolean Ei = false;
    private final AtomicInteger Ej = new AtomicInteger(0);
    private boolean Ek = false;
    private final Handler mHandler;
    private final Object zzako = new Object();

    public interface zza {
        boolean isConnected();

        Bundle zzapn();
    }

    public zzk(Looper looper, zza com_google_android_gms_common_internal_zzk_zza) {
        this.Ee = com_google_android_gms_common_internal_zzk_zza;
        this.mHandler = new Handler(looper, this);
    }

    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) message.obj;
            synchronized (this.zzako) {
                if (this.Ei && this.Ee.isConnected() && this.Ef.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(this.Ee.zzapn());
                }
            }
            return true;
        }
        Log.wtf("GmsClientEvents", "Don't know how to handle message: " + message.what, new Exception());
        return false;
    }

    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        boolean contains;
        zzaa.zzy(connectionCallbacks);
        synchronized (this.zzako) {
            contains = this.Ef.contains(connectionCallbacks);
        }
        return contains;
    }

    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener) {
        boolean contains;
        zzaa.zzy(onConnectionFailedListener);
        synchronized (this.zzako) {
            contains = this.Eh.contains(onConnectionFailedListener);
        }
        return contains;
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzaa.zzy(connectionCallbacks);
        synchronized (this.zzako) {
            if (this.Ef.contains(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.Ef.add(connectionCallbacks);
            }
        }
        if (this.Ee.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, connectionCallbacks));
        }
    }

    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzaa.zzy(onConnectionFailedListener);
        synchronized (this.zzako) {
            if (this.Eh.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.Eh.add(onConnectionFailedListener);
            }
        }
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzaa.zzy(connectionCallbacks);
        synchronized (this.zzako) {
            if (!this.Ef.remove(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 52).append("unregisterConnectionCallbacks(): listener ").append(valueOf).append(" not found").toString());
            } else if (this.Ek) {
                this.Eg.add(connectionCallbacks);
            }
        }
    }

    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzaa.zzy(onConnectionFailedListener);
        synchronized (this.zzako) {
            if (!this.Eh.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    public void zzawc() {
        this.Ei = false;
        this.Ej.incrementAndGet();
    }

    public void zzawd() {
        this.Ei = true;
    }

    public void zzgn(int i) {
        boolean z = false;
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            z = true;
        }
        zzaa.zza(z, (Object) "onUnintentionalDisconnection must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzako) {
            this.Ek = true;
            ArrayList arrayList = new ArrayList(this.Ef);
            int i2 = this.Ej.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) it.next();
                if (!this.Ei || this.Ej.get() != i2) {
                    break;
                } else if (this.Ef.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnectionSuspended(i);
                }
            }
            this.Eg.clear();
            this.Ek = false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzn(ConnectionResult connectionResult) {
        zzaa.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionFailure must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzako) {
            ArrayList arrayList = new ArrayList(this.Eh);
            int i = this.Ej.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                OnConnectionFailedListener onConnectionFailedListener = (OnConnectionFailedListener) it.next();
                if (!this.Ei || this.Ej.get() != i) {
                } else if (this.Eh.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }

    public void zzp(Bundle bundle) {
        boolean z = true;
        zzaa.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionSuccess must only be called on the Handler thread");
        synchronized (this.zzako) {
            zzaa.zzbs(!this.Ek);
            this.mHandler.removeMessages(1);
            this.Ek = true;
            if (this.Eg.size() != 0) {
                z = false;
            }
            zzaa.zzbs(z);
            ArrayList arrayList = new ArrayList(this.Ef);
            int i = this.Ej.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) it.next();
                if (!this.Ei || !this.Ee.isConnected() || this.Ej.get() != i) {
                    break;
                } else if (!this.Eg.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(bundle);
                }
            }
            this.Eg.clear();
            this.Ek = false;
        }
    }
}
