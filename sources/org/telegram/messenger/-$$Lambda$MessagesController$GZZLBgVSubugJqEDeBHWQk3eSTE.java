package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$GZZLBgVSubugJqEDeBHWQk3eSTE implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$GZZLBgVSubugJqEDeBHWQk3eSTE INSTANCE = new -$$Lambda$MessagesController$GZZLBgVSubugJqEDeBHWQk3eSTE();

    private /* synthetic */ -$$Lambda$MessagesController$GZZLBgVSubugJqEDeBHWQk3eSTE() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$140(tLObject, tL_error);
    }
}
