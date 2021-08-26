package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda284 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda284(MessagesController messagesController, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
        this.f$2 = tLRPC$User;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$unpinAllMessages$94(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
