package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.dynamite.DynamiteModule.zza;

public abstract class zzbhc<T> {
    private final Context mContext;
    private final String mTag;
    private boolean zzbNo = false;
    private T zzbNp;
    private final Object zzrN = new Object();

    public zzbhc(Context context, String str) {
        this.mContext = context;
        this.mTag = str;
    }

    public boolean isOperational() {
        return zzSq() != null;
    }

    protected abstract void zzSn() throws RemoteException;

    public void zzSp() {
        synchronized (this.zzrN) {
            if (this.zzbNp == null) {
                return;
            }
            try {
                zzSn();
            } catch (Throwable e) {
                Log.e(this.mTag, "Could not finalize native handle", e);
            }
        }
    }

    protected T zzSq() {
        T t;
        Throwable e;
        synchronized (this.zzrN) {
            if (this.zzbNp != null) {
                t = this.zzbNp;
            } else {
                try {
                    this.zzbNp = zzb(DynamiteModule.zza(this.mContext, DynamiteModule.zzaQz, "com.google.android.gms.vision.dynamite"), this.mContext);
                } catch (zza e2) {
                    e = e2;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!!this.zzbNo) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbNp;
                    return t;
                } catch (RemoteException e3) {
                    e = e3;
                    Log.e(this.mTag, "Error creating remote native handle", e);
                    if (!this.zzbNo) {
                    }
                    Log.w(this.mTag, "Native handle is now available.");
                    t = this.zzbNp;
                    return t;
                }
                if (!this.zzbNo && this.zzbNp == null) {
                    Log.w(this.mTag, "Native handle not yet available. Reverting to no-op handle.");
                    this.zzbNo = true;
                } else if (this.zzbNo && this.zzbNp != null) {
                    Log.w(this.mTag, "Native handle is now available.");
                }
                t = this.zzbNp;
            }
        }
        return t;
    }

    protected abstract T zzb(DynamiteModule dynamiteModule, Context context) throws RemoteException, zza;
}
