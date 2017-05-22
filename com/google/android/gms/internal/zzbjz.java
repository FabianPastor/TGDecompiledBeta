package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public abstract class zzbjz<T> {
    private final Context mContext;
    private final String mTag;
    private boolean zzbPm = false;
    private T zzbPn;
    private final Object zzrJ = new Object();

    public zzbjz(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public boolean isOperational() {
        return zzTU() != null;
    }

    protected abstract void zzTR() throws RemoteException;

    public void zzTT() {
        synchronized (this.zzrJ) {
            if (this.zzbPn == null) {
                return;
            }
            try {
                zzTR();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected T zzTU() {
        T t;
        Throwable e;
        synchronized (this.zzrJ) {
            if (this.zzbPn != null) {
                t = this.zzbPn;
            } else {
                try {
                    this.zzbPn = zzb(DynamiteModule.zza(this.mContext, DynamiteModule.zzaRX, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (zza e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.zzbPm) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbPn;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.zzbPm) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbPn;
                    return t;
                }
                if (!this.zzbPm && this.zzbPn == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.zzbPm = true;
                } else if (this.zzbPm && this.zzbPn != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.zzbPn;
            }
        }
        return t;
    }

    protected abstract T zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza;
}
