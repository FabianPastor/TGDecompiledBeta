package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.NotificationsSoundActivity;

public final /* synthetic */ class NotificationsSoundActivity$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public static final /* synthetic */ NotificationsSoundActivity$1$$ExternalSyntheticLambda2 INSTANCE = new NotificationsSoundActivity$1$$ExternalSyntheticLambda2();

    private /* synthetic */ NotificationsSoundActivity$1$$ExternalSyntheticLambda2() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        NotificationsSoundActivity.AnonymousClass1.lambda$deleteSelectedMessages$2(tLObject, tL_error);
    }
}
