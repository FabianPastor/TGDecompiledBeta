package com.google.android.gms.common.internal;

import android.util.Log;

public abstract class zzi<TListener> {
    private TListener zzfuk;
    private /* synthetic */ zzd zzfza;
    private boolean zzfzb = false;

    public zzi(zzd com_google_android_gms_common_internal_zzd, TListener tListener) {
        this.zzfza = com_google_android_gms_common_internal_zzd;
        this.zzfuk = tListener;
    }

    public final void removeListener() {
        synchronized (this) {
            this.zzfuk = null;
        }
    }

    public final void unregister() {
        removeListener();
        synchronized (this.zzfza.zzfyo) {
            this.zzfza.zzfyo.remove(this);
        }
    }

    public final void zzaks() {
        synchronized (this) {
            Object obj = this.zzfuk;
            if (this.zzfzb) {
                String valueOf = String.valueOf(this);
                Log.w("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Callback proxy ").append(valueOf).append(" being reused. This is not safe.").toString());
            }
        }
        if (obj != null) {
            try {
                zzw(obj);
            } catch (RuntimeException e) {
                throw e;
            }
        }
        synchronized (this) {
            this.zzfzb = true;
        }
        unregister();
    }

    protected abstract void zzw(TListener tListener);
}
