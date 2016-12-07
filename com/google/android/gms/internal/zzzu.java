package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Set;

public final class zzzu {
    private final ArrayMap<zzzs<?>, ConnectionResult> zzaxy = new ArrayMap();
    private final TaskCompletionSource<Void> zzayC = new TaskCompletionSource();
    private int zzayD;
    private boolean zzayE = false;

    public zzzu(Iterable<zzc<? extends ApiOptions>> iterable) {
        for (zzc apiKey : iterable) {
            this.zzaxy.put(apiKey.getApiKey(), null);
        }
        this.zzayD = this.zzaxy.keySet().size();
    }

    public Task<Void> getTask() {
        return this.zzayC.getTask();
    }

    public void zza(zzzs<?> com_google_android_gms_internal_zzzs_, ConnectionResult connectionResult) {
        this.zzaxy.put(com_google_android_gms_internal_zzzs_, connectionResult);
        this.zzayD--;
        if (!connectionResult.isSuccess()) {
            this.zzayE = true;
        }
        if (this.zzayD != 0) {
            return;
        }
        if (this.zzayE) {
            this.zzayC.setException(new zzb(this.zzaxy));
            return;
        }
        this.zzayC.setResult(null);
    }

    public Set<zzzs<?>> zzuY() {
        return this.zzaxy.keySet();
    }

    public void zzuZ() {
        this.zzayC.setResult(null);
    }
}
