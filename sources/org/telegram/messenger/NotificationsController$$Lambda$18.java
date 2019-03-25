package org.telegram.messenger;

import android.net.Uri;

final /* synthetic */ class NotificationsController$$Lambda$18 implements Runnable {
    private final Uri arg$1;

    NotificationsController$$Lambda$18(Uri uri) {
        this.arg$1 = uri;
    }

    public void run() {
        ApplicationLoader.applicationContext.revokeUriPermission(this.arg$1, 1);
    }
}
