package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Set;

public final class zzqn {
    private final ArrayMap<zzql<?>, ConnectionResult> xo = new ArrayMap();
    private final TaskCompletionSource<Void> yv = new TaskCompletionSource();
    private int yw;
    private boolean yx = false;

    public zzqn(Iterable<zzc<? extends ApiOptions>> iterable) {
        for (zzc apiKey : iterable) {
            this.xo.put(apiKey.getApiKey(), null);
        }
        this.yw = this.xo.keySet().size();
    }

    public Task<Void> getTask() {
        return this.yv.getTask();
    }

    public void zza(zzql<?> com_google_android_gms_internal_zzql_, ConnectionResult connectionResult) {
        this.xo.put(com_google_android_gms_internal_zzql_, connectionResult);
        this.yw--;
        if (!connectionResult.isSuccess()) {
            this.yx = true;
        }
        if (this.yw != 0) {
            return;
        }
        if (this.yx) {
            this.yv.setException(new zzb(this.xo));
            return;
        }
        this.yv.setResult(null);
    }

    public Set<zzql<?>> zzaro() {
        return this.xo.keySet();
    }

    public void zzarp() {
        this.yv.setResult(null);
    }
}
