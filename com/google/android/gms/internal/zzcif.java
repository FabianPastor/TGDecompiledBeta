package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzcif implements Runnable {
    private /* synthetic */ zzcid zzbua;
    private /* synthetic */ AtomicReference zzbub;

    zzcif(zzcid com_google_android_gms_internal_zzcid, AtomicReference atomicReference) {
        this.zzbua = com_google_android_gms_internal_zzcid;
        this.zzbub = atomicReference;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        synchronized (this.zzbub) {
            try {
                zzcfd zzd = this.zzbua.zzbtU;
                if (zzd == null) {
                    this.zzbua.zzwF().zzyx().log("Failed to get app instance id");
                } else {
                    this.zzbub.set(zzd.zzc(this.zzbua.zzwu().zzdV(null)));
                    String str = (String) this.zzbub.get();
                    if (str != null) {
                        this.zzbua.zzwt().zzee(str);
                        this.zzbua.zzwG().zzbrq.zzef(str);
                    }
                    this.zzbua.zzkP();
                    this.zzbub.notify();
                }
            } catch (RemoteException e) {
                this.zzbua.zzwF().zzyx().zzj("Failed to get app instance id", e);
            } finally {
                this.zzbub.notify();
            }
        }
    }
}
