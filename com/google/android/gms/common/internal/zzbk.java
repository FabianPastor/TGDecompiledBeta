package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.Result;

final class zzbk implements zzbm<R, T> {
    private /* synthetic */ Response zzaIn;

    zzbk(Response response) {
        this.zzaIn = response;
    }

    public final /* synthetic */ Object zzd(Result result) {
        this.zzaIn.setResult(result);
        return this.zzaIn;
    }
}
