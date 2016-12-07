package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import java.util.Set;

public final class zzqb extends zzqe<zzc> {
    private int wv;
    private boolean ww;

    private void zza(ConnectionResult connectionResult) {
        ArrayMap arrayMap = null;
        for (int i = 0; i < arrayMap.size(); i++) {
            zza((zzpz) arrayMap.keyAt(i), connectionResult);
        }
    }

    public void zza(zzpz<?> com_google_android_gms_internal_zzpz_, ConnectionResult connectionResult) {
        synchronized (null) {
            ArrayMap arrayMap = null;
            try {
                arrayMap.put(com_google_android_gms_internal_zzpz_, connectionResult);
                this.wv--;
                boolean isSuccess = connectionResult.isSuccess();
                if (!isSuccess) {
                    this.ww = isSuccess;
                }
                if (this.wv == 0) {
                    Status status = this.ww ? new Status(13) : Status.vY;
                    arrayMap = null;
                    zzc(arrayMap.size() == 1 ? new zzb(status, null) : new zzc(status, null));
                }
            } finally {
            }
        }
    }

    public Set<zzpz<?>> zzaqm() {
        ArrayMap arrayMap = null;
        return arrayMap.keySet();
    }

    protected /* synthetic */ Result zzc(Status status) {
        return zzy(status);
    }

    protected zzc zzy(Status status) {
        zzc com_google_android_gms_common_api_zzc;
        synchronized (null) {
            try {
                zza(new ConnectionResult(8));
                ArrayMap arrayMap = null;
                if (arrayMap.size() != 1) {
                    com_google_android_gms_common_api_zzc = new zzc(status, null);
                }
            } finally {
            }
        }
        return com_google_android_gms_common_api_zzc;
    }
}
