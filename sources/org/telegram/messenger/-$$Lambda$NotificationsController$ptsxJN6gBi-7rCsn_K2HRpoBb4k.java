package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$ptsxJN6gBi-7rCsn_K2HRpoBb4k implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$NotificationsController$ptsxJN6gBi-7rCsn_K2HRpoBb4k INSTANCE = new -$$Lambda$NotificationsController$ptsxJN6gBi-7rCsn_K2HRpoBb4k();

    private /* synthetic */ -$$Lambda$NotificationsController$ptsxJN6gBi-7rCsn_K2HRpoBb4k() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$35(tLObject, tL_error);
    }
}
