package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

public abstract class fa<T> {
    private final Context mContext;
    private final Object mLock = new Object();
    private final String mTag;
    private boolean zzbNJ = false;
    private T zzbNK;

    public fa(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public final boolean isOperational() {
        return zzDQ() != null;
    }

    protected abstract void zzDN() throws RemoteException;

    public final void zzDP() {
        synchronized (this.mLock) {
            if (this.zzbNK == null) {
                return;
            }
            try {
                zzDN();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected final T zzDQ() {
        T t;
        Throwable e;
        synchronized (this.mLock) {
            if (this.zzbNK != null) {
                t = this.zzbNK;
            } else {
                try {
                    this.zzbNK = zza(DynamiteModule.zza(this.mContext, DynamiteModule.zzaSO, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (zzc e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.zzbNJ) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbNK;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.zzbNJ) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbNK;
                    return t;
                }
                if (!this.zzbNJ && this.zzbNK == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.zzbNJ = true;
                } else if (this.zzbNJ && this.zzbNK != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.zzbNK;
            }
        }
        return t;
    }

    protected abstract T zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, zzc;
}
