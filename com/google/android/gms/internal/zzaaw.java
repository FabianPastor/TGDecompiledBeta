package com.google.android.gms.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzaaw {
    private static final ExecutorService zzaCm = Executors.newFixedThreadPool(2, new zzadc("GAC_Executor"));

    public static ExecutorService zzwv() {
        return zzaCm;
    }
}
