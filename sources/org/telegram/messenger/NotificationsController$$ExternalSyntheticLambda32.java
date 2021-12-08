package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda32 implements RequestDelegate {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda32 INSTANCE = new NotificationsController$$ExternalSyntheticLambda32();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda32() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$39(tLObject, tL_error);
    }
}
