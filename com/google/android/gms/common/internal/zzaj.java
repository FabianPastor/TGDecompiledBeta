package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.HashSet;
import java.util.Set;

final class zzaj implements ServiceConnection {
    private ComponentName mComponentName;
    private int mState = 2;
    private IBinder zzfzf;
    private final Set<ServiceConnection> zzgaq = new HashSet();
    private boolean zzgar;
    private final zzah zzgas;
    private /* synthetic */ zzai zzgat;

    public zzaj(zzai com_google_android_gms_common_internal_zzai, zzah com_google_android_gms_common_internal_zzah) {
        this.zzgat = com_google_android_gms_common_internal_zzai;
        this.zzgas = com_google_android_gms_common_internal_zzah;
    }

    public final IBinder getBinder() {
        return this.zzfzf;
    }

    public final ComponentName getComponentName() {
        return this.mComponentName;
    }

    public final int getState() {
        return this.mState;
    }

    public final boolean isBound() {
        return this.zzgar;
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this.zzgat.zzgam) {
            this.zzgat.mHandler.removeMessages(1, this.zzgas);
            this.zzfzf = iBinder;
            this.mComponentName = componentName;
            for (ServiceConnection onServiceConnected : this.zzgaq) {
                onServiceConnected.onServiceConnected(componentName, iBinder);
            }
            this.mState = 1;
        }
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        synchronized (this.zzgat.zzgam) {
            this.zzgat.mHandler.removeMessages(1, this.zzgas);
            this.zzfzf = null;
            this.mComponentName = componentName;
            for (ServiceConnection onServiceDisconnected : this.zzgaq) {
                onServiceDisconnected.onServiceDisconnected(componentName);
            }
            this.mState = 2;
        }
    }

    public final void zza(ServiceConnection serviceConnection, String str) {
        this.zzgat.zzgan;
        this.zzgat.mApplicationContext;
        this.zzgas.zzall();
        this.zzgaq.add(serviceConnection);
    }

    public final boolean zza(ServiceConnection serviceConnection) {
        return this.zzgaq.contains(serviceConnection);
    }

    public final boolean zzalm() {
        return this.zzgaq.isEmpty();
    }

    public final void zzb(ServiceConnection serviceConnection, String str) {
        this.zzgat.zzgan;
        this.zzgat.mApplicationContext;
        this.zzgaq.remove(serviceConnection);
    }

    public final void zzgi(String str) {
        this.mState = 3;
        this.zzgar = this.zzgat.zzgan.zza(this.zzgat.mApplicationContext, str, this.zzgas.zzall(), this, this.zzgas.zzalk());
        if (this.zzgar) {
            this.zzgat.mHandler.sendMessageDelayed(this.zzgat.mHandler.obtainMessage(1, this.zzgas), this.zzgat.zzgap);
            return;
        }
        this.mState = 2;
        try {
            this.zzgat.zzgan;
            this.zzgat.mApplicationContext.unbindService(this);
        } catch (IllegalArgumentException e) {
        }
    }

    public final void zzgj(String str) {
        this.zzgat.mHandler.removeMessages(1, this.zzgas);
        this.zzgat.zzgan;
        this.zzgat.mApplicationContext.unbindService(this);
        this.zzgar = false;
        this.mState = 2;
    }
}
