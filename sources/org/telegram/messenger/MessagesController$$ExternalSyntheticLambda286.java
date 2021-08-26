package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda286 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda286(MessagesController messagesController, TLRPC$Chat tLRPC$Chat, boolean z, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkChatInviter$280(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
