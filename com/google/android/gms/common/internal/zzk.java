package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public final class zzk extends zzax {
    private zzd zzfzc;
    private final int zzfzd;

    public zzk(zzd com_google_android_gms_common_internal_zzd, int i) {
        this.zzfzc = com_google_android_gms_common_internal_zzd;
        this.zzfzd = i;
    }

    public final void zza(int i, Bundle bundle) {
        Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
    }

    public final void zza(int i, IBinder iBinder, Bundle bundle) {
        zzbq.checkNotNull(this.zzfzc, "onPostInitComplete can be called only once per call to getRemoteService");
        this.zzfzc.zza(i, iBinder, bundle, this.zzfzd);
        this.zzfzc = null;
    }
}
