package com.google.android.gms.internal;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbar extends zzban {
    private zzbdy<?> zzaBy;

    public zzbar(zzbdy<?> com_google_android_gms_internal_zzbdy_, TaskCompletionSource<Void> taskCompletionSource) {
        super(4, taskCompletionSource);
        this.zzaBy = com_google_android_gms_internal_zzbdy_;
    }

    public final /* bridge */ /* synthetic */ void zza(@NonNull zzbbt com_google_android_gms_internal_zzbbt, boolean z) {
    }

    public final void zzb(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws RemoteException {
        zzbef com_google_android_gms_internal_zzbef = (zzbef) com_google_android_gms_internal_zzbdd_.zzqs().remove(this.zzaBy);
        if (com_google_android_gms_internal_zzbef != null) {
            com_google_android_gms_internal_zzbef.zzaBv.zzc(com_google_android_gms_internal_zzbdd_.zzpJ(), this.zzalE);
            com_google_android_gms_internal_zzbef.zzaBu.zzqH();
            return;
        }
        Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
        this.zzalE.trySetException(new ApiException(Status.zzaBo));
    }

    public final /* bridge */ /* synthetic */ void zzp(@NonNull Status status) {
        super.zzp(status);
    }
}
