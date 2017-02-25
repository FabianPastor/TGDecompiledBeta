package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzbth {
    private static final AtomicReference<zzbth> zzbWQ = new AtomicReference();

    zzbth(Context context) {
    }

    @Nullable
    public static zzbth zzabX() {
        return (zzbth) zzbWQ.get();
    }

    public static zzbth zzcx(Context context) {
        zzbWQ.compareAndSet(null, new zzbth(context));
        return (zzbth) zzbWQ.get();
    }

    public Set<String> zzabY() {
        return Collections.emptySet();
    }

    public void zzg(@NonNull FirebaseApp firebaseApp) {
    }

    public FirebaseOptions zzjC(@NonNull String str) {
        return null;
    }
}
