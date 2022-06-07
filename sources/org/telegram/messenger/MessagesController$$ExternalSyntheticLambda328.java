package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda328 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda328(MessagesController messagesController, TLRPC$User tLRPC$User, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$User;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadFullUser$51(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
