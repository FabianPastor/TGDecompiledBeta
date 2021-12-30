package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda279 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda279(MessagesController messagesController, long j, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getGroupCall$35(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
