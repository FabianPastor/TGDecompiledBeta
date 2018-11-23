package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsController$$Lambda$19 implements RequestDelegate {
    static final RequestDelegate $instance = new NotificationsController$$Lambda$19();

    private NotificationsController$$Lambda$19() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.m17x2e794ca2(tLObject, tL_error);
    }
}
