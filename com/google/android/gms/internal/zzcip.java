package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzcip implements Runnable {
    private /* synthetic */ boolean zzbtw;
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ AtomicReference zzbub;

    zzcip(zzcid com_google_android_gms_internal_zzcid, AtomicReference atomicReference, boolean z) {
        this.zzbua = com_google_android_gms_internal_zzcid;
        this.zzbub = atomicReference;
        this.zzbtw = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        synchronized (this.zzbub) {
            try {
                zzcfd zzd = this.zzbua.zzbtU;
                if (zzd == null) {
                    this.zzbua.zzwF().zzyx().log("Failed to get user properties");
                } else {
                    this.zzbub.set(zzd.zza(this.zzbua.zzwu().zzdV(null), this.zzbtw));
                    this.zzbua.zzkP();
                    this.zzbub.notify();
                }
            } catch (RemoteException e) {
                this.zzbua.zzwF().zzyx().zzj("Failed to get user properties", e);
            } finally {
                this.zzbub.notify();
            }
        }
    }
}
