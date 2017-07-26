package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbgw implements ThreadFactory {
    private final int mPriority;
    private final String zzaKe;
    private final AtomicInteger zzaKf;
    private final ThreadFactory zzaKg;

    public zzbgw(String str) {
        this(str, 0);
    }

    private zzbgw(String str, int i) {
        this.zzaKf = new AtomicInteger();
        this.zzaKg = Executors.defaultThreadFactory();
        this.zzaKe = (String) zzbo.zzb((Object) str, (Object) "Name must not be null");
        this.mPriority = 0;
    }

    public final Thread newThread(Runnable runnable) {
        Thread newThread = this.zzaKg.newThread(new zzbgx(runnable, 0));
        String str = this.zzaKe;
        newThread.setName(new StringBuilder(String.valueOf(str).length() + 13).append(str).append("[").append(this.zzaKf.getAndIncrement()).append("]").toString());
        return newThread;
    }
}
