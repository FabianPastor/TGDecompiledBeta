package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzbqk {
    private static final AtomicReference<zzbqk> zzbUK = new AtomicReference();

    zzbqk(Context context) {
    }

    @Nullable
    public static zzbqk zzaap() {
        return (zzbqk) zzbUK.get();
    }

    public static zzbqk zzbZ(Context context) {
        zzbUK.compareAndSet(null, new zzbqk(context));
        return (zzbqk) zzbUK.get();
    }

    public Set<String> zzaaq() {
        return Collections.emptySet();
    }

    public void zzg(@NonNull FirebaseApp firebaseApp) {
    }

    public FirebaseOptions zzjD(@NonNull String str) {
        return null;
    }
}
