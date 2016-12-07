package com.google.android.gms.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzqs {
    private static final ExecutorService ys = Executors.newFixedThreadPool(2, new zzsf("GAC_Executor"));

    public static ExecutorService zzarz() {
        return ys;
    }
}
