package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda302 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda302(MessagesController messagesController, long j, String str) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$updateChannelUserName$240(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
