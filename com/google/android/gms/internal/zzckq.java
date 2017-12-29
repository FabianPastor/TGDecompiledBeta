package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

final class zzckq implements Runnable {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ String zzjgr;
    private /* synthetic */ zzckg zzjij;
    private /* synthetic */ AtomicReference zzjik;

    zzckq(zzckg com_google_android_gms_internal_zzckg, AtomicReference atomicReference, String str, String str2, String str3, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjij = com_google_android_gms_internal_zzckg;
        this.zzjik = atomicReference;
        this.zzimf = str;
        this.zzjgq = str2;
        this.zzjgr = str3;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        synchronized (this.zzjik) {
            try {
                zzche zzd = this.zzjij.zzjid;
                if (zzd == null) {
                    this.zzjij.zzawy().zzazd().zzd("Failed to get conditional properties", zzchm.zzjk(this.zzimf), this.zzjgq, this.zzjgr);
                    this.zzjik.set(Collections.emptyList());
                } else {
                    if (TextUtils.isEmpty(this.zzimf)) {
                        this.zzjik.set(zzd.zza(this.zzjgq, this.zzjgr, this.zzjgn));
                    } else {
                        this.zzjik.set(zzd.zzj(this.zzimf, this.zzjgq, this.zzjgr));
                    }
                    this.zzjij.zzxr();
                    this.zzjik.notify();
                }
            } catch (RemoteException e) {
                this.zzjij.zzawy().zzazd().zzd("Failed to get conditional properties", zzchm.zzjk(this.zzimf), this.zzjgq, e);
                this.zzjik.set(Collections.emptyList());
            } finally {
                this.zzjik.notify();
            }
        }
    }
}
