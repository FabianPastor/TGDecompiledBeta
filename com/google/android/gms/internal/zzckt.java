package com.google.android.gms.internal;

import android.os.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

final class zzckt implements Runnable {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ boolean zzjhf;
    private /* synthetic */ zzckg zzjij;
    private /* synthetic */ AtomicReference zzjik;

    zzckt(zzckg com_google_android_gms_internal_zzckg, AtomicReference atomicReference, zzcgi com_google_android_gms_internal_zzcgi, boolean z) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjik = atomicReference;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
        this.zzjhf = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        synchronized (this.zzjik) {
            try {
                zzche zzd = this.zzjij.zzjid;
                if (zzd == null) {
                    this.zzjij.zzawy().zzazd().log("Failed to get user properties");
                } else {
                    this.zzjik.set(zzd.zza(this.zzjgn, this.zzjhf));
                    this.zzjij.zzxr();
                    this.zzjik.notify();
                }
            } catch (RemoteException e) {
                this.zzjij.zzawy().zzazd().zzj("Failed to get user properties", e);
            } finally {
                this.zzjik.notify();
            }
        }
    }
}
