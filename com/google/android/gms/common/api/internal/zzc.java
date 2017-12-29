package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzc<A extends zzm<? extends Result, zzb>> extends zza {
    private A zzfnp;

    public zzc(int i, A a) {
        super(i);
        this.zzfnp = a;
    }

    public final void zza(zzae com_google_android_gms_common_api_internal_zzae, boolean z) {
        com_google_android_gms_common_api_internal_zzae.zza(this.zzfnp, z);
    }

    public final void zza(zzbo<?> com_google_android_gms_common_api_internal_zzbo_) throws DeadObjectException {
        this.zzfnp.zzb(com_google_android_gms_common_api_internal_zzbo_.zzahp());
    }

    public final void zzs(Status status) {
        this.zzfnp.zzu(status);
    }
}
