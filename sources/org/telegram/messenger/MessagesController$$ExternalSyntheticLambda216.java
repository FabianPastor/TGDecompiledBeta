package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda216 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda216(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadHintDialogs$149(tLObject, tLRPC$TL_error);
    }
}