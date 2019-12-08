package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$A5JUENTv6Earg4PHsuQcPWssROs implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$A5JUENTv6Earg4PHsuQcPWssROs(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$updateTimerProc$94$MessagesController(tLObject, tL_error);
    }
}
