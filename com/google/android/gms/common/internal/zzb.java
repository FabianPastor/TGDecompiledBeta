package com.google.android.gms.common.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.common.api.zze;

public class zzb {
    @NonNull
    public static zza zzae(@NonNull Status status) {
        return status.hasResolution() ? new zze(status) : new zza(status);
    }

    @NonNull
    public static zza zzl(@NonNull ConnectionResult connectionResult) {
        return zzae(new Status(connectionResult.getErrorCode(), connectionResult.getErrorMessage(), connectionResult.getResolution()));
    }
}
