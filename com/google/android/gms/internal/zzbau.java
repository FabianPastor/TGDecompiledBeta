package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Set;

public final class zzbau {
    private final ArrayMap<zzbas<?>, ConnectionResult> zzaAB = new ArrayMap();
    private final TaskCompletionSource<Void> zzaBG = new TaskCompletionSource();
    private int zzaBH;
    private boolean zzaBI = false;

    public zzbau(Iterable<? extends GoogleApi<?>> iterable) {
        for (GoogleApi zzph : iterable) {
            this.zzaAB.put(zzph.zzph(), null);
        }
        this.zzaBH = this.zzaAB.keySet().size();
    }

    public final Task<Void> getTask() {
        return this.zzaBG.getTask();
    }

    public final void zza(zzbas<?> com_google_android_gms_internal_zzbas_, ConnectionResult connectionResult) {
        this.zzaAB.put(com_google_android_gms_internal_zzbas_, connectionResult);
        this.zzaBH--;
        if (!connectionResult.isSuccess()) {
            this.zzaBI = true;
        }
        if (this.zzaBH != 0) {
            return;
        }
        if (this.zzaBI) {
            this.zzaBG.setException(new zza(this.zzaAB));
            return;
        }
        this.zzaBG.setResult(null);
    }

    public final Set<zzbas<?>> zzpt() {
        return this.zzaAB.keySet();
    }

    public final void zzpu() {
        this.zzaBG.setResult(null);
    }
}
