package com.google.android.gms.internal;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbap extends zzban {
    private zzbee<zzb, ?> zzaBu;
    private zzbey<zzb, ?> zzaBv;

    public zzbap(zzbef com_google_android_gms_internal_zzbef, TaskCompletionSource<Void> taskCompletionSource) {
        super(3, taskCompletionSource);
        this.zzaBu = com_google_android_gms_internal_zzbef.zzaBu;
        this.zzaBv = com_google_android_gms_internal_zzbef.zzaBv;
    }

    public final /* bridge */ /* synthetic */ void zza(@NonNull zzbbt com_google_android_gms_internal_zzbbt, boolean z) {
    }

    public final void zzb(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws RemoteException {
        this.zzaBu.zzb(com_google_android_gms_internal_zzbdd_.zzpJ(), this.zzalE);
        if (this.zzaBu.zzqG() != null) {
            com_google_android_gms_internal_zzbdd_.zzqs().put(this.zzaBu.zzqG(), new zzbef(this.zzaBu, this.zzaBv));
        }
    }

    public final /* bridge */ /* synthetic */ void zzp(@NonNull Status status) {
        super.zzp(status);
    }
}
