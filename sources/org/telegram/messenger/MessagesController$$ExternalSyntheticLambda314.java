package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda314 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda314(MessagesController messagesController, Runnable runnable, Runnable runnable2) {
        this.f$0 = messagesController;
        this.f$1 = runnable;
        this.f$2 = runnable2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$toggleChatJoinRequest$231(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
