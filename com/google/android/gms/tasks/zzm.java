package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

final class zzm implements Executor {
    zzm() {
    }

    public final void execute(Runnable runnable) {
        runnable.run();
    }
}
