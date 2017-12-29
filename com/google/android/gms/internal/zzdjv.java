package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

public abstract class zzdjv<T> {
    private final Context mContext;
    private final Object mLock = new Object();
    private final String mTag;
    private boolean zzkxs = false;
    private T zzkxt;

    public zzdjv(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public final boolean isOperational() {
        return zzbjv() != null;
    }

    protected abstract T zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc;

    protected abstract void zzbjs() throws RemoteException;

    public final void zzbju() {
        synchronized (this.mLock) {
            if (this.zzkxt == null) {
                return;
            }
            try {
                zzbjs();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected final T zzbjv() {
        T t;
        Throwable e;
        synchronized (this.mLock) {
            if (this.zzkxt != null) {
                t = this.zzkxt;
            } else {
                try {
                    this.zzkxt = zza(DynamiteModule.zza(this.mContext, DynamiteModule.zzgxa, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (zzc e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.zzkxs) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzkxt;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.zzkxs) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzkxt;
                    return t;
                }
                if (!this.zzkxs && this.zzkxt == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.zzkxs = true;
                } else if (this.zzkxs && this.zzkxt != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.zzkxt;
            }
        }
        return t;
    }
}
