package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class aac {
    private static final AtomicReference<aac> zzbVi = new AtomicReference();

    private aac(Context context) {
    }

    @Nullable
    public static aac zzJZ() {
        return (aac) zzbVi.get();
    }

    public static Set<String> zzKa() {
        return Collections.emptySet();
    }

    public static aac zzbL(Context context) {
        zzbVi.compareAndSet(null, new aac(context));
        return (aac) zzbVi.get();
    }

    public static void zze(@NonNull FirebaseApp firebaseApp) {
    }

    public static FirebaseOptions zzhq(@NonNull String str) {
        return null;
    }
}
