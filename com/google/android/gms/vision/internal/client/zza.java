package com.google.android.gms.vision.internal.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zzsu;

public abstract class zza<T> {
    private boolean aLr = false;
    private T aLs;
    private final Context mContext;
    private final String mTag;
    private final Object zzakd = new Object();

    public zza(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public boolean isOperational() {
        return zzclt() != null;
    }

    protected abstract T zzb(zzsu com_google_android_gms_internal_zzsu, Context context) throws RemoteException, com.google.android.gms.internal.zzsu.zza;

    protected abstract void zzclq() throws RemoteException;

    public void zzcls() {
        synchronized (this.zzakd) {
            if (this.aLs == null) {
                return;
            }
            try {
                zzclq();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected T zzclt() {
        T t;
        Throwable e;
        synchronized (this.zzakd) {
            if (this.aLs != null) {
                t = this.aLs;
            } else {
                try {
                    this.aLs = zzb(zzsu.zza(this.mContext, zzsu.OB, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (com.google.android.gms.internal.zzsu.zza e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.aLr) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.aLs;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.aLr) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.aLs;
                    return t;
                }
                if (!this.aLr && this.aLs == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.aLr = true;
                } else if (this.aLr && this.aLs != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.aLs;
            }
        }
        return t;
    }
}
