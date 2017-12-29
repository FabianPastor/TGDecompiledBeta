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
import java.util.concurrent.atomic.AtomicInteger;

public final class zzae implements Callback {
    private final Handler mHandler;
    private final Object mLock = new Object();
    private final zzaf zzgab;
    private final ArrayList<ConnectionCallbacks> zzgac = new ArrayList();
    private ArrayList<ConnectionCallbacks> zzgad = new ArrayList();
    private final ArrayList<OnConnectionFailedListener> zzgae = new ArrayList();
    private volatile boolean zzgaf = false;
    private final AtomicInteger zzgag = new AtomicInteger(0);
    private boolean zzgah = false;

    public zzae(Looper looper, zzaf com_google_android_gms_common_internal_zzaf) {
        this.zzgab = com_google_android_gms_common_internal_zzaf;
        this.mHandler = new Handler(looper, this);
    }

    public final boolean handleMessage(Message message) {
        if (message.what == 1) {
            ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) message.obj;
            synchronized (this.mLock) {
                if (this.zzgaf && this.zzgab.isConnected() && this.zzgac.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(this.zzgab.zzafi());
                }
            }
            return true;
        }
        Log.wtf("GmsClientEvents", "Don't know how to handle message: " + message.what, new Exception());
        return false;
    }

    public final void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzbq.checkNotNull(connectionCallbacks);
        synchronized (this.mLock) {
            if (this.zzgac.contains(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzgac.add(connectionCallbacks);
            }
        }
        if (this.zzgab.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, connectionCallbacks));
        }
    }

    public final void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzbq.checkNotNull(onConnectionFailedListener);
        synchronized (this.mLock) {
            if (this.zzgae.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzgae.add(onConnectionFailedListener);
            }
        }
    }

    public final void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzbq.checkNotNull(onConnectionFailedListener);
        synchronized (this.mLock) {
            if (!this.zzgae.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    public final void zzali() {
        this.zzgaf = false;
        this.zzgag.incrementAndGet();
    }

    public final void zzalj() {
        this.zzgaf = true;
    }

    public final void zzcg(int i) {
        int i2 = 0;
        zzbq.zza(Looper.myLooper() == this.mHandler.getLooper(), "onUnintentionalDisconnection must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.mLock) {
            this.zzgah = true;
            ArrayList arrayList = new ArrayList(this.zzgac);
            int i3 = this.zzgag.get();
            arrayList = arrayList;
            int size = arrayList.size();
            while (i2 < size) {
                Object obj = arrayList.get(i2);
                i2++;
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) obj;
                if (this.zzgaf && this.zzgag.get() == i3) {
                    if (this.zzgac.contains(connectionCallbacks)) {
                        connectionCallbacks.onConnectionSuspended(i);
                    }
                }
            }
            this.zzgad.clear();
            this.zzgah = false;
        }
    }

    public final void zzk(Bundle bundle) {
        boolean z = true;
        int i = 0;
        zzbq.zza(Looper.myLooper() == this.mHandler.getLooper(), "onConnectionSuccess must only be called on the Handler thread");
        synchronized (this.mLock) {
            zzbq.checkState(!this.zzgah);
            this.mHandler.removeMessages(1);
            this.zzgah = true;
            if (this.zzgad.size() != 0) {
                z = false;
            }
            zzbq.checkState(z);
            ArrayList arrayList = new ArrayList(this.zzgac);
            int i2 = this.zzgag.get();
            arrayList = arrayList;
            int size = arrayList.size();
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) obj;
                if (this.zzgaf && this.zzgab.isConnected() && this.zzgag.get() == i2) {
                    if (!this.zzgad.contains(connectionCallbacks)) {
                        connectionCallbacks.onConnected(bundle);
                    }
                }
            }
            this.zzgad.clear();
            this.zzgah = false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzk(ConnectionResult connectionResult) {
        int i = 0;
        zzbq.zza(Looper.myLooper() == this.mHandler.getLooper(), "onConnectionFailure must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.mLock) {
            ArrayList arrayList = new ArrayList(this.zzgae);
            int i2 = this.zzgag.get();
            arrayList = arrayList;
            int size = arrayList.size();
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                OnConnectionFailedListener onConnectionFailedListener = (OnConnectionFailedListener) obj;
                if (!this.zzgaf || this.zzgag.get() != i2) {
                } else if (this.zzgae.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }
}
