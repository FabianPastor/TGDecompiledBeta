package org.telegram.messenger;

import android.net.Uri;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ Uri f$0;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda4(Uri uri) {
        this.f$0 = uri;
    }

    public final void run() {
        ApplicationLoader.applicationContext.revokeUriPermission(this.f$0, 1);
    }
}
