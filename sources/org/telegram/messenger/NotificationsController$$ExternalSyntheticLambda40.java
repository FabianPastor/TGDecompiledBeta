package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda40 implements RequestDelegate {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda40 INSTANCE = new NotificationsController$$ExternalSyntheticLambda40();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda40() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$39(tLObject, tLRPC$TL_error);
    }
}
