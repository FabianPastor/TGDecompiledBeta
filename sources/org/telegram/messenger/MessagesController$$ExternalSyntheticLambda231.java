package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda231 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda231(MessagesController messagesController, String str) {
        this.f$0 = messagesController;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m362x1d313fb7(this.f$1, tLObject, tL_error);
    }
}
