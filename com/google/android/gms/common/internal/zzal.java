package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.common.api.Api.zzg;

public class zzal<T extends IInterface> extends zzl<T> {
    private final zzg<T> zzaGJ;

    protected String zzeA() {
        return this.zzaGJ.zzeA();
    }

    protected String zzez() {
        return this.zzaGJ.zzez();
    }

    protected T zzh(IBinder iBinder) {
        return this.zzaGJ.zzh(iBinder);
    }

    public zzg<T> zzyn() {
        return this.zzaGJ;
    }
}
