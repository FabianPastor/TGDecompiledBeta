package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzaaz.zzb;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class zzaba {
    private final Set<zzaaz<?>> zzarH = Collections.newSetFromMap(new WeakHashMap());

    public static <L> zzb<L> zza(@NonNull L l, @NonNull String str) {
        zzac.zzb((Object) l, (Object) "Listener must not be null");
        zzac.zzb((Object) str, (Object) "Listener type must not be null");
        zzac.zzh(str, "Listener type must not be empty");
        return new zzb(l, str);
    }

    public static <L> zzaaz<L> zzb(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzac.zzb((Object) l, (Object) "Listener must not be null");
        zzac.zzb((Object) looper, (Object) "Looper must not be null");
        zzac.zzb((Object) str, (Object) "Listener type must not be null");
        return new zzaaz(looper, l, str);
    }

    public void release() {
        for (zzaaz clear : this.zzarH) {
            clear.clear();
        }
        this.zzarH.clear();
    }

    public <L> zzaaz<L> zza(@NonNull L l, @NonNull Looper looper, @NonNull String str) {
        zzaaz<L> zzb = zzb(l, looper, str);
        this.zzarH.add(zzb);
        return zzb;
    }

    public <L> zzaaz<L> zzb(@NonNull L l, Looper looper) {
        return zza(l, looper, "NO_TYPE");
    }
}
