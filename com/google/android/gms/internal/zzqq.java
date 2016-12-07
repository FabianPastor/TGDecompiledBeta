package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.internal.zzqc.zza;

public interface zzqq {
    void begin();

    void connect();

    boolean disconnect();

    void onConnected(Bundle bundle);

    void onConnectionSuspended(int i);

    void zza(ConnectionResult connectionResult, Api<?> api, int i);

    <A extends zzb, R extends Result, T extends zza<R, A>> T zzc(T t);

    <A extends zzb, T extends zza<? extends Result, A>> T zzd(T t);
}
