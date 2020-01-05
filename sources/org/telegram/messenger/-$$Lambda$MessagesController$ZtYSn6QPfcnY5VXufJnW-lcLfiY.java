package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ZtYSn6QPfcnY5VXufJnW-lcLfiY implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$MessagesController$ZtYSn6QPfcnY5VXufJnW-lcLfiY(MessagesController messagesController, int i, String str) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$updateChannelUserName$191$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
