package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class zzre {
    private final Set<zzrd<?>> pA = Collections.newSetFromMap(new WeakHashMap());

    public void release() {
        for (zzrd clear : this.pA) {
            clear.clear();
        }
        this.pA.clear();
    }

    public <L> zzrd<L> zza(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzac.zzb((Object) l, (Object) "Listener must not be null");
        zzac.zzb((Object) looper, (Object) "Looper must not be null");
        zzac.zzb((Object) str, (Object) "Listener type must not be null");
        zzrd<L> com_google_android_gms_internal_zzrd = new zzrd(looper, l, str);
        this.pA.add(com_google_android_gms_internal_zzrd);
        return com_google_android_gms_internal_zzrd;
    }

    public <L> zzrd<L> zzb(@NonNull L l, Looper looper) {
        return zza(l, looper, "NO_TYPE");
    }
}
