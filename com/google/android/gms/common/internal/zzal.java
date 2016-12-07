package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.common.api.Api.zzg;

public class zzal<T extends IInterface> extends zzl<T> {
    private final zzg<T> zzaFm;

    protected String zzeu() {
        return this.zzaFm.zzeu();
    }

    protected String zzev() {
        return this.zzaFm.zzev();
    }

    protected T zzh(IBinder iBinder) {
        return this.zzaFm.zzh(iBinder);
    }

    public zzg<T> zzxG() {
        return this.zzaFm;
    }
}
