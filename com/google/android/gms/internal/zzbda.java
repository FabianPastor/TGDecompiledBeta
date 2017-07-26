package com.google.android.gms.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class zzbda {
    private static final ExecutorService zzaEb = Executors.newFixedThreadPool(2, new zzbgw("GAC_Executor"));

    public static ExecutorService zzqj() {
        return zzaEb;
    }
}
