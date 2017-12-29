package com.google.android.gms.common.api.internal;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzcm {
    private final Set<zzci<?>> zzfab = Collections.newSetFromMap(new WeakHashMap());

    public final void release() {
        for (zzci clear : this.zzfab) {
            clear.clear();
        }
        this.zzfab.clear();
    }
}
