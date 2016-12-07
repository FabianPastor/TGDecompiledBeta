package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzzv.zza;
import java.util.Collections;

public class zzaak implements zzaam {
    private final zzaan zzazK;

    public zzaak(zzaan com_google_android_gms_internal_zzaan) {
        this.zzazK = com_google_android_gms_internal_zzaan;
    }

    public void begin() {
        this.zzazK.zzvQ();
        this.zzazK.zzazd.zzaAs = Collections.emptySet();
    }

    public void connect() {
        this.zzazK.zzvO();
    }

    public boolean disconnect() {
        return true;
    }

    public void onConnected(Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(T t) {
        this.zzazK.zzazd.zzaAl.add(t);
        return t;
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
