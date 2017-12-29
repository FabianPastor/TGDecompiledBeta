package com.google.android.gms.common.api.internal;

import com.google.android.gms.internal.zzbhb;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class zzbl {
    private static final ExecutorService zzfsx = Executors.newFixedThreadPool(2, new zzbhb("GAC_Executor"));

    public static ExecutorService zzaip() {
        return zzfsx;
    }
}
