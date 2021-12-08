package org.telegram.messenger;

import android.content.Intent;

public final /* synthetic */ class NotificationBadge$AsusHomeBadger$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Intent f$0;

    public /* synthetic */ NotificationBadge$AsusHomeBadger$$ExternalSyntheticLambda0(Intent intent) {
        this.f$0 = intent;
    }

    public final void run() {
        ApplicationLoader.applicationContext.sendBroadcast(this.f$0);
    }
}
