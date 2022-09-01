package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda270 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda270(MessagesController messagesController, int i, String str) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$registerForPush$258(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
