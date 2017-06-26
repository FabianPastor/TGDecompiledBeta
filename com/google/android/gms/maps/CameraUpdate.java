package com.google.android.gms.maps;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class CameraUpdate {
    private final IObjectWrapper zzblv;

    CameraUpdate(IObjectWrapper iObjectWrapper) {
        this.zzblv = (IObjectWrapper) zzbo.zzu(iObjectWrapper);
    }

    public final IObjectWrapper zzwe() {
        return this.zzblv;
    }
}
