package com.google.android.gms.internal;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbao extends zzbam {
    private zzbed<zzb, ?> zzaBu;
    private zzbex<zzb, ?> zzaBv;

    public zzbao(zzbee com_google_android_gms_internal_zzbee, TaskCompletionSource<Void> taskCompletionSource) {
        super(3, taskCompletionSource);
        this.zzaBu = com_google_android_gms_internal_zzbee.zzaBu;
        this.zzaBv = com_google_android_gms_internal_zzbee.zzaBv;
    }

    public final /* bridge */ /* synthetic */ void zza(@NonNull zzbbs com_google_android_gms_internal_zzbbs, boolean z) {
    }

    public final void zzb(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws RemoteException {
        this.zzaBu.zzb(com_google_android_gms_internal_zzbdc_.zzpJ(), this.zzalE);
        if (this.zzaBu.zzqG() != null) {
            com_google_android_gms_internal_zzbdc_.zzqs().put(this.zzaBu.zzqG(), new zzbee(this.zzaBu, this.zzaBv));
        }
    }

    public final /* bridge */ /* synthetic */ void zzp(@NonNull Status status) {
        super.zzp(status);
    }
}
