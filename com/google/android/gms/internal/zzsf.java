package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class zzsf implements ThreadFactory {
    private final String Ff;
    private final AtomicInteger Fg;
    private final ThreadFactory Fh;
    private final int mPriority;

    public zzsf(String str) {
        this(str, 0);
    }

    public zzsf(String str, int i) {
        this.Fg = new AtomicInteger();
        this.Fh = Executors.defaultThreadFactory();
        this.Ff = (String) zzac.zzb((Object) str, (Object) "Name must not be null");
        this.mPriority = i;
    }

    public Thread newThread(Runnable runnable) {
        Thread newThread = this.Fh.newThread(new zzsg(runnable, this.mPriority));
        String str = this.Ff;
        newThread.setName(new StringBuilder(String.valueOf(str).length() + 13).append(str).append("[").append(this.Fg.getAndIncrement()).append("]").toString());
        return newThread;
    }
}
