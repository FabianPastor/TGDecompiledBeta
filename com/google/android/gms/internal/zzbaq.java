package com.google.android.gms.internal;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbaq extends zzbam {
    private zzbdx<?> zzaBy;

    public zzbaq(zzbdx<?> com_google_android_gms_internal_zzbdx_, TaskCompletionSource<Void> taskCompletionSource) {
        super(4, taskCompletionSource);
        this.zzaBy = com_google_android_gms_internal_zzbdx_;
    }

    public final /* bridge */ /* synthetic */ void zza(@NonNull zzbbs com_google_android_gms_internal_zzbbs, boolean z) {
    }

    public final void zzb(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws RemoteException {
        zzbee com_google_android_gms_internal_zzbee = (zzbee) com_google_android_gms_internal_zzbdc_.zzqs().remove(this.zzaBy);
        if (com_google_android_gms_internal_zzbee != null) {
            com_google_android_gms_internal_zzbee.zzaBv.zzc(com_google_android_gms_internal_zzbdc_.zzpJ(), this.zzalE);
            com_google_android_gms_internal_zzbee.zzaBu.zzqH();
            return;
        }
        Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
        this.zzalE.trySetException(new ApiException(Status.zzaBo));
    }

    public final /* bridge */ /* synthetic */ void zzp(@NonNull Status status) {
        super.zzp(status);
    }
}
