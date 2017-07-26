package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import java.util.Collections;

public final class zzbco implements zzbcw {
    private final zzbcx zzaCZ;

    public zzbco(zzbcx com_google_android_gms_internal_zzbcx) {
        this.zzaCZ = com_google_android_gms_internal_zzbcx;
    }

    public final void begin() {
        for (zze disconnect : this.zzaCZ.zzaDF.values()) {
            disconnect.disconnect();
        }
        this.zzaCZ.zzaCl.zzaDG = Collections.emptySet();
    }

    public final void connect() {
        this.zzaCZ.zzqh();
    }

    public final boolean disconnect() {
        return true;
    }

    public final void onConnected(Bundle bundle) {
    }

    public final void onConnectionSuspended(int i) {
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
    }

    public final <A extends zzb, R extends Result, T extends zzbay<R, A>> T zzd(T t) {
        this.zzaCZ.zzaCl.zzaCJ.add(t);
        return t;
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
