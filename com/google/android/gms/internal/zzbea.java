package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbea {
    private final Set<zzbdw<?>> zzauB = Collections.newSetFromMap(new WeakHashMap());

    public static <L> zzbdy<L> zza(@NonNull L l, @NonNull String str) {
        zzbo.zzb((Object) l, (Object) "Listener must not be null");
        zzbo.zzb((Object) str, (Object) "Listener type must not be null");
        zzbo.zzh(str, "Listener type must not be empty");
        return new zzbdy(l, str);
    }

    public static <L> zzbdw<L> zzb(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzbo.zzb((Object) l, (Object) "Listener must not be null");
        zzbo.zzb((Object) looper, (Object) "Looper must not be null");
        zzbo.zzb((Object) str, (Object) "Listener type must not be null");
        return new zzbdw(looper, l, str);
    }

    public final void release() {
        for (zzbdw clear : this.zzauB) {
            clear.clear();
        }
        this.zzauB.clear();
    }

    public final <L> zzbdw<L> zza(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzbdw<L> zzb = zzb(l, looper, str);
        this.zzauB.add(zzb);
        return zzb;
    }
}
