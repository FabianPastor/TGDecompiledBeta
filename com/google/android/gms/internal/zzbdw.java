package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;

public final class zzbdw<L> {
    private volatile L mListener;
    private final zzbdx zzaEM;
    private final zzbdy<L> zzaEN;

    zzbdw(@NonNull Looper looper, @NonNull L l, @NonNull String str) {
        this.zzaEM = new zzbdx(this, looper);
        this.mListener = zzbo.zzb((Object) l, (Object) "Listener must not be null");
        this.zzaEN = new zzbdy(l, zzbo.zzcF(str));
    }

    public final void clear() {
        this.mListener = null;
    }

    public final void zza(zzbdz<? super L> com_google_android_gms_internal_zzbdz__super_L) {
        zzbo.zzb((Object) com_google_android_gms_internal_zzbdz__super_L, (Object) "Notifier must not be null");
        this.zzaEM.sendMessage(this.zzaEM.obtainMessage(1, com_google_android_gms_internal_zzbdz__super_L));
    }

    final void zzb(zzbdz<? super L> com_google_android_gms_internal_zzbdz__super_L) {
        Object obj = this.mListener;
        if (obj == null) {
            com_google_android_gms_internal_zzbdz__super_L.zzpT();
            return;
        }
        try {
            com_google_android_gms_internal_zzbdz__super_L.zzq(obj);
        } catch (RuntimeException e) {
            com_google_android_gms_internal_zzbdz__super_L.zzpT();
            throw e;
        }
    }

    public final boolean zzoc() {
        return this.mListener != null;
    }

    @NonNull
    public final zzbdy<L> zzqG() {
        return this.zzaEN;
    }
}
