package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda321 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ TLRPC$Chat f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda321(MessagesController messagesController, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
        this.f$2 = tLRPC$User;
        this.f$3 = tLRPC$Chat2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$deleteUserChannelHistory$106(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
