package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbdz {
    private final Set<zzbdv<?>> zzauB = Collections.newSetFromMap(new WeakHashMap());

    public static <L> zzbdx<L> zza(@NonNull L l, @NonNull String str) {
        zzbo.zzb((Object) l, (Object) "Listener must not be null");
        zzbo.zzb((Object) str, (Object) "Listener type must not be null");
        zzbo.zzh(str, "Listener type must not be empty");
        return new zzbdx(l, str);
    }

    public static <L> zzbdv<L> zzb(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzbo.zzb((Object) l, (Object) "Listener must not be null");
        zzbo.zzb((Object) looper, (Object) "Looper must not be null");
        zzbo.zzb((Object) str, (Object) "Listener type must not be null");
        return new zzbdv(looper, l, str);
    }

    public final void release() {
        for (zzbdv clear : this.zzauB) {
            clear.clear();
        }
        this.zzauB.clear();
    }

    public final <L> zzbdv<L> zza(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzbdv<L> zzb = zzb(l, looper, str);
        this.zzauB.add(zzb);
        return zzb;
    }
}
