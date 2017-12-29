package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzf extends zzb<Boolean> {
    private zzck<?> zzfnu;

    public zzf(zzck<?> com_google_android_gms_common_api_internal_zzck_, TaskCompletionSource<Boolean> taskCompletionSource) {
        super(4, taskCompletionSource);
        this.zzfnu = com_google_android_gms_common_api_internal_zzck_;
    }

    public final /* bridge */ /* synthetic */ void zza(zzae com_google_android_gms_common_api_internal_zzae, boolean z) {
    }

    public final void zzb(zzbo<?> com_google_android_gms_common_api_internal_zzbo_) throws RemoteException {
        zzcr com_google_android_gms_common_api_internal_zzcr = (zzcr) com_google_android_gms_common_api_internal_zzbo_.zzaiy().remove(this.zzfnu);
        if (com_google_android_gms_common_api_internal_zzcr != null) {
            com_google_android_gms_common_api_internal_zzcr.zzfnr.zzc(com_google_android_gms_common_api_internal_zzbo_.zzahp(), this.zzedx);
            com_google_android_gms_common_api_internal_zzcr.zzfnq.zzajp();
            return;
        }
        this.zzedx.trySetResult(Boolean.valueOf(false));
    }

    public final /* bridge */ /* synthetic */ void zzs(Status status) {
        super.zzs(status);
    }
}
