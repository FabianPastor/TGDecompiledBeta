package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzanr {
    private static final AtomicReference<zzanr> aWf = new AtomicReference();

    zzanr(Context context) {
    }

    @Nullable
    public static zzanr P() {
        return (zzanr) aWf.get();
    }

    public static zzanr zzeu(Context context) {
        aWf.compareAndSet(null, new zzanr(context));
        return (zzanr) aWf.get();
    }

    public Set<String> Q() {
        return Collections.emptySet();
    }

    public void zzg(@NonNull FirebaseApp firebaseApp) {
    }

    public FirebaseOptions zztz(@NonNull String str) {
        return null;
    }
}
