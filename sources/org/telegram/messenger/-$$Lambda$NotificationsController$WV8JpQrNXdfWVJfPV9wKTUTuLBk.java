package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk INSTANCE = new -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk();

    private /* synthetic */ -$$Lambda$NotificationsController$WV8JpQrNXdfWVJfPV9wKTUTuLBk() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$37(tLObject, tL_error);
    }
}
