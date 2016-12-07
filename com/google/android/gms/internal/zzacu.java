package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class zzacu implements ThreadFactory {
    private final int mPriority;
    private final String zzaHh;
    private final AtomicInteger zzaHi;
    private final ThreadFactory zzaHj;

    public zzacu(String str) {
        this(str, 0);
    }

    public zzacu(String str, int i) {
        this.zzaHi = new AtomicInteger();
        this.zzaHj = Executors.defaultThreadFactory();
        this.zzaHh = (String) zzac.zzb((Object) str, (Object) "Name must not be null");
        this.mPriority = i;
    }

    public Thread newThread(Runnable runnable) {
        Thread newThread = this.zzaHj.newThread(new zzacv(runnable, this.mPriority));
        String str = this.zzaHh;
        newThread.setName(new StringBuilder(String.valueOf(str).length() + 13).append(str).append("[").append(this.zzaHi.getAndIncrement()).append("]").toString());
        return newThread;
    }
}
