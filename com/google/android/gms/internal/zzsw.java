package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzaa;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class zzsw implements ThreadFactory {
    private final String GM;
    private final AtomicInteger GN;
    private final ThreadFactory GO;
    private final int mPriority;

    public zzsw(String str) {
        this(str, 0);
    }

    public zzsw(String str, int i) {
        this.GN = new AtomicInteger();
        this.GO = Executors.defaultThreadFactory();
        this.GM = (String) zzaa.zzb((Object) str, (Object) "Name must not be null");
        this.mPriority = i;
    }

    public Thread newThread(Runnable runnable) {
        Thread newThread = this.GO.newThread(new zzsx(runnable, this.mPriority));
        String str = this.GM;
        newThread.setName(new StringBuilder(String.valueOf(str).length() + 13).append(str).append("[").append(this.GN.getAndIncrement()).append("]").toString());
        return newThread;
    }
}
