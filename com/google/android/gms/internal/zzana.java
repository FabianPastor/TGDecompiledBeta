package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzana {
    private static final AtomicReference<zzana> aST = new AtomicReference();

    zzana(Context context) {
    }

    @Nullable
    public static zzana N() {
        return (zzana) aST.get();
    }

    public static zzana zzew(Context context) {
        aST.compareAndSet(null, new zzana(context));
        return (zzana) aST.get();
    }

    public Set<String> O() {
        return Collections.emptySet();
    }

    public void zzg(@NonNull FirebaseApp firebaseApp) {
    }

    public FirebaseOptions zzua(@NonNull String str) {
        return null;
    }
}
