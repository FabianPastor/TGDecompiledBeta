package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsController$$Lambda$18 implements RequestDelegate {
    static final RequestDelegate $instance = new NotificationsController$$Lambda$18();

    private NotificationsController$$Lambda$18() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$32$NotificationsController(tLObject, tL_error);
    }
}
