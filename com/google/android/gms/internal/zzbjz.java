package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public abstract class zzbjz<T> {
    private final Context mContext;
    private final String mTag;
    private boolean zzbPn = false;
    private T zzbPo;
    private final Object zzrJ = new Object();

    public zzbjz(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public boolean isOperational() {
        return zzTR() != null;
    }

    protected abstract void zzTO() throws RemoteException;

    public void zzTQ() {
        synchronized (this.zzrJ) {
            if (this.zzbPo == null) {
                return;
            }
            try {
                zzTO();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected T zzTR() {
        T t;
        Throwable e;
        synchronized (this.zzrJ) {
            if (this.zzbPo != null) {
                t = this.zzbPo;
            } else {
                try {
                    this.zzbPo = zzb(DynamiteModule.zza(this.mContext, DynamiteModule.zzaRX, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (zza e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.zzbPn) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbPo;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.zzbPn) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbPo;
                    return t;
                }
                if (!this.zzbPn && this.zzbPo == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.zzbPn = true;
                } else if (this.zzbPn && this.zzbPo != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.zzbPo;
            }
        }
        return t;
    }

    protected abstract T zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza;
}
