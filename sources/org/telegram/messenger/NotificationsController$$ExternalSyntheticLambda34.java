package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda34 implements RequestDelegate {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda34 INSTANCE = new NotificationsController$$ExternalSyntheticLambda34();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda34() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$40(tLObject, tL_error);
    }
}
