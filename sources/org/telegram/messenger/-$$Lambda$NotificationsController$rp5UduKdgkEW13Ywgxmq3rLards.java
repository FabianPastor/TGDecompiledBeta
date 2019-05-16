package org.telegram.messenger;

import android.net.Uri;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$rp5UduKdgkEW13Ywgxmq3rLards implements Runnable {
    private final /* synthetic */ Uri f$0;

    public /* synthetic */ -$$Lambda$NotificationsController$rp5UduKdgkEW13Ywgxmq3rLards(Uri uri) {
        this.f$0 = uri;
    }

    public final void run() {
        ApplicationLoader.applicationContext.revokeUriPermission(this.f$0, 1);
    }
}
