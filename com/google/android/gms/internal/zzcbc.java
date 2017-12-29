package com.google.android.gms.internal;

import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.util.concurrent.Callable;

public final class zzcbc {
    public static <T> T zzb(Callable<T> callable) throws Exception {
        ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            StrictMode.setThreadPolicy(ThreadPolicy.LAX);
            T call = callable.call();
            return call;
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }
}
