package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsController$$Lambda$17 implements RequestDelegate {
    static final RequestDelegate $instance = new NotificationsController$$Lambda$17();

    private NotificationsController$$Lambda$17() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.m10x2618b2a0(tLObject, tL_error);
    }
}
