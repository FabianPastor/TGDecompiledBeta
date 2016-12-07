package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzqc.zza;
import java.util.Collections;

public class zzqo implements zzqq {
    private final zzqr xk;

    public zzqo(zzqr com_google_android_gms_internal_zzqr) {
        this.xk = com_google_android_gms_internal_zzqr;
    }

    public void begin() {
        this.xk.zzary();
        this.xk.wV.xX = Collections.emptySet();
    }

    public void connect() {
        this.xk.zzarw();
    }

    public boolean disconnect() {
        return true;
    }

    public void onConnected(Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zzc(T t) {
        this.xk.wV.xQ.add(t);
        return t;
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzd(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
