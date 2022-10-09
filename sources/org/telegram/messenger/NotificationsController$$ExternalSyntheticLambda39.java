package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda39 implements RequestDelegate {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda39 INSTANCE = new NotificationsController$$ExternalSyntheticLambda39();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda39() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$40(tLObject, tLRPC$TL_error);
    }
}
