package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Set;

public final class zzaab {
    private final ArrayMap<zzzz<?>, ConnectionResult> zzayL = new ArrayMap();
    private final TaskCompletionSource<Void> zzazS = new TaskCompletionSource();
    private int zzazT;
    private boolean zzazU = false;

    public zzaab(Iterable<? extends zzc<?>> iterable) {
        for (zzc apiKey : iterable) {
            this.zzayL.put(apiKey.getApiKey(), null);
        }
        this.zzazT = this.zzayL.keySet().size();
    }

    public Task<Void> getTask() {
        return this.zzazS.getTask();
    }

    public void zza(zzzz<?> com_google_android_gms_internal_zzzz_, ConnectionResult connectionResult) {
        this.zzayL.put(com_google_android_gms_internal_zzzz_, connectionResult);
        this.zzazT--;
        if (!connectionResult.isSuccess()) {
            this.zzazU = true;
        }
        if (this.zzazT != 0) {
            return;
        }
        if (this.zzazU) {
            this.zzazS.setException(new zzb(this.zzayL));
            return;
        }
        this.zzazS.setResult(null);
    }

    public void zzvA() {
        this.zzazS.setResult(null);
    }

    public Set<zzzz<?>> zzvz() {
        return this.zzayL.keySet();
    }
}
