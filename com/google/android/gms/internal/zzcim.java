package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

final class zzcim implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;
    private /* synthetic */ boolean zzbtw;
    private /* synthetic */ zzcic zzbua;
    private /* synthetic */ AtomicReference zzbub;

    zzcim(zzcic com_google_android_gms_internal_zzcic, AtomicReference atomicReference, String str, String str2, String str3, boolean z) {
        this.zzbua = com_google_android_gms_internal_zzcic;
        this.zzbub = atomicReference;
        this.zzbjh = str;
        this.zzbth = str2;
        this.zzbti = str3;
        this.zzbtw = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        synchronized (this.zzbub) {
            try {
                zzcfc zzd = this.zzbua.zzbtU;
                if (zzd == null) {
                    this.zzbua.zzwF().zzyx().zzd("Failed to get user properties", zzcfk.zzdZ(this.zzbjh), this.zzbth, this.zzbti);
                    this.zzbub.set(Collections.emptyList());
                } else {
                    if (TextUtils.isEmpty(this.zzbjh)) {
                        this.zzbub.set(zzd.zza(this.zzbth, this.zzbti, this.zzbtw, this.zzbua.zzwu().zzdV(this.zzbua.zzwF().zzyE())));
                    } else {
                        this.zzbub.set(zzd.zza(this.zzbjh, this.zzbth, this.zzbti, this.zzbtw));
                    }
                    this.zzbua.zzkP();
                    this.zzbub.notify();
                }
            } catch (RemoteException e) {
                this.zzbua.zzwF().zzyx().zzd("Failed to get user properties", zzcfk.zzdZ(this.zzbjh), this.zzbth, e);
                this.zzbub.set(Collections.emptyList());
            } finally {
                this.zzbub.notify();
            }
        }
    }
}
