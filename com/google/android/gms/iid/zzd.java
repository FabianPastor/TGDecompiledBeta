package com.google.android.gms.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Intent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class zzd {
    final Intent intent;
    private final PendingResult zzieu;
    private boolean zziev = false;
    private final ScheduledFuture<?> zziew;

    zzd(Intent intent, PendingResult pendingResult, ScheduledExecutorService scheduledExecutorService) {
        this.intent = intent;
        this.zzieu = pendingResult;
        this.zziew = scheduledExecutorService.schedule(new zze(this, intent), 9500, TimeUnit.MILLISECONDS);
    }

    final synchronized void finish() {
        if (!this.zziev) {
            this.zzieu.finish();
            this.zziew.cancel(false);
            this.zziev = true;
        }
    }
}
