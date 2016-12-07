package com.google.android.gms.vision.internal.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.zztl;

public abstract class zza<T> {
    private boolean aOC = false;
    private T aOD;
    private final Context mContext;
    private final String mTag;
    private final Object zzako = new Object();

    public zza(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public boolean isOperational() {
        return zzcls() != null;
    }

    protected abstract T zzb(zztl com_google_android_gms_internal_zztl, Context context) throws RemoteException, com.google.android.gms.internal.zztl.zza;

    protected abstract void zzclp() throws RemoteException;

    public void zzclr() {
        synchronized (this.zzako) {
            if (this.aOD == null) {
                return;
            }
            try {
                zzclp();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected T zzcls() {
        T t;
        Throwable e;
        synchronized (this.zzako) {
            if (this.aOD != null) {
                t = this.aOD;
            } else {
                try {
                    this.aOD = zzb(zztl.zza(this.mContext, zztl.Qp, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (com.google.android.gms.internal.zztl.zza e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.aOC) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.aOD;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.aOC) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.aOD;
                    return t;
                }
                if (!this.aOC && this.aOD == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.aOC = true;
                } else if (this.aOC && this.aOD != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.aOD;
            }
        }
        return t;
    }
}
