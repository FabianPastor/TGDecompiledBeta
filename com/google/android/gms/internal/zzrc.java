package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzqo.zza;
import java.util.Collections;

public class zzrc implements zzre {
    private final zzrf zA;

    public zzrc(zzrf com_google_android_gms_internal_zzrf) {
        this.zA = com_google_android_gms_internal_zzrf;
    }

    public void begin() {
        this.zA.zzate();
        this.zA.yW.Ak = Collections.emptySet();
    }

    public void connect() {
        this.zA.zzatc();
    }

    public boolean disconnect() {
        return true;
    }

    public void onConnected(Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(T t) {
        this.zA.yW.Ad.add(t);
        return t;
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
