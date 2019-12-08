package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$X7wxJ-DWqIG65O0cyyvXQX_huwY implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$X7wxJ-DWqIG65O0cyyvXQX_huwY INSTANCE = new -$$Lambda$MessagesController$X7wxJ-DWqIG65O0cyyvXQX_huwY();

    private /* synthetic */ -$$Lambda$MessagesController$X7wxJ-DWqIG65O0cyyvXQX_huwY() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$62(tLObject, tL_error);
    }
}
