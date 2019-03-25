package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsController$$Lambda$20 implements RequestDelegate {
    static final RequestDelegate $instance = new NotificationsController$$Lambda$20();

    private NotificationsController$$Lambda$20() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$34$NotificationsController(tLObject, tL_error);
    }
}
