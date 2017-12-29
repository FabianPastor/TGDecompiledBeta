package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzbhb implements ThreadFactory {
    private final int mPriority;
    private final String zzgfb;
    private final AtomicInteger zzgfc;
    private final ThreadFactory zzgfd;

    public zzbhb(String str) {
        this(str, 0);
    }

    private zzbhb(String str, int i) {
        this.zzgfc = new AtomicInteger();
        this.zzgfd = Executors.defaultThreadFactory();
        this.zzgfb = (String) zzbq.checkNotNull(str, "Name must not be null");
        this.mPriority = 0;
    }

    public final Thread newThread(Runnable runnable) {
        Thread newThread = this.zzgfd.newThread(new zzbhc(runnable, 0));
        String str = this.zzgfb;
        newThread.setName(new StringBuilder(String.valueOf(str).length() + 13).append(str).append("[").append(this.zzgfc.getAndIncrement()).append("]").toString());
        return newThread;
    }
}
