package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ewgb8hNy_OgbxLhXOtSvcjYuXpE implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$ewgb8hNy_OgbxLhXOtSvcjYuXpE INSTANCE = new -$$Lambda$MessagesController$ewgb8hNy_OgbxLhXOtSvcjYuXpE();

    private /* synthetic */ -$$Lambda$MessagesController$ewgb8hNy_OgbxLhXOtSvcjYuXpE() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$157(tLObject, tL_error);
    }
}
