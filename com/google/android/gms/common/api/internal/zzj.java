package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.AvailabilityException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;
import java.util.Set;

public final class zzj {
    private final ArrayMap<zzh<?>, ConnectionResult> zzflw = new ArrayMap();
    private final ArrayMap<zzh<?>, String> zzfoc = new ArrayMap();
    private final TaskCompletionSource<Map<zzh<?>, String>> zzfod = new TaskCompletionSource();
    private int zzfoe;
    private boolean zzfof = false;

    public zzj(Iterable<? extends GoogleApi<?>> iterable) {
        for (GoogleApi zzagn : iterable) {
            this.zzflw.put(zzagn.zzagn(), null);
        }
        this.zzfoe = this.zzflw.keySet().size();
    }

    public final Task<Map<zzh<?>, String>> getTask() {
        return this.zzfod.getTask();
    }

    public final void zza(zzh<?> com_google_android_gms_common_api_internal_zzh_, ConnectionResult connectionResult, String str) {
        this.zzflw.put(com_google_android_gms_common_api_internal_zzh_, connectionResult);
        this.zzfoc.put(com_google_android_gms_common_api_internal_zzh_, str);
        this.zzfoe--;
        if (!connectionResult.isSuccess()) {
            this.zzfof = true;
        }
        if (this.zzfoe != 0) {
            return;
        }
        if (this.zzfof) {
            this.zzfod.setException(new AvailabilityException(this.zzflw));
            return;
        }
        this.zzfod.setResult(this.zzfoc);
    }

    public final Set<zzh<?>> zzaha() {
        return this.zzflw.keySet();
    }
}
