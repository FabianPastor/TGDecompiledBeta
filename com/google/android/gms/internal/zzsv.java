package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public class zzsv implements Executor {
    private final Handler mHandler;

    public zzsv(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    public void execute(@NonNull Runnable runnable) {
        this.mHandler.post(runnable);
    }
}
