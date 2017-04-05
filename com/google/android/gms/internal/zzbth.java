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
    private static final AtomicReference<zzbth> zzbWM = new AtomicReference();

    zzbth(Context context) {
    }

    @Nullable
    public static zzbth zzabY() {
        return (zzbth) zzbWM.get();
    }

    public static zzbth zzcw(Context context) {
        zzbWM.compareAndSet(null, new zzbth(context));
        return (zzbth) zzbWM.get();
    }

    public Set<String> zzabZ() {
        return Collections.emptySet();
    }

    public void zzg(@NonNull FirebaseApp firebaseApp) {
    }

    public FirebaseOptions zzjC(@NonNull String str) {
        return null;
    }
}
