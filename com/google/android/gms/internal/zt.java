package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class zt {
    private static final AtomicReference<zt> zzbVg = new AtomicReference();

    private zt(Context context) {
    }

    @Nullable
    public static zt zzJW() {
        return (zt) zzbVg.get();
    }

    public static Set<String> zzJX() {
        return Collections.emptySet();
    }

    public static zt zzbL(Context context) {
        zzbVg.compareAndSet(null, new zt(context));
        return (zt) zzbVg.get();
    }

    public static void zze(@NonNull FirebaseApp firebaseApp) {
    }

    public static FirebaseOptions zzhq(@NonNull String str) {
        return null;
    }
}
